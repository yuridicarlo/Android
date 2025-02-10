package it.bancomat.pay.consumer.storeLocator.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.UiSettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.viewmodel.HomeViewModel;
import it.bancomat.pay.consumer.viewmodel.StoreLocatorViewModel;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomat.pay.consumer.storeLocator.adapters.MerchantMapListAdapter;
import it.bancomatpay.sdkui.databinding.FragmentMerchantMapBinding;
import it.bancomatpay.sdkui.events.LocationUpdateRequestEvent;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.CjConstants;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.NavHelper;

public class MerchantMapFragment extends GenericErrorFragment implements MerchantMapListAdapter.InteractionListener, OnMapReadyCallback {
    private static final String TAG = MerchantMapFragment.class.getSimpleName();
    private FragmentMerchantMapBinding binding;

    private HomeViewModel homeViewModel;

    private static final int REQUEST_CODE_LOCATION = 1000;
    private static final int REQUEST_CODE_RESOLUTION = 2000;
    // Location updates intervals in sec
    private static final int UPDATE_INTERVAL = 10000; // 10 sec
    private static final int FASTEST_INTERVAL = 5000; // 5 sec
    private final float ZOOM_LEVEL = 15.5F; //empirical 15.5=800 meters 16F=500 meters +1F->/=2.5 meters, -1F->*=2.5 meters
    protected BcmLocation mLastLocation;
    private LocationCallback googleLocationCallback;
    private com.huawei.hms.location.LocationCallback huaweiLocationCallback;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private com.huawei.hms.maps.model.CameraPosition hCameraPosition;
    private HuaweiMap hMap;

    private MapView mMapView;
    private MerchantMapListAdapter adapter;
    private final int SEARCH_AGAIN_DISTANCE = 400;          //in meters.

    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<com.huawei.hms.maps.model.Marker> hMarkers = new ArrayList<>();

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private Marker selectedMarker;
    private com.huawei.hms.maps.model.Marker selectedHMarker;

    private StoreLocatorViewModel storeLocatorViewModel;

    private boolean wasLastSearchDistant = false;

    ActivityResultLauncher<Intent> activityResultLauncherPosition = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                mapFragment.getMapAsync(this::onGetMapAsyncResult);

//                manageResult(REQUEST_CODE_SHOW_POSITION_CONSENT,result.getResultCode(),data);
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMerchantMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        storeLocatorViewModel = new ViewModelProvider(requireActivity()).get(StoreLocatorViewModel.class);

        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            binding.mapView.setVisibility(View.VISIBLE);
            binding.coverView.setVisibility(View.VISIBLE);
            setUpMapHuawei(savedInstanceState);
            homeViewModel.isCameraSheetExpanded().observe(requireActivity(), isExpanded -> {
                if(mMapView != null) {
                    if (isExpanded) {
                        mMapView.setVisibility(View.GONE);
                    } else {
                        mMapView.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            binding.mapView.setVisibility(View.GONE);
            binding.coverView.setVisibility(View.GONE);
            setUpMap();
        }

        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), it.bancomatpay.sdkui.R.color.colorAccentBancomat));
        binding.refresh.setOnRefreshListener(() -> {
            doRequest(wasLastSearchDistant);
        });
        binding.storeSearchBtn.setEnabled(false);
        binding.storeSearchBtn.setOnClickListener(v -> {
            NavHelper.navigate(requireActivity(), StoreLocatorListFragmentDirections.actionStoreLocatorListFragmentToPhysicalShopListFragment(mLastLocation));
        });

