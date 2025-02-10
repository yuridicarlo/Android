package it.bancomat.pay.consumer.utilities.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyPermanentlyInvalidatedException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.greenrobot.eventbus.EventBus;

import java.security.InvalidKeyException;
import java.security.UnrecoverableKeyException;

import it.bancomat.pay.consumer.AppAuthenticationResultCallback;
import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomat.pay.consumer.biometric.PromptInfoHelper;
import it.bancomat.pay.consumer.biometric.callable.GetAppAuthentication;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.touchid.dialog.UpdateStatus;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.lifecycle.LiveUtil;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveSingle;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.CjUtils;

import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_ID;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_TAG;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.BCM_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_AUTH_REQUEST_TYPE;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.DIRECT_DEBITS_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.DISPOSITIVE_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.PAYMENT_AUTHENTICATION_P2B;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.PAYMENT_AUTHENTICATION_P2P;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.WITHDRAWAL_OPERATION_EXTRA;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.BANKID;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.CASHBACK;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.PAYMENT;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.CASHBACK_UNDEFINED_ENROLLMENT;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXIT_APP;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_USER_NOT_ENABLED_ON_BCM_PAY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_AUTHORIZATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_AUTHORIZATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_AUTHORIZATION;

public class BiometricAuthenticationActivity extends AppGenericErrorActivity {

    private static final String TAG = BiometricAuthenticationActivity.class.getSimpleName();

    private BCMOperationAuthorization bcmOperationExtra;

    private boolean isOperationAuthentication = false;
    private boolean isProviderAccessAuthorization = false;
    private boolean isWithdrawalOperationAuthorization = false;
    private boolean isDirectDebitAuthorization = false;
    private boolean isCashbackAuthorization = false;
    private ECashbackAuthorizationTypeRequest cashbackAuthorizationTypeRequest;
    private String accessRequestId;
    private String accessRequestTag;

