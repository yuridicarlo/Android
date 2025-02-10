package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.adapter.ShopsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmMerchantListV2Binding;
import it.bancomatpay.sdkui.events.LocationUpdateRequestEvent;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsMerchantLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogMerchantLocation;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static it.bancomatpay.sdk.manager.utilities.Constants.DISPLACEMENT;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.POSITION_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_POSITION_CONSENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_MERCHANT_SELECTED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_LATITUDE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_LONGITUDE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_SEARCH_TEXT;

public class MerchantListActivity extends GenericErrorActivity implements InteractionListener {

	private ActivityBcmMerchantListV2Binding binding;

    private static final String TAG = MerchantListActivity.class.getSimpleName();

	private static final int REQUEST_CODE_LOCATION = 1000;
	private static final int REQUEST_CODE_RESOLUTION = 2000;

	// Location updates intervals in sec
	private static final int UPDATE_INTERVAL = 10000; // 10 sec
	private static final int FASTEST_INTERVAL = 5000; // 5 sec

	private ShopsAdapter shopsAdapter;
	List<ItemInterfaceConsumer> geoItems;

	protected BcmLocation mLastLocation;

	private LocationCallback googleLocationCallback;
	private com.huawei.hms.location.LocationCallback huaweiLocationCallback;
	private final HashMap<String, ShopsDataMerchant> shopsItemHashMap = new HashMap<>();

	private boolean isPermissionLocationServicesRequestedAlready;