        binding.storeCenterBtn.setEnabled(false);
        binding.storeCenterBtn.setOnClickListener(v -> {
            if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
                com.huawei.hms.maps.model.LatLng latLng = new com.huawei.hms.maps.model.LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                hMap.animateCamera(com.huawei.hms.maps.CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
                hCameraPosition = new com.huawei.hms.maps.model.CameraPosition(latLng, hCameraPosition.zoom, hCameraPosition.tilt, hCameraPosition.bearing);
            } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
                cameraPosition = new CameraPosition(latLng, cameraPosition.zoom, cameraPosition.tilt, cameraPosition.bearing);
            }
            binding.searchAgainBtn.setVisibility(View.INVISIBLE);
            doRequest(false);
        });

        binding.storesLayout.setElevation(60);
        binding.searchAgainBtn.setOnClickListener(v -> {
            binding.searchAgainBtn.setVisibility(View.INVISIBLE);
            doRequest(true);
        });

        if (!storeLocatorViewModel.isPreActivation()) {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(requireActivity(), CjConstants.KEY_STORE_LOCATOR, null, true);
        }
    }

    private void setPageSpacing(Boolean divideEqually){
        ConstraintLayout.LayoutParams map_lp = (ConstraintLayout.LayoutParams) binding.mapLayout.getLayoutParams();
        if(divideEqually){
            map_lp.matchConstraintPercentHeight = (float) 0.55;
            binding.guidelineStoresStart.setGuidelinePercent((float) 0.5);
            binding.noShopsTv.setVisibility(View.GONE);
            binding.recyclerViewShops.setVisibility(View.VISIBLE);
        }else {
            map_lp.matchConstraintPercentHeight = (float) 0.80;
            binding.guidelineStoresStart.setGuidelinePercent((float) 0.75);
            binding.noShopsTv.setVisibility(View.VISIBLE);
            binding.recyclerViewShops.setVisibility(View.GONE);
        }
        binding.mapLayout.setLayoutParams(map_lp);
    }

    void setAdapter(ShopList shopList) {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.recyclerViewShops.setLayoutManager(layoutManagerPanel);

        setPageSpacing(shopList.getShops().size() != 0);
        adapter = new MerchantMapListAdapter(shopList.getShops(), this, requireContext(), mLastLocation);
        binding.recyclerViewShops.setAdapter(adapter);
    }

    void setRefreshing(boolean isRefreshing) {
        binding.refresh.setRefreshing(isRefreshing);

        if(isRefreshing) {
            binding.noShopsTv.setVisibility(View.GONE);
            binding.refresh.setVisibility(View.VISIBLE);
        }
    }

    public void onLocationChanged(BcmLocation location) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            com.huawei.hms.maps.model.LatLng latLng = new com.huawei.hms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
            hMap.moveCamera(com.huawei.hms.maps.CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
            hCameraPosition = hMap.getCameraPosition();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
            cameraPosition = mMap.getCameraPosition();
        }
        binding.storeSearchBtn.setEnabled(true);
        binding.storeCenterBtn.setEnabled(true);
        mLastLocation = location;
        doRequest(false);
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_static_map);

        mapFragment.getMapAsync(this::onGetMapAsyncResult);
    }

    private void setUpMapHuawei(Bundle savedInstanceState) {
        mMapView = binding.mapView;
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        MapsInitializer.initialize(requireContext());
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void onGetMapAsyncResult(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, 0, 0, 65);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(100);
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


            LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(mLocationRequest, googleLocationCallback, Looper.myLooper());

        }
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        mMap.setOnCameraIdleListener(() -> {
            // Calculate the distance between the two positions and set it to results[0]
            if (mMap != null && cameraPosition != null) {
                LatLng cameraLatLng = mMap.getCameraPosition().target;

                // search again button
                float[] results = new float[3];
                android.location.Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, cameraPosition.target.latitude, cameraPosition.target.longitude, results);
                if (results[0] > SEARCH_AGAIN_DISTANCE) {
                    cameraPosition = mMap.getCameraPosition();
                    binding.searchAgainBtn.setVisibility(View.VISIBLE);
                }

                //center camera button
                Location.distanceBetween(
                        mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                        cameraLatLng.latitude, cameraLatLng.longitude, results);
                if (results[0] > SEARCH_AGAIN_DISTANCE) {
                    binding.storeCenterBtn.setVisibility(View.VISIBLE);
                } else {
                    binding.storeCenterBtn.setVisibility(View.GONE);
                }

            }
        });
    }


    private void doRequest(boolean isDistantSearch) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wasLastSearchDistant = isDistantSearch;
            setRefreshing(true);
            EventBus.getDefault().post(new LocationUpdateRequestEvent());

