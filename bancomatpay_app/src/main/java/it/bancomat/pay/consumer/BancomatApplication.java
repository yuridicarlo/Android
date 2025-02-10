package it.bancomat.pay.consumer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.google.firebase.messaging.FirebaseMessaging;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.hms.aaid.HmsInstanceId;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

import it.bancomat.pay.consumer.cj.CustomerJourneyIntentService;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.extended.activities.HomeActivityExtended;
import it.bancomat.pay.consumer.network.BancomatPayApi;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.task.AppRefreshTokenTask;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.AppTokens;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.utilities.BCMAuthenticationResultListener;
import it.bancomat.pay.consumer.utilities.activity.BiometricAuthenticationActivity;
import it.bancomat.pay.eventbus.EventBusIndex;
import it.bancomatpay.consumer.BuildConfig;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.listener.BCMFullStackAbortListener;
import it.bancomatpay.sdkui.listener.BCMFullStackAtmCardlessListener;
import it.bancomatpay.sdkui.listener.BCMFullStackAuthenticationListener;
import it.bancomatpay.sdkui.listener.BCMFullStackCJEventListener;
import it.bancomatpay.sdkui.listener.BCMFullStackCashbackListener;
import it.bancomatpay.sdkui.listener.BCMFullStackDirectDebitsListener;
import it.bancomatpay.sdkui.listener.BCMFullStackOperationHandlerListener;
import it.bancomatpay.sdkui.listener.BCMFullStackProviderAccessListener;
import it.bancomatpay.sdkui.listener.BCMSessionRefreshListener;
import it.bancomatpay.sdkui.utilities.GoToHomeInterface;

import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_ID;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_TAG;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.WITHDRAWAL_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.BCM_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_AUTH_REQUEST_TYPE;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.DIRECT_DEBITS_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.DISPOSITIVE_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.PAYMENT_AUTHENTICATION_P2B;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.PAYMENT_AUTHENTICATION_P2P;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.NO_RETE;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_FLOW_RETURN_TO_INTRO;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW;
import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW_LOST_PIN;
import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.CASHBACK_SHOW_INFO_DIALOG;

