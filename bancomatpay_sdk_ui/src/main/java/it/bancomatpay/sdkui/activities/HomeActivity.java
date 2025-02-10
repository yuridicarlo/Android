package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyConsents;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.CustomProgressDialogFragment;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.GoToHomeInterface;
import it.bancomatpay.sdkui.widgets.BlueCustomDialog;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.LOYALTY_TOKEN_EXPIRED;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.NO_ACTIVE_IBAN;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CONTACT_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CONTACTS_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_FLOW_RETURN_TO_INTRO;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW_LOST_PIN;
import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.CASHBACK_SHOW_INFO_DIALOG;
import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_BPLAY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_PAYMENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_PHONEBOOK;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_SERVICES;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_SETTINGS;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_USER_GEOLOC;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_LATITUDE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_LONGITUDE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class HomeActivity extends GenericErrorActivity implements GoToHomeInterface, BlueCustomDialog.InteractionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    public static final int REQUEST_PERMISSION_CAMERA = 250;

    protected CustomProgressDialogFragment progressLoading;

    private static final String ALREADY_SHOW_DIALOG_SET_INCOMING_DEFAULT_INSTRUMENT = "ALREADY_SHOW_DIALOG_SET_INCOMING_DEFAULT_INSTRUMENT";
    boolean alreadyShowDialogSetIncomingDefaultInstrument = false;

    private LocationCallback mLocationCallback;


    // Location updates intervals in sec
    private static final int UPDATE_INTERVAL = 10000; // 10 sec
    private static final int FASTEST_INTERVAL = 5000; // 5 sec

    protected boolean processPush;

    private Handler handlerUi;

    protected BcmLocation mLastLocation;


    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            });






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(HomeActivity.class.getSimpleName());

        if (getIntent().getBooleanExtra(FINISH_SDK_FLOW, false)) {
            finish();
        }

        if (savedInstanceState != null) {
            alreadyShowDialogSetIncomingDefaultInstrument = savedInstanceState.getBoolean(ALREADY_SHOW_DIALOG_SET_INCOMING_DEFAULT_INSTRUMENT, false);
        }


        handlerUi = new Handler(Looper.getMainLooper());

        if (!processPush) {
            BancomatSdk.getInstance().getUserFrequent();
        }

        //showCashbackDialog();

        if (!hasPermissionCamera()) {
            requestPermissionCamera();
        }

        //Non usato
        //BitmapCacheUtil.getInstance().refreshBitmapCacheStatus();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                CustomLogger.d(TAG, "onLocationResult = " + locationResult.getLastLocation().toString());
                BcmLocation currentLocation = new BcmLocation();
                currentLocation.setLatitude(locationResult.getLastLocation().getLatitude());
                currentLocation.setLongitude(locationResult.getLastLocation().getLongitude());
                onLocationChanged(currentLocation);
            }
        };
        startLocationUpdates();

    }

    public boolean checkSectionNavigable(){
        return true;
    }



  /*  private void showCashbackDialog() {
        if (getIntent().getBooleanExtra(CASHBACK_SHOW_INFO_DIALOG, true)) {
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                int timesToShow;
                if (FullStackSdkDataManager.getInstance().getTimeToAccessInHome() == 0) {
                    timesToShow = TIMES_TO_SHOW_CASHBACK_DIALOG_DEFAULT;
                } else
                    timesToShow = FullStackSdkDataManager.getInstance().getTimeToShowCashbackDialog();
                if (timesToShow > 0) {
                    FullStackSdkDataManager.getInstance().putTimesToShowCashbackDialog(timesToShow - 1);
                    CashbackCustomDialog dialog = new CashbackCustomDialog();
                    if (!isFinishing() && !isDestroyed()) {
                        dialog.show(getSupportFragmentManager(), "");
                    }
                }
            }
        }
    }*/





    @Override
    protected void onResume() {
        super.onResume();
        //showBplayBalloon();

        BCMReturnHomeCallback.getInstance().setReturnHomeListener(this);
        BancomatSdk.getInstance().getUserFrequent();
    }

    protected void verifyBPayTermsAndConditionsAccepted() {
        if (!TextUtils.isEmpty(BancomatDataManager.getInstance().getBPayTermsAndConditionsAcceptedTimestamp())) {
            long savedTimestamp = Long.parseLong(BancomatDataManager.getInstance().getBPayTermsAndConditionsAcceptedTimestamp());
          //  SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String bPayTermsAndConditionsAcceptedTimestamp = dayTime.format(new Date(savedTimestamp));


            BancomatSdkInterface.Factory.getInstance().doAcceptCashbackBpayTermsAndConditions(this, result -> {
                if (result != null) {
                    if (result.isSuccess()) {
                        BancomatDataManager.getInstance().putBPayTermsAndConditionsAcceptedTimestamp("");
                    }
                }
            }, bPayTermsAndConditionsAcceptedTimestamp, SessionManager.getInstance().getSessionToken());
        }
        checkCashbackDigitalPaymentStatus();

    }

    private void checkCashbackDigitalPaymentStatus() {

        BancomatSdkInterface.Factory.getInstance().doGetCashbackStatus(
                this, result -> {
                    if (progressLoading != null && progressLoading.isVisible()) {
                        progressLoading.dismissAllowingStateLoss();
                    }
                    if (result != null) {
                        if (result.isSuccess()) {
                            if ((result.getResult().isbPaySubscribed() && result.getResult().isPagoPaCashbackEnabled()) ||
                                    (!result.getResult().isbPaySubscribed() && result.getResult().isPagoPaCashbackEnabled()
                                            && result.getResult().getbPayUnsubscribedTimestamp() != null)
                            ) {
                                if (FullStackSdkDataManager.getInstance().getTimeToShowCashbackDialog() != 0) {
                                    FullStackSdkDataManager.getInstance().putTimesToShowCashbackDialog(0);
                                }
                                HomeFlowManager.goToCashbackDigitalPayments(this, result.getResult());
                            } else {
                                HomeFlowManager.goToCashbackTermsAndConditions(this, result.getResult());
                            }
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                }, SessionManager.getInstance().getSessionToken());

        progressLoading = new CustomProgressDialogFragment();
        progressLoading.show(getSupportFragmentManager(), "");
    }

    public void showCustomerJourneyConsents() {
        //non usato
    }

    public void saveCjConsents(CustomerJourneyConsents cjConsents) {
        //non usato
    }

    public void goToProfile() {
        HomeFlowManager.goToProfile(this);
    }

    @Override
    public void onBackPressed() {
        BCMAbortCallback.getInstance().getAuthenticationListener()
                .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_EXIT);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        handlerUi.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_SHOW_DIALOG_SET_INCOMING_DEFAULT_INSTRUMENT, alreadyShowDialogSetIncomingDefaultInstrument);
    }



    @Override
    public void goToHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked,
                         boolean returnToIntro) {
        Intent i = new Intent(activity, HomeActivity.class);

        i.putExtra(CASHBACK_SHOW_INFO_DIALOG, false);

        if (finishSdkFlow) {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(FINISH_SDK_FLOW, true);
        } else if (pinBlocked) {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(FINISH_SDK_FLOW_LOST_PIN, true);
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        if (returnToIntro) {
            i.putExtra(FINISH_FLOW_RETURN_TO_INTRO, true);
        }

        activity.startActivity(i);
    }

    private boolean hasPermissionCamera() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }



    private void requestPermissionCamera() {
        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)) {
            PermissionFlowManager.goToCameraDisclosure(this, activityResultLauncherCamera);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
    }



    /*  private void requestPermissionContactsAndCamera() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA}, REQUEST_PERMISSION_CONTACTS_AND_CAMERA);
    }*/



    private CustomProgressDialogFragment progressDialogBplay;

    protected void goToBplayService() {
        progressDialogBplay = new CustomProgressDialogFragment();
        progressDialogBplay.showNow(getSupportFragmentManager(), "");
        String loyaltyTokenSaved = LoyaltyTokenManager.getInstance().getLoyaltyToken();
        if (TextUtils.isEmpty(loyaltyTokenSaved)) {
            requestAuthenticationForLoyaltyToken();
        } else {
            getLoyaltyJwtForBplay(loyaltyTokenSaved);
        }
    }

    private void requestAuthenticationForLoyaltyToken() {
        BCMOperationAuthorization bcmOperationAuthorization = new BCMOperationAuthorization();
        bcmOperationAuthorization.setOperation(AuthenticationOperationType.BPLAY);

        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForDispositiveOperation(this,
                        bcmOperationAuthorization, (authenticated, authToken, loyaltyToken, paymentId) -> {
                            if (authenticated) {
                                LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);
                                getLoyaltyJwtForBplay(loyaltyToken);
                            } else {
                                showAuthenticationErrorDialog();
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                            }
                        });
    }

    private void getLoyaltyJwtForBplay(String loyaltyToken) {
        BancomatSdkInterface.Factory.getInstance().doGetLoyaltyJwt(
                this,
                result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            dismissProgressDialogBplay();
                            HomeFlowManager.goToBplay(this, result.getResult());
                            new Handler().postDelayed(() ->
                                    EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME)), 500);
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else if (result.getStatusCode() == LOYALTY_TOKEN_EXPIRED) {
                            requestAuthenticationForLoyaltyToken();
                        } else {
                            dismissProgressDialogBplay();
                            showError(result.getStatusCode());
                        }
                    } else {
                        dismissProgressDialogBplay();
                    }
                },
                loyaltyToken,
                SessionManager.getInstance().getSessionToken()
        );
    }

    protected void dismissProgressDialogBplay() {
        if (progressDialogBplay != null && progressDialogBplay.isVisible()) {
            progressDialogBplay.dismissAllowingStateLoss();
        }
    }

    private void showAuthenticationErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.bplay_authentication_error_message)
                .setPositiveButton(R.string.ok, (dialog, which) ->
                        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME)))
                .setCancelable(false);
        builder.show();
    }

    private void onLocationChanged(BcmLocation location) {
        CustomLogger.d(TAG, "onLocationChanged = " + location);
        mLastLocation = location;
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            CustomLogger.d(TAG, "startLocationUpdates");

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(100);

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

                                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                    LocationManager locationManager = (LocationManager) HomeActivity.this.getSystemService(LOCATION_SERVICE);
                                    if (locationManager != null) {
                                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FASTEST_INTERVAL, 0, new LocationListener() {
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
                            CustomLogger.d(TAG, "Error trying to get last NETWORK location: " + e.getMessage()));

            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        }

    }



    public static boolean isFirstInstall(@NonNull Context context) {
        try {
            long firstInstallTime = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;
            long lastUpdateTime = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).lastUpdateTime;
            return firstInstallTime == lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            CustomLogger.e("HomeCameraFragment", "Error in getting if version has been updated");
            return true;
        }
    }

    protected void sendCjTagEventLocation(BcmLocation location) {
        if (location != null) {
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_LATITUDE, String.valueOf(location.getLatitude()));
            mapEventParams.put(PARAM_LONGITUDE, String.valueOf(location.getLongitude()));
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_USER_GEOLOC, mapEventParams, false);
        }
    }

    @Override
    public void onFirstButtonClicked(int requestCode) {

    }

    @Override
    public void onSecondButtonClicked(int requestCode) {

    }


}