//            BcmLocation fakelocation = new BcmLocation();
//            fakelocation.setLatitude(45.10049);
//            fakelocation.setLongitude(7.66682);
            BcmLocation searchLocation = new BcmLocation();
            searchLocation.setLatitude(mLastLocation.getLatitude());
            searchLocation.setLongitude(mLastLocation.getLongitude());

            if (isDistantSearch) {
                if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
                    searchLocation.setLongitude(hMap.getCameraPosition().target.longitude);
                    searchLocation.setLatitude(hMap.getCameraPosition().target.latitude);
                } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
                    searchLocation.setLongitude(mMap.getCameraPosition().target.longitude);
                    searchLocation.setLatitude(mMap.getCameraPosition().target.latitude);
                }
            }
            BancomatPayApiInterface.Factory.getInstance().doGetStoreLocator(requireActivity(), result -> {
                setRefreshing(false);
                if (result != null) {
                    if (result.isSuccess()) {
                        manageGeoShopItems(result.getResult());
                    } else if (result.isSessionExpired()) {
                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                .onAbortSession(requireActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
                    } else {
                        showError(result.getStatusCode());
                    }
                }

            }, searchLocation, 1, SessionManager.getInstance().getSessionToken());
        }
    }


    private synchronized void manageGeoShopItems(ShopList shopItems) {
        setAdapter(shopItems);

        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            com.huawei.hms.maps.model.BitmapDescriptor customHMarkerIcon = com.huawei.hms.maps.model.BitmapDescriptorFactory.fromResource(it.bancomatpay.sdkui.R.drawable.ico_pin);
            if(selectedHMarker != null) {
                selectedHMarker = null;
            }
            hMarkers = new ArrayList<>();
            hMap.clear();
            for(ShopItem item : shopItems.getShops()) {
                com.huawei.hms.maps.model.LatLng markerPosition = new com.huawei.hms.maps.model.LatLng(item.getLatitude(), item.getLongitude());

                com.huawei.hms.maps.model.MarkerOptions markerOptions = new com.huawei.hms.maps.model.MarkerOptions()
                        .position(markerPosition)
                        .title(item.getName())
                        .icon(customHMarkerIcon);

                com.huawei.hms.maps.model.Marker marker = hMap.addMarker(markerOptions);
                marker.setTag(item.getName());

                hMarkers.add(marker);
            }

            hMap.setOnMarkerClickListener(clickedMarker -> {
                onMarkerSelected(clickedMarker);

                return false;
            });

            hMap.setOnMapClickListener(latLng -> {
                if (selectedHMarker != null) {
                    selectedHMarker.setIcon(customHMarkerIcon);
                    selectedHMarker = null;
                }
            });

        } else {
            BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ico_pin);
            if(selectedMarker != null) {
                selectedMarker = null;
            }
            markers = new ArrayList<>();
            mMap.clear();
            for(ShopItem item : shopItems.getShops()) {
                LatLng markerPosition = new LatLng(item.getLatitude(), item.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(markerPosition)
                        .title(item.getName())
                        .icon(customMarkerIcon);

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(item.getName());

                markers.add(marker);
            }

            mMap.setOnMarkerClickListener(clickedMarker -> {
                onMarkerSelected(clickedMarker);

                return false;
            });

            mMap.setOnMapClickListener(latLng -> {
                if (selectedMarker != null) {
                    selectedMarker.setIcon(customMarkerIcon);
                    selectedMarker = null;
                }
            });
        }
    }

    private void onMarkerSelected(Marker clickedMarker) {
        BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ico_pin);
        BitmapDescriptor customMarkerIconSelected = BitmapDescriptorFactory.fromResource(R.drawable.ico_pin_selected);
        if (selectedMarker != null) {
            if(clickedMarker.getTag() != null && clickedMarker.getTag().equals(selectedMarker.getTag())) {
                return;
            }
            selectedMarker.setIcon(customMarkerIcon);
        }

        clickedMarker.setIcon(customMarkerIconSelected);
        ShopItem correspondingItem = adapter.bubbleItemUp(clickedMarker.getTag());
        if(correspondingItem != null) {
            sendCJ(correspondingItem.getName());
        }

        selectedMarker = clickedMarker;
    }

    private void onMarkerSelected(com.huawei.hms.maps.model.Marker clickedMarker) {
        com.huawei.hms.maps.model.BitmapDescriptor customHMarkerIcon = com.huawei.hms.maps.model.BitmapDescriptorFactory.fromResource(it.bancomatpay.sdkui.R.drawable.ico_pin);
        com.huawei.hms.maps.model.BitmapDescriptor customHMarkerIconSelected = com.huawei.hms.maps.model.BitmapDescriptorFactory.fromResource(it.bancomatpay.sdkui.R.drawable.ico_pin_selected);
        if (selectedHMarker != null) {
            if(clickedMarker.getTag().equals(selectedHMarker.getTag())) {
                return;
            }
            selectedHMarker.setIcon(customHMarkerIcon);
        }

        clickedMarker.setIcon(customHMarkerIconSelected);
        ShopItem correspondingItem = adapter.bubbleItemUp(clickedMarker.getTag());
        if(correspondingItem != null) {
            sendCJ(correspondingItem.getName());
        }

        selectedHMarker = clickedMarker;
    }

    private void sendCJ(String insignia) {
        if(!storeLocatorViewModel.isPreActivation()) {
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(CjConstants.PARAM_NOME_INSEGNA, insignia);
            CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), CjConstants.KEY_STORE_LOCATOR, mapEventParams, true);
        }
    }


    @Override
    public void onTap(ShopItem item) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            com.huawei.hms.maps.model.LatLng position = new com.huawei.hms.maps.model.LatLng(item.getLatitude(), item.getLongitude());
            hMap.animateCamera(com.huawei.hms.maps.CameraUpdateFactory.newLatLng(position));
            for(com.huawei.hms.maps.model.Marker marker : hMarkers) {
                if(marker.getTag() != null && item.getName().equals(marker.getTag())){
                    marker.showInfoWindow();
                    onMarkerSelected(marker);
                    return;
                }
            }
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            for(Marker marker : markers) {
                if(marker.getTag() != null && item.getName().equals(marker.getTag())){
                    marker.showInfoWindow();
                    onMarkerSelected(marker);
                    return;
                }
            }
        }

    }

    @Override
    public void onDirectionsTap(ShopItem item) {
        boolean petalMapsInstalled = isPackageInstalled("com.huawei.maps.app", requireContext().getPackageManager());
        boolean googleMapsInstalled = isPackageInstalled("com.google.android.apps.maps", requireContext().getPackageManager());

        if (BancomatFullStackSdk.getInstance().hasHuaweiServices() && petalMapsInstalled) {
            com.huawei.hms.maps.model.LatLng storeLocation = new com.huawei.hms.maps.model.LatLng(item.getLatitude(), item.getLongitude());
            String uri = "petalmaps://navigation?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + storeLocation.latitude + "," + storeLocation.longitude;
            Uri gmmIntentUri = Uri.parse(uri/*"geo:0,0?q="*/);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            requireContext().startActivity(mapIntent);
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices() && googleMapsInstalled) {
            LatLng storeLocation = new LatLng(item.getLatitude(), item.getLongitude());
            String uri = "http://maps.google.com/maps?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + storeLocation.latitude + "," + storeLocation.longitude;
            Uri gmmIntentUri = Uri.parse(uri/*"geo:0,0?q="*/);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            requireContext().startActivity(mapIntent);
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hMap.setMyLocationEnabled(true);
            UiSettings uiController = hMap.getUiSettings();
            uiController.setMyLocationButtonEnabled(false);
            uiController.setZoomControlsEnabled(false);
            uiController.setCompassEnabled(false);

            com.huawei.hms.location.LocationRequest mLocationRequest = new com.huawei.hms.location.LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(100);

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
            com.huawei.hms.location.LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(mLocationRequest, huaweiLocationCallback, Looper.myLooper());
        }
        hMap.getUiSettings().setMapToolbarEnabled(false);
        hMap.getUiSettings().setAllGesturesEnabled(true);

        hMap.setOnCameraIdleListener(() -> {
            // Calculate the distance between the two positions and set it to results[0]
            if (hMap != null && hCameraPosition != null) {
                com.huawei.hms.maps.model.LatLng cameraLatLng = hMap.getCameraPosition().target;

                // search again button
                float[] results = new float[3];
                Location.distanceBetween(
                        cameraLatLng.latitude, cameraLatLng.longitude,
                        hCameraPosition.target.latitude, hCameraPosition.target.longitude, results);

                if (results[0] > SEARCH_AGAIN_DISTANCE) {
                    hCameraPosition = hMap.getCameraPosition();
                    binding.searchAgainBtn.setVisibility(View.VISIBLE);
                }

                //center camera button
                Location.distanceBetween(
                        mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                        hCameraPosition.target.latitude, hCameraPosition.target.longitude, results);
                if (results[0] > SEARCH_AGAIN_DISTANCE) {
                    binding.storeCenterBtn.setVisibility(View.VISIBLE);
                } else {
                    binding.storeCenterBtn.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            mMapView.onStart();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            mapFragment.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices() && huaweiLocationCallback != null) {
            mMapView.onStop();
            com.huawei.hms.location.LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(huaweiLocationCallback);
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices() && googleLocationCallback != null) {
            mapFragment.onStop();
            LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(googleLocationCallback);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            mMapView.onDestroy();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            mapFragment.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            mMapView.onPause();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            mapFragment.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<ShopItem> list = ApplicationModel.getInstance().getShopItems();
        if (list != null && !list.isEmpty()) {
//            manageGeoShopItems(list);
        } else {
            EventBus.getDefault().post(new LocationUpdateRequestEvent());
        }
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            mMapView.onResume();
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            mapFragment.onResume();
        }

    }
}