	ActivityResultLauncher<Intent> activityResultLauncherPosition = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				Intent data = result.getData();
				manageResult(REQUEST_CODE_SHOW_POSITION_CONSENT,result.getResultCode(),data);
			});

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActivityName(MerchantListActivity.class.getSimpleName());
		binding = ActivityBcmMerchantListV2Binding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		//Prevent keyboard open
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
			huaweiLocationCallback = new com.huawei.hms.location.LocationCallback() {
				@Override
				public void onLocationResult(com.huawei.hms.location.LocationResult locationResult) {
					CustomLogger.d(TAG, "onLocationResult = " + locationResult.getLastLocation().toString());
					BcmLocation currentLocation = new BcmLocation();
					currentLocation.setLatitude(locationResult.getLastLocation().getLatitude());
					currentLocation.setLongitude(locationResult.getLastLocation().getLongitude());
					onLocationChanged(currentLocation);
				}
			};
		} else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
			googleLocationCallback = new LocationCallback() {
				@Override
				public void onLocationResult(LocationResult locationResult) {
					CustomLogger.d(TAG, "onLocationResult = " + locationResult.getLastLocation().toString());
					BcmLocation currentLocation = new BcmLocation();
					currentLocation.setLatitude(locationResult.getLastLocation().getLatitude());
					currentLocation.setLongitude(locationResult.getLastLocation().getLongitude());
					onLocationChanged(currentLocation);
				}
			};
		}

		binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

		LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(this);
		binding.recyclerViewMerchant.setLayoutManager(layoutManagerPanel);

		geoItems = new ArrayList<>();
		shopsAdapter = new ShopsAdapter(geoItems, this);
		binding.recyclerViewMerchant.setAdapter(shopsAdapter);

		binding.refresh.setOnRefreshListener(() -> {
			isPermissionLocationServicesRequestedAlready = false;
			doRequest();
		});
		binding.refresh.setColorSchemeColors(
				ContextCompat.getColor(this, R.color.colorAccentBancomat));

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {

				if (text.length() > 0) {
					if (binding.cancelButtonSearch.getVisibility() != View.VISIBLE) {
						AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, 250);
					}
				} else {
					AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);

					ArrayList<ShopItem> list = ApplicationModel.getInstance().getShopItems();
					if (list != null && !list.isEmpty()) {
						manageGeoShopItems(list);
					} else {
						EventBus.getDefault().post(new LocationUpdateRequestEvent());
						showEmptyResult();
					}
				}

				if (TextUtils.isEmpty(text) || text.length() < 3) {
					if (shopsAdapter != null) {
						shopsAdapter.getFilter().filter(text);
					}
				} else {
					binding.merchantListEmpty.setVisibility(View.GONE);
					binding.refresh.setRefreshing(true);
					BancomatSdkInterface.Factory.getInstance().doGetShopByMerchantNameList(MerchantListActivity.this, result -> {
						if (result != null) {
							if (result.isSuccess()) {
								manageSearchShopItems(result.getResult());
							} else if (result.getStatusCode() == StatusCode.Server.P2B_SEARCH_SHOP_FAILED) {
								showEmptyResult();
							} else if (result.isSessionExpired()) {
								BCMAbortCallback.getInstance().getAuthenticationListener()
										.onAbortSession(MerchantListActivity.this, BCMAbortCallback.getInstance().getSessionRefreshListener());
							} else {
								showError(result.getStatusCode());
								showEmptyResult();
							}
						}
						binding.refresh.setRefreshing(false);
						binding.merchantListEmpty.setVisibility(View.VISIBLE);
					}, text.toString(), mLastLocation, SessionManager.getInstance().getSessionToken());
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		binding.searchShopEditText.addTextChangedListener(textWatcher);

		binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
			binding.searchShopEditText.getText().clear();
			AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);

			ArrayList<ShopItem> list = ApplicationModel.getInstance().getShopItems();
			if (list != null && !list.isEmpty()) {
				manageGeoShopItems(list);
			} else {
				EventBus.getDefault().post(new LocationUpdateRequestEvent());
			}
		}));

		isPermissionLocationServicesRequestedAlready = false;

		doRequest();

	}

	@Override
	protected void onDestroy() {
		BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
		super.onDestroy();
	}

	private void doRequest() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			EventBus.getDefault().post(new LocationUpdateRequestEvent());
			binding.merchantListEmpty.setVisibility(View.GONE);
			binding.refresh.setRefreshing(true);
			BancomatSdkInterface.Factory.getInstance().getShopList(this, result -> {
				if (result != null) {
					if (result.isSuccess()) {
						manageGeoShopItems(result.getResult());
					} else if (result.isSessionExpired()) {
						BCMAbortCallback.getInstance().getAuthenticationListener()
								.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
					} else {
						showError(result.getStatusCode());
						showEmptyResult();
					}
				} else {
					showEmptyResult();
				}

				binding.refresh.setRefreshing(false);
				binding.merchantListEmpty.setVisibility(View.VISIBLE);

			}, mLastLocation, SessionManager.getInstance().getSessionToken());

		} else {
			if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(POSITION_DISCLOSURE)){
				PermissionFlowManager.goToPositionDisclosure(this, activityResultLauncherPosition);
			}
			else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
			}
		}
	}

    /*@Override
    public void onStart() {
        super.onStart();
        startLocationUpdates();
    }*/

	@Override
	public void onStop() {
		super.onStop();
		if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
			com.huawei.hms.location.LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(huaweiLocationCallback);
		} else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
			LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(googleLocationCallback);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ArrayList<ShopItem> list = ApplicationModel.getInstance().getShopItems();
		if (list != null && !list.isEmpty()) {
			manageGeoShopItems(list);
		} else {
			EventBus.getDefault().post(new LocationUpdateRequestEvent());
		}
		binding.cancelButtonSearch.performClick();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_LOCATION) {
			if (grantResults.length > 0 && grantResults[grantResults.length - 1] == PackageManager.PERMISSION_GRANTED) {
				EventBus.getDefault().post(new LocationUpdateRequestEvent());
				binding.refresh.setRefreshing(true);
				BancomatSdkInterface.Factory.getInstance().getShopList(this, result -> {
					if (result != null) {
						if (result.isSuccess()) {
							manageGeoShopItems(result.getResult());
						} else if (result.isSessionExpired()) {
							BCMAbortCallback.getInstance().getAuthenticationListener()
									.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
						} else {
							showError(result.getStatusCode());
							showEmptyResult();
						}
					} else {
						showEmptyResult();
					}

					binding.refresh.setRefreshing(false);

				}, mLastLocation, SessionManager.getInstance().getSessionToken());
			} else {
				showEmptyResult();

				HashMap<String, String> mapEventParams = new HashMap<>();
				mapEventParams.put(PARAM_PERMISSION, "Location");
				CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
			}
		}
	}


	private synchronized void manageGeoShopItems(ArrayList<ShopItem> shopItems) {
		boolean isEmptyList = shopsAdapter.getItemCount() == 0;

		geoItems = getItems(shopItems);
		shopsAdapter = new ShopsAdapter(geoItems, this);
		binding.recyclerViewMerchant.setAdapter(shopsAdapter);
		if (isEmptyList) {
			AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewMerchant);
		}

		binding.refresh.setRefreshing(false);

		if (geoItems.isEmpty()) {
			showEmptyResult();
		} else {
			showResult();
		}
	}

	synchronized private void manageSearchShopItems(ArrayList<ShopItem> shopItems) {
		List<ItemInterfaceConsumer> items = getItems(shopItems);
		shopsAdapter = new ShopsAdapter(items, this);
		binding.recyclerViewMerchant.setAdapter(shopsAdapter);
		CustomLogger.d(TAG, "manageSearchShopItems mSwipeRefreshLayout: " + binding.refresh.isEnabled());

		binding.refresh.setRefreshing(false);

		if (items.isEmpty()) {
			showEmptyResult();
		} else {
			showResult();
		}
	}

	private List<ItemInterfaceConsumer> getItems(ArrayList<ShopItem> shopItems) {
		List<ItemInterfaceConsumer> items = new ArrayList<>();
		if (shopItems != null) {
			for (ShopItem shopItem : shopItems) {
				items.add(new ShopsDataMerchant(shopItem));
			}
		}
		return items;
	}

	private void showEmptyResult() {
		binding.refresh.setEnabled(true);
		binding.refresh.setRefreshing(false);
		binding.merchantListEmpty.setVisibility(View.VISIBLE);

		binding.recyclerViewMerchant.setVisibility(View.GONE);
	}

	private void showResult() {
		binding.refresh.setRefreshing(false);
		binding.merchantListEmpty.setVisibility(View.GONE);
		binding.recyclerViewMerchant.setVisibility(View.VISIBLE);
	}

	private void startGoogleLocationUpdates() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			CustomLogger.d(TAG, "startLocationUpdates");

			LocationRequest mLocationRequest = new LocationRequest();
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

			LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
					.addLocationRequest(mLocationRequest);

			builder.setAlwaysShow(true); //this is the key ingredient

			SettingsClient settingsClient = LocationServices.getSettingsClient(this);
			Task<LocationSettingsResponse> result = settingsClient.checkLocationSettings(builder.build());
			result.addOnCompleteListener(task -> {
				try {
					if (!isPermissionLocationServicesRequestedAlready) {
						isPermissionLocationServicesRequestedAlready = true;
						binding.refresh.setRefreshing(true);

						LocationSettingsResponse response = task.getResult(ApiException.class);
						if (response != null) {
							if (response.getLocationSettingsStates().isLocationUsable() &&
									(ApplicationModel.getInstance().getShopItems() != null || !BancomatSdk.getInstance().isDeviceOnline(this))) {
								binding.refresh.setRefreshing(false);
							}
						}
						CustomLogger.d(TAG, "LocationSettingsResponse = " + (response != null ? response.toString() : "null"));
					}
					// All location settings are satisfied. The client can initialize location
					// requests here.
                    /*gps = new Permission(Permission.Type.GPS, true);
                    EventBus.getDefault().post(gps);*/
					//startLocationUpdates();
				} catch (ApiException exception) {
					switch (exception.getStatusCode()) {
						case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
							// Location settings are not satisfied. But could be fixed by showing the
							// user a dialog.
							try {
								// Cast to a resolvable exception.
								ResolvableApiException resolvable = (ResolvableApiException) exception;
								// Show the dialog by calling startResolutionForResult(),
								// and check the result in onActivityResult().
								resolvable.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
							} catch (IntentSender.SendIntentException | ClassCastException e) {
								// Ignore the error.
							}
							break;
						case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
							// Location settings are not satisfied. However, we have no way to fix the
							// settings so we won't show the dialog.
							break;
					}
				}
			});

			FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
			locationClient.getLastLocation()
					.addOnSuccessListener(new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							// GPS location can be null if GPS is switched off
							if (location != null) {
								BcmLocation currentLocation = new BcmLocation();
								currentLocation.setLatitude(location.getLatitude());
								currentLocation.setLongitude(location.getLongitude());
								onLocationChanged(currentLocation);
								CustomLogger.d(TAG, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
							} else {

								//questo e' un bel workaround... nel caso in cui la lastLocation venga tornata null (statisticamente) le fusedlocation ci mettono molto a rispondere...
								//facendo una richiesta al vecchio locationManager la cosa si sblocca

								if (ActivityCompat.checkSelfPermission(MerchantListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
										&& ActivityCompat.checkSelfPermission(MerchantListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

									LocationManager locationManager = (LocationManager) MerchantListActivity.this.getSystemService(LOCATION_SERVICE);
									if (locationManager != null) {
										locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FASTEST_INTERVAL, 0, new android.location.LocationListener() {
											@Override
											public void onLocationChanged(Location location) {
												CustomLogger.d(TAG, "NETWORK_PROVIDER onLocationChanged = " + location);
												locationManager.removeUpdates(this);
											}

											@Override
											public void onStatusChanged(String provider, int status, Bundle extras) {
											}

											@Override
											public void onProviderEnabled(String provider) {
											}

											@Override
											public void onProviderDisabled(String provider) {
											}
										});
									}

								}

							}
						}
					})
					.addOnFailureListener(e ->
							CustomLogger.d(TAG, "Error trying to get last GPS location: " + e.getMessage()));

			LocationServices.getFusedLocationProviderClient(this)
					.requestLocationUpdates(mLocationRequest, googleLocationCallback, Looper.myLooper());

		}

	}

	private void startHuaweiLocationUpdates() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			CustomLogger.d(TAG, "startLocationUpdates");

			com.huawei.hms.location.LocationRequest mLocationRequest = new com.huawei.hms.location.LocationRequest();
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

			com.huawei.hms.location.LocationSettingsRequest.Builder builder = new com.huawei.hms.location.LocationSettingsRequest.Builder()
					.addLocationRequest(mLocationRequest);

			builder.setAlwaysShow(true); //this is the key ingredient

			com.huawei.hms.location.SettingsClient settingsClient = com.huawei.hms.location.LocationServices.getSettingsClient(this);
			com.huawei.hmf.tasks.Task<com.huawei.hms.location.LocationSettingsResponse> result = settingsClient.checkLocationSettings(builder.build());
			result.addOnCompleteListener(task -> {
				try {
					if (!isPermissionLocationServicesRequestedAlready) {
						isPermissionLocationServicesRequestedAlready = true;
						binding.refresh.setRefreshing(true);

						com.huawei.hms.location.LocationSettingsResponse response = task.getResultThrowException(com.huawei.hms.common.ApiException.class);
						if (response != null) {
							if (response.getLocationSettingsStates().isLocationUsable() &&
									(ApplicationModel.getInstance().getShopItems() != null || !BancomatSdk.getInstance().isDeviceOnline(this))) {
								binding.refresh.setRefreshing(false);
							}
						}
						CustomLogger.d(TAG, "LocationSettingsResponse = " + (response != null ? response.toString() : "null"));
					}
				}catch (com.huawei.hms.common.ApiException exception){
					switch (exception.getStatusCode()) {
						case com.huawei.hms.location.LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
							// Location settings are not satisfied. But could be fixed by showing the
							// user a dialog.
							try {
								// Cast to a resolvable exception.
								com.huawei.hms.common.ResolvableApiException resolvable = (com.huawei.hms.common.ResolvableApiException) exception;

								// Show the dialog by calling startResolutionForResult(),
								// and check the result in onActivityResult().
								resolvable.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
							} catch (IntentSender.SendIntentException | ClassCastException e) {
								// Ignore the error.
							}
							break;
						case com.huawei.hms.location.LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
							// Location settings are not satisfied. However, we have no way to fix the
							// settings so we won't show the dialog.
							break;
				}

				// All location settings are satisfied. The client can initialize location
				// requests here.
                    /*gps = new Permission(Permission.Type.GPS, true);
                    EventBus.getDefault().post(gps);*/
				//startLocationUpdates();
				}});

			com.huawei.hms.location.FusedLocationProviderClient locationClient =
					com.huawei.hms.location.LocationServices.getFusedLocationProviderClient(this);
			locationClient.getLastLocation()
					.addOnSuccessListener(new com.huawei.hmf.tasks.OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							// GPS location can be null if GPS is switched off
							if (location != null) {
								BcmLocation currentLocation = new BcmLocation();
								currentLocation.setLatitude(location.getLatitude());
								currentLocation.setLongitude(location.getLongitude());
								onLocationChanged(currentLocation);
								CustomLogger.d(TAG, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
							} else {

								//questo e' un bel workaround... nel caso in cui la lastLocation venga tornata null (statisticamente) le fusedlocation ci mettono molto a rispondere...
								//facendo una richiesta al vecchio locationManager la cosa si sblocca

								if (ActivityCompat.checkSelfPermission(MerchantListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
										&& ActivityCompat.checkSelfPermission(MerchantListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

									LocationManager locationManager = (LocationManager) MerchantListActivity.this.getSystemService(LOCATION_SERVICE);
									if (locationManager != null) {
										locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FASTEST_INTERVAL, 0, new android.location.LocationListener() {
											@Override
											public void onLocationChanged(Location location) {
												CustomLogger.d(TAG, "NETWORK_PROVIDER onLocationChanged = " + location);
												locationManager.removeUpdates(this);
											}

											@Override
											public void onStatusChanged(String provider, int status, Bundle extras) {
											}

											@Override
											public void onProviderEnabled(String provider) {
											}

											@Override
											public void onProviderDisabled(String provider) {
											}
										});
									}

								}

							}
						}
					})
					.addOnFailureListener(e ->
							CustomLogger.d(TAG, "Error trying to get last GPS location: " + e.getMessage()));

			com.huawei.hms.location.LocationServices.getFusedLocationProviderClient(this)
					.requestLocationUpdates(mLocationRequest, huaweiLocationCallback, Looper.myLooper());

		}

	}

	public void onLocationChanged(BcmLocation location) {
		CustomLogger.d(TAG, "onLocationChanged = " + location);
		if (mLastLocation == null) {
			BancomatSdkInterface.Factory.getInstance().getShopList(this, result -> {
				if (result != null) {
					if (result.isSuccess()) {
						manageGeoShopItems(result.getResult());
					} else if (result.isSessionExpired()) {
						BCMAbortCallback.getInstance().getAuthenticationListener()
								.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
					} else {
						showError(result.getStatusCode());
						showEmptyResult();
					}
				}
			}, location, SessionManager.getInstance().getSessionToken());
		}
		mLastLocation = location;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(LocationUpdateRequestEvent event) {
		CustomLogger.d(TAG, "LocationUpdateRequestEvent received");
		if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
			startHuaweiLocationUpdates();
		} else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
			startGoogleLocationUpdates();
		}
	}

	@Override
	public void onConsumerInteraction(ItemInterfaceConsumer item) {
		//Non usata
	}

	@Override
	public void onMerchantInteraction(ItemInterfaceConsumer item) {
		hideKeyboard();

		CjUtils.getInstance().startP2BPaymentFlow();

		HashMap<String, String> mapEventParams = new HashMap<>();
		if (!binding.searchShopEditText.getText().toString().isEmpty()) {
			mapEventParams.put(PARAM_SEARCH_TEXT, binding.searchShopEditText.getText().toString());
		} else if (mLastLocation != null) {
			mapEventParams.put(PARAM_LATITUDE, String.valueOf(mLastLocation.getLatitude()));
			mapEventParams.put(PARAM_LONGITUDE, String.valueOf(mLastLocation.getLongitude()));
		}
		CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_MERCHANT_SELECTED, mapEventParams, true);

		if (((ShopItem) item.getItemInterface()).getMerchantType() == ShopItem.EMerchantType.STANDARD) {
			PaymentFlowManager.goToInsertAmount(this, item, false, PaymentContactFlowType.SEND, false);
		} else if (((ShopItem) item.getItemInterface()).getMerchantType() == ShopItem.EMerchantType.PREAUTHORIZED) {
			HomeFlowManager.goToPetrol(this, (ShopItem) item.getItemInterface());
		}
	}

	@Override
	public void onImageConsumerInteraction(ItemInterfaceConsumer item) {
		//Non usata
	}

	@Override
	public void onImageMerchantInteraction(ItemInterfaceConsumer item) {
		hideKeyboard();
		if (item instanceof ShopsDataMerchant) {
			if (shopsItemHashMap.containsKey(item.getPhoneNumber())) {

				ShopsDataMerchant merchantItem = shopsItemHashMap.get(item.getPhoneNumber());
				showBottomDialog(merchantItem);
			} else {

				LoaderHelper.showLoader(this);

				BancomatSdkInterface.Factory.getInstance().doGetMerchantDetail(this, result -> {
					if (result != null) {
						if (result.isSuccess()) {
							final ShopsDataMerchant merchantItem = new ShopsDataMerchant(result.getResult());
							shopsItemHashMap.put(item.getPhoneNumber(), merchantItem);
							showBottomDialog(merchantItem);
						} else if (result.isSessionExpired()) {
							BCMAbortCallback.getInstance().getAuthenticationListener()
									.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
						} else {
							showError(result.getStatusCode());
						}
					}
				}, item.getPhoneNumber(), SessionManager.getInstance().getSessionToken());
			}

		}
	}

	private void showBottomDialog(ShopsDataMerchant merchantItem) {
		if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
			BottomDialogHmsMerchantLocation hmsBottomDialog = new BottomDialogHmsMerchantLocation(this, merchantItem);
			if (!hmsBottomDialog.isVisible()) {
				hmsBottomDialog.showDialog();
			}
		} else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
			BottomDialogMerchantLocation bottomDialog = new BottomDialogMerchantLocation(this, merchantItem);
			if (!bottomDialog.isVisible()) {
				bottomDialog.showDialog();
			}
		}
	}

	private void hideKeyboard() {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		}
	}

	protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == REQUEST_CODE_RESOLUTION) {
			if (resultCode == RESULT_CANCELED) {
				binding.refresh.setRefreshing(false);
				showEmptyResult();
			}
		} else if (requestCode == REQUEST_CODE_SHOW_POSITION_CONSENT){
			if(resultCode == RESULT_OK){
				EventBus.getDefault().post(new LocationUpdateRequestEvent());
				binding.refresh.setRefreshing(true);
				BancomatSdkInterface.Factory.getInstance().getShopList(this, result -> {
					if (result != null) {
						if (result.isSuccess()) {
							manageGeoShopItems(result.getResult());
						} else if (result.isSessionExpired()) {
							BCMAbortCallback.getInstance().getAuthenticationListener()
									.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
						} else {
							showError(result.getStatusCode());
							showEmptyResult();
						}
					} else {
						showEmptyResult();
					}

					binding.refresh.setRefreshing(false);

				}, mLastLocation, SessionManager.getInstance().getSessionToken());
			}else{
				showEmptyResult();
			}
		}
	}
}