    private AuthenticationOperationType operationType;
    private String operationId;
    private String amount;
    private String msisdnSender;
    private String receiver;
    private MutableLiveSingle<AppAuthenticationInterface> getAppAuthenticationResponse;

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    ActivityResultLauncher<Intent> activityResultLauncherDeviceCredentials = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                EventBus.getDefault().post(new ActivityResult(REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, result.getResultCode(), data));
            });

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (getIntent().getBooleanExtra(DISPOSITIVE_OPERATION_EXTRA, false)) {
            bcmOperationExtra = (BCMOperationAuthorization) getIntent().getSerializableExtra(BCM_OPERATION_EXTRA);
            isOperationAuthentication = true;

        } else if (getIntent().getBooleanExtra(ACCESS_REQUEST_OPERATION_EXTRA, false)) {
            accessRequestId = getIntent().getStringExtra(ACCESS_REQUEST_ID);
            accessRequestTag = getIntent().getStringExtra(ACCESS_REQUEST_TAG);

            isProviderAccessAuthorization = true;

        } else if (getIntent().getBooleanExtra(WITHDRAWAL_OPERATION_EXTRA, false)) {
            bcmOperationExtra = (BCMOperationAuthorization) getIntent().getSerializableExtra(BCM_OPERATION_EXTRA);
            isWithdrawalOperationAuthorization = true;
        } else if (getIntent().getBooleanExtra(DIRECT_DEBITS_OPERATION_EXTRA, false)) {
            bcmOperationExtra = (BCMOperationAuthorization) getIntent().getSerializableExtra(BCM_OPERATION_EXTRA);
            isDirectDebitAuthorization = true;
        } else if (getIntent().getBooleanExtra(CASHBACK_OPERATION_EXTRA, false)) {
            bcmOperationExtra = (BCMOperationAuthorization) getIntent().getSerializableExtra(BCM_OPERATION_EXTRA);
            cashbackAuthorizationTypeRequest = (ECashbackAuthorizationTypeRequest) getIntent().getSerializableExtra(CASHBACK_AUTH_REQUEST_TYPE);
            isCashbackAuthorization = true;
        }
        KeyguardManager manager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        getAppAuthenticationResponse = new MutableLiveSingle<>();
        getAppAuthenticationResponse.setListener(this, new SingleListener<AppAuthenticationInterface>() {
            @Override
            public void onSuccess(AppAuthenticationInterface response) {
                manageAuthenticationSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                cancel(throwable);
            }
        });

        if (manager.isDeviceSecure()) {

            LiveUtil.fromCallable(new GetAppAuthentication(this, PromptInfoHelper.getPromptInfo(), REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, activityResultLauncherDeviceCredentials))
                    .subscribe(LiveUtil.getWrapper(getAppAuthenticationResponse));


        } else {
            cancel(null);
        }

    }

    private void manageAuthenticationSuccess(AppAuthenticationInterface data) {



            CustomLogger.d(TAG, "manageAuthenticationSuccess auth");
            EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.SUCCESS,
                    getString(R.string.common_check_fingerprint_authentication_success)));

            if (isProviderAccessAuthorization) {
                operationType = BANKID;
                operationId = accessRequestId;
                amount = "";
                msisdnSender = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();
                receiver = accessRequestTag;
            } else if (isOperationAuthentication || isWithdrawalOperationAuthorization || isDirectDebitAuthorization) {
                operationType = bcmOperationExtra.getOperation();
                operationId = bcmOperationExtra.getPaymentId();
                amount = bcmOperationExtra.getAmount();
                msisdnSender = bcmOperationExtra.getSender();
                receiver = bcmOperationExtra.getReceiver();

                if (operationType == PAYMENT) {
                    if (getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2B, false)) {
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_AUTHORIZATION, null, true);
                    } else if (getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2P, false)) {
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_AUTHORIZATION, null, true);
                    }
                }
            } else if (isCashbackAuthorization) {
                operationType = CASHBACK;
            }

            Task<?> t = BancomatPayApiInterface.Factory.getInstance().doGetAuthorizationToken(result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                if (isCashbackAuthorization) {

                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_AUTHORIZATION, null, false);

                                    switch (cashbackAuthorizationTypeRequest) {
                                        case SUBSCRIBE_BCM_CASHBACK_TOOL:
                                            doSubscribeBcmCashbackTool(result.getResult().getAuthorizationToken());
                                            break;
                                        case UNSUBSCRIBE_BCM_CASHBACK_TOOL:
                                            doUnsubscribeBcmCashbackTool(result.getResult().getAuthorizationToken());
                                            break;
                                        case DISABLE_CASHBACK:
                                            doDisableCashback(result.getResult().getAuthorizationToken());
                                            break;
                                    }
                                } else {
                                    goToResult(
                                            result.getResult().getAuthorizationToken(),
                                            result.getResult().getLoyaltyToken());
                                }
                            } else if (result.getStatusCode() == EXIT_APP) {
                                showExitAppError();
                            } else if (result.getStatusCode() == P2P_USER_NOT_ENABLED_ON_BCM_PAY) {
                                showUserNotEnabledError();
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> {
                                    AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                                            .onOperationAuthenticationResult(
                                                    false, null, null, null);
                                    finish();
                                });
                            }
                        } else {
                            showErrorAndDoAction(GENERIC_ERROR, (dialog, which) -> {
                                AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                                        .onOperationAuthenticationResult(
                                                false, null, null, null);
                                finish();
                            });
                        }
                    },
                    data,
                    operationType,
                    operationId,
                    amount,
                    msisdnSender,
                    receiver);

            addTask(t);


    }

    private void doSubscribeBcmCashbackTool(String authorizationToken) {
        BancomatSdkInterface.Factory.getInstance().doSubscribeCashback(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    goToResultCashbackOperation(ECashbackActivationResult.SUBSCRIBED_SUCCESS);
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    if (result.getStatusCode() == CASHBACK_UNDEFINED_ENROLLMENT) {
                        goToResultCashbackOperation(ECashbackActivationResult.SUBSCRIBED_UNDEFINED);
                    } else {
                        goToResultCashbackOperation(ECashbackActivationResult.SUBSCRIBED_FAILURE);
                    }
                }
            }
        }, authorizationToken, SessionManager.getInstance().getSessionToken());
    }

    private void doUnsubscribeBcmCashbackTool(String authorizationToken) {
        BancomatSdkInterface.Factory.getInstance().doUnsubscribeCashback(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    goToResultCashbackOperation(ECashbackActivationResult.BPAY_UNSUBSCRIBED_SUCCESS);
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    if (result.getStatusCode() == CASHBACK_UNDEFINED_ENROLLMENT) {
                        goToResultCashbackOperation(ECashbackActivationResult.SUBSCRIBED_UNDEFINED);
                    } else
                        goToResultCashbackOperation(ECashbackActivationResult.BPAY_UNSUBSCRIBED_FAILURE);
                }
            }
        }, authorizationToken, SessionManager.getInstance().getSessionToken());
    }

    private void doDisableCashback(String authorizationToken) {
        BancomatSdkInterface.Factory.getInstance().doDisableCashback(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    goToResultCashbackOperation(ECashbackActivationResult.CASHBACK_DISABLING_SUCCESS);
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    if (result.getStatusCode() == CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS) {
                        goToResultCashbackOperation(ECashbackActivationResult.CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS);
                    } else if (result.getStatusCode() == CASHBACK_UNDEFINED_ENROLLMENT) {
                        goToResultCashbackOperation(ECashbackActivationResult.SUBSCRIBED_UNDEFINED);
                    } else {
                        goToResultCashbackOperation(ECashbackActivationResult.CASHBACK_DISABLING_FAILED);
                    }
                }
            }
        }, authorizationToken, SessionManager.getInstance().getSessionToken());
    }


    private void goToResult(String authorizationToken, String loyaltyToken) {
        if (isOperationAuthentication) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onOperationAuthenticationResult(true, bcmOperationExtra, authorizationToken, loyaltyToken);
        } else if (isProviderAccessAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onProviderAccessAuthenticationResult(true, authorizationToken);
        } else if (isWithdrawalOperationAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onWithdrawalAuthenticationResult(true, authorizationToken);
        } else if (isDirectDebitAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onDirectDebitAuthenticationResult(true, authorizationToken);
        }
        finish();
    }

    private void goToResultCashbackOperation(ECashbackActivationResult result) {
        AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                .onCashbackAuthenticationResult(true, result);
        finish();
    }



    protected void cancel(Throwable throwable) {
        String bankUUID = AppBancomatDataManager.getInstance().getBankUuid();
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            CustomLogger.d(TAG, "doUserMonitoringTask success");
                        } else {
                            CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                        }
                    }
                },
                bankUUID,
                UserMonitoringConstants.WRONG_FINGERPRINT_PAYMENT,
                UserMonitoringConstants.WRONG_FINGERPRINT_PAYMENT_EVENT,
                "");
        addTask(t);
        if (isOperationAuthentication) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onOperationAuthenticationResult(false, null, null, null);
        } else if (isProviderAccessAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onProviderAccessAuthenticationResult(false, null);
        } else if (isWithdrawalOperationAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onWithdrawalAuthenticationResult(false, null);
        } else if (isDirectDebitAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onDirectDebitAuthenticationResult(false, null);
        } else if (isCashbackAuthorization) {
            AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                    .onCashbackAuthenticationResult(false, null);
        }
        if(throwable instanceof KeyPermanentlyInvalidatedException || throwable instanceof UnrecoverableKeyException){
            showExitAppError();
            FirebaseCrashlytics.getInstance().recordException(throwable);
        }else {
            finish();
        }
    }


}