public class BancomatApplication extends Application
        implements BCMFullStackAuthenticationListener, BCMFullStackAbortListener,
        BCMAuthenticationResultListener, GoToHomeInterface, BCMFullStackCJEventListener {

    private static final String TAG = BancomatApplication.class.getSimpleName();

    private static Context context;
    private BCMFullStackOperationHandlerListener paymentListener;
    private BCMFullStackProviderAccessListener providerListener;
    private BCMFullStackAtmCardlessListener withdrawalOperationListener;
    private BCMFullStackDirectDebitsListener directDebitListener;
    private BCMFullStackCashbackListener cashbackListener;

    private Set<Task<?>> taskSet;

    @Override
    public void onCreate() {
        //turnOnStrictMode();
        super.onCreate();
        context = getApplicationContext();
        taskSet = new HashSet<>();

        //payCore va inizializzato nell'application
        PayCore.setAppContext(getAppContext());
        EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();

        /*if (Constants.CUSTOMER_JOURNEY_ENABLED) {
            Intent intent = new Intent(context, CustomerJourneyIntentService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }*/

        BancomatFullStackSdkInterfaceExtended.Factory.getInstance().setAuthenticationListener(this);
        BancomatFullStackSdkInterfaceExtended.Factory.getInstance().setAbortListener(this);
        BancomatPayApiInterface.Factory.getInstance().setCustomerJourneyEventListener(this);

        BancomatPayApiInterface.Factory.getInstance().setAuthenticationResultListener(this);
        BancomatFullStackSdk.getInstance().setReturnHomeListener(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (BuildConfig.DEBUG) {
            MultiDex.install(this);
        }
    }

    /*private void turnOnStrictMode() {
        if (BuildConfig.DEBUG) {
             StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
             StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                             .penaltyLog().penaltyDeath().build());
        }
    }*/

    public static Context getAppContext() {
        return context;
    }

    @Override
    public synchronized void authenticationNeededForDispositiveOperation(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackOperationHandlerListener listener) {

        this.paymentListener = listener;

        Intent intentAuthentication = new Intent(activity, BiometricAuthenticationActivity.class);
        intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmPayment);
        intentAuthentication.putExtra(DISPOSITIVE_OPERATION_EXTRA, true);

        intentAuthentication.putExtra(PAYMENT_AUTHENTICATION_P2B, bcmPayment.isP2B());
        intentAuthentication.putExtra(PAYMENT_AUTHENTICATION_P2P, !bcmPayment.isP2B());

        activity.startActivity(intentAuthentication);
    }

    @Override
    public synchronized void authenticationNeededForProviderAccess(Activity activity, BCMFullStackProviderAccessListener listener, String requestId, String requestTag) {

        this.providerListener = listener;

        Intent intentAuthentication = new Intent(activity, BiometricAuthenticationActivity.class);
        intentAuthentication.putExtra(ACCESS_REQUEST_ID, requestId);
        intentAuthentication.putExtra(ACCESS_REQUEST_TAG, requestTag);
        intentAuthentication.putExtra(ACCESS_REQUEST_OPERATION_EXTRA, true);
        activity.startActivity(intentAuthentication);
    }

    @Override
    public void authenticationNeededForWithdrawalOperation(Activity activity, BCMOperationAuthorization bcmOperation, BCMFullStackAtmCardlessListener listener) {

        this.withdrawalOperationListener = listener;

        Intent intentAuthentication = new Intent(activity, BiometricAuthenticationActivity.class);
        intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmOperation);
        intentAuthentication.putExtra(WITHDRAWAL_OPERATION_EXTRA, true);
        activity.startActivity(intentAuthentication);
    }

    @Override
    public void authenticationNeededForDirectDebits(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackDirectDebitsListener listener) {
        this.directDebitListener = listener;

        Intent intentAuthentication = new Intent(activity, BiometricAuthenticationActivity.class);
        intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmPayment);
        intentAuthentication.putExtra(DIRECT_DEBITS_OPERATION_EXTRA, true);

        activity.startActivity(intentAuthentication);
    }

    @Override
    public void authenticationNeededForCashback(Activity activity, BCMOperationAuthorization bcmPayment, ECashbackAuthorizationTypeRequest cashbackAuthorizationTypeRequest, BCMFullStackCashbackListener listener) {
        this.cashbackListener = listener;

        Intent intentAuthentication = new Intent(activity, BiometricAuthenticationActivity.class);
        intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmPayment);
        intentAuthentication.putExtra(CASHBACK_OPERATION_EXTRA, true);
        intentAuthentication.putExtra(CASHBACK_AUTH_REQUEST_TYPE, cashbackAuthorizationTypeRequest);

        activity.startActivity(intentAuthentication);
    }

    @Override
    public synchronized void onAbort(BancomatFullStackSdkInterfaceExtended.EBCMFullStackStatusCodes statusCode) {
        if (statusCode == BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED) {
            CustomLogger.d(TAG, "onAbort USER_DELETED triggered!");

            BancomatPayApiInterface.Factory.getInstance().deleteUserData();

            /*Intent intentIntro = new Intent(this, IntroActivity.class);
            intentIntro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentIntro);*/
        } else {
            CustomLogger.d(TAG, "Abort statusCode = " + statusCode.toString());
        }
    }

    @Override
    public void onAbortSession(Activity activity, BCMSessionRefreshListener listener) {
        CustomLogger.d(TAG, "onAbort SESSION_EXPIRED triggered!");
        if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
            callRefreshToken(activity, listener);
        }
    }

    @Override
    public void onOperationAuthenticationResult(boolean authenticated, BCMOperationAuthorization bcmOperation, String authorizationToken, String loyaltyToken) {
        if (this.paymentListener != null) {
            if (authenticated) {
                this.paymentListener.authenticationOperationResult(true, authorizationToken, loyaltyToken, bcmOperation.getPaymentId());
            } else {
                this.paymentListener.authenticationOperationResult(false, authorizationToken, loyaltyToken, "");
            }
        } else {
            CustomLogger.e(TAG, "BancomatApplication.paymentListener is null, can't do operation");
        }
        this.paymentListener = null;
    }

    @Override
    public void onProviderAccessAuthenticationResult(boolean authenticated, String authorizationToken) {
        if (this.providerListener != null) {
            this.providerListener.authenticationResult(authenticated, authorizationToken);
        } else {
            CustomLogger.e(TAG, "BancomatApplication.providerListener is null, can't do operation");
        }
        this.providerListener = null;
    }

    @Override
    public void onWithdrawalAuthenticationResult(boolean authenticated, String authorizationToken) {
        if (this.withdrawalOperationListener != null) {
            this.withdrawalOperationListener.authenticationResult(authenticated, authorizationToken);
        } else {
            CustomLogger.e(TAG, "BancomatApplication.atmCardlessListener is null, can't do operation");
        }
        this.withdrawalOperationListener = null;
    }

    @Override
    public void onDirectDebitAuthenticationResult(boolean authenticated, String authorizationToken) {
        if (this.directDebitListener != null) {
            this.directDebitListener.authenticationResult(authenticated, authorizationToken);
        } else {
            CustomLogger.e(TAG, "BancomatApplication.directDebitListener is null, can't do operation");
        }
        this.directDebitListener = null;
    }

    @Override
    public void onCashbackAuthenticationResult(boolean authenticated, ECashbackActivationResult result) {
        if (this.cashbackListener != null) {
            this.cashbackListener.authenticationResult(authenticated, result);
        } else {
            CustomLogger.e(TAG, "BancomatApplication.cashbackListener is null, can't do operation");
        }
        this.cashbackListener = null;
    }

    private void callRefreshToken(Activity activity, BCMSessionRefreshListener listener) {
        Task<?> task = new AppRefreshTokenTask(result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    CustomLogger.d(TAG, "Session token refreshed!");
                    //retrySessionTask();
                    listener.onSessionRefreshed(SessionManager.getInstance().getSessionToken());
                } else {
                    CustomLogger.e(TAG, "Session token error: " + result.toString());
                    manageError(activity, result.getStatusCode());
                }
            }
        });
        task.setMasterListener(new OnCompleteListener() {
            @Override
            public void onComplete(Task<?> task) {
                task.removeListener();
                CustomLogger.d(TAG, "Session token master listener completed");
                taskSet.remove(task);
            }

            @Override
            public void onCompleteWithError(Task<?> task, Error error) {
                if (error instanceof HttpError) {
                    HttpError httpError = (HttpError) error;
                    if (httpError.getCode() == 401) {
                        manageError(activity, StatusCode.Server.EXIT_APP);
                    }
                } else {
                    CustomLogger.d(task.getClass().getSimpleName(), error.toString());
                    if (error instanceof ExtendedError) {
                        StatusCodeInterface statusCodeInterface = ((ExtendedError) error).getStatusCodeInterface();
                        manageError(activity, statusCodeInterface);
                    } else {
                        if (BancomatPayApi.getInstance().isDeviceOnline(getAppContext())) {
                            manageError(activity, StatusCode.Http.GENERIC_ERROR);
                        } else {
                            manageError(activity, NO_RETE);
                        }
                    }
                }
            }
        });
        if (!taskSet.contains(task)) {
            taskSet.add(task);
            task.execute();
        }
    }

    private void manageError(Activity activity, StatusCodeInterface statusCodeInterface) {
        if (activity instanceof AppGenericErrorActivity) {
            ((AppGenericErrorActivity) activity).manageError(statusCodeInterface);
        } else if (activity instanceof GenericErrorActivity) {
            ((GenericErrorActivity) activity).manageError(statusCodeInterface);
        }
    }

    @Override
    public void onSendCJTagEvent(Context context, CustomerJourneyTag tagEvent) {
        boolean isProfilingAllowed = BancomatPayApiInterface.Factory.getInstance().isProfilingConsentAllowed();
        if (isProfilingAllowed) {
            CustomerJourneyIntentService.enqueueWork(context, tagEvent);
        }
        CustomLogger.d("setCustomerJourneyTag", "isProfilingAllowed = " + isProfilingAllowed);
    }

    @Override
    public void goToHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked, boolean returnToIntro) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            //This method is a synchronous method, and you cannot call it in the main thread.
            try{
                String pushToken = HmsInstanceId.getInstance(activity).getToken(new AGConnectOptionsBuilder().build(activity).getString("client/app_id"), com.huawei.hms.push.HmsMessaging.DEFAULT_TOKEN_SCOPE);
                if (!TextUtils.isEmpty(pushToken)) {
                    AppTokens tokens = AppBancomatDataManager.getInstance().getTokens();
                    String sessionToken = tokens != null ? tokens.getOauth() : "";
                    BancomatPayApiInterface.Factory.getInstance().doSubscribePush(
                            null, pushToken, sessionToken);
                }
                startIntentHome(activity, finishSdkFlow, pinBlocked, returnToIntro);

            } catch (Exception e) {
                CustomLogger.e(TAG, "Failed to retrieve HMS token: " + e.getMessage());
                startIntentHome(activity, finishSdkFlow, pinBlocked, returnToIntro);
            }


        }else if(BancomatFullStackSdk.getInstance().hasGooglePlayServices()){
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(activity, instanceIdResult -> {
                        if (!TextUtils.isEmpty(instanceIdResult)) {
                            AppTokens tokens = AppBancomatDataManager.getInstance().getTokens();
                            String sessionToken = tokens != null ? tokens.getOauth() : "";
                            BancomatPayApiInterface.Factory.getInstance().doSubscribePush(
                                    null, instanceIdResult, sessionToken);
                        }
                        startIntentHome(activity, finishSdkFlow, pinBlocked, returnToIntro);
                    })
                    .addOnFailureListener(activity, e -> {
                        CustomLogger.e(TAG, "Failed to retrieve Firebase token: " + e.getMessage());
                        startIntentHome(activity, finishSdkFlow, pinBlocked, returnToIntro);
                    });
        }

    }

    private void startIntentHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked, boolean returnToIntro) {
        Intent i = new Intent(activity, HomeActivityExtended.class);

        i.putExtra(CASHBACK_SHOW_INFO_DIALOG, false);

        boolean finishActivity = false;
        if (finishSdkFlow) {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(FINISH_SDK_FLOW, true);
        } else if (pinBlocked) {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(FINISH_SDK_FLOW_LOST_PIN, true);
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finishActivity = true;
        }

        if (returnToIntro) {
            i.putExtra(FINISH_FLOW_RETURN_TO_INTRO, true);
        }

        activity.startActivity(i);
        if (finishActivity) {
            activity.finish();
        }
    }

}
