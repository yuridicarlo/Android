package it.bancomat.pay.consumer.utilities.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import it.bancomat.pay.consumer.AppAuthenticationResultCallback;
import it.bancomat.pay.consumer.login.AuthenticationActivity;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.AuthenticationFingerprintData;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintAuthHelper;
import it.bancomat.pay.consumer.touchid.FingerprintDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintState;
import it.bancomat.pay.consumer.touchid.dialog.FingerprintAuthenticationDialog;
import it.bancomat.pay.consumer.touchid.dialog.UpdateStatus;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.CjUtils;

import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_ID;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.ACCESS_REQUEST_TAG;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.WITHDRAWAL_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.AUTHORIZATION_TOKEN_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.BCM_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_AUTH_REQUEST_TYPE;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.CASHBACK_RESULT_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.DIRECT_DEBITS_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.DISPOSITIVE_OPERATION_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.LOYALTY_TOKEN_EXTRA;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.PAYMENT_AUTHENTICATION_P2B;
import static it.bancomat.pay.consumer.login.AuthenticationActivity.PAYMENT_AUTHENTICATION_P2P;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.BANKID;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.CASHBACK;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.PAYMENT;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.POS;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.CASHBACK_UNDEFINED_ENROLLMENT;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXIT_APP;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_USER_NOT_ENABLED_ON_BCM_PAY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_BIOMETRY_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_BIOMETRIC_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_BIOMETRIC_AUTH;

public class TransparentAuthenticationActivity extends AppGenericErrorActivity
        implements FingerprintAuthenticationDialog.InteractionListener, FingerprintAuthHelper.AuthCallback {

    private static final String TAG = TransparentAuthenticationActivity.class.getSimpleName();
    private static final String FINGERPRINT_DIALOG_FRAGMENT_TAG = "FINGERPRINT_DIALOG_FRAGMENT_TAG";
    private static final int REQUEST_CODE_AUTHENTICATION = 1000;

    private BCMOperationAuthorization bcmOperationExtra;

    private FingerprintAuthHelper fingerprintAuthHelper;
    private FingerprintAuthenticationDialog dialogFingerprint;

    private Handler handler;
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

    ActivityResultLauncher<Intent> activityResultLauncherRequestAuthentication = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_AUTHENTICATION,result.getResultCode(),data);
            });

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
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

        if (BancomatPayApiInterface.Factory.getInstance().isFingerprintEnrolled()) {

            fingerprintAuthHelper = new FingerprintAuthHelper(this);

            String dialogDescription;
            if (isOperationAuthentication && bcmOperationExtra.getOperation() == AuthenticationOperationType.BPLAY) {
                dialogDescription = getString(R.string.common_check_fingerprint_status_idle);
            } else {
                dialogDescription = getString(R.string.use_fingerprint_description_generic);
            }

            dialogFingerprint = FingerprintAuthenticationDialog.newInstance(dialogDescription);
            dialogFingerprint.setCancelable(false);
            dialogFingerprint.showNow(getSupportFragmentManager(), FINGERPRINT_DIALOG_FRAGMENT_TAG);
            /*if (fingerprintAuthHelper != null) {
                fingerprintAuthHelper.startListening();
            }*/

        } else {
            goToAuthenticationPin();
        }

    }

    private void goToAuthenticationPin() {
        Intent intentAuthentication = new Intent(this, AuthenticationActivity.class);
        if (isOperationAuthentication) {
            intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmOperationExtra);
            intentAuthentication.putExtra(DISPOSITIVE_OPERATION_EXTRA, true);
            intentAuthentication.putExtra(PAYMENT_AUTHENTICATION_P2B, getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2B, false));
            intentAuthentication.putExtra(PAYMENT_AUTHENTICATION_P2P, getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2P, false));
        } else if (isProviderAccessAuthorization) {
            intentAuthentication.putExtra(ACCESS_REQUEST_ID, accessRequestId);
            intentAuthentication.putExtra(ACCESS_REQUEST_TAG, accessRequestTag);
            intentAuthentication.putExtra(ACCESS_REQUEST_OPERATION_EXTRA, true);
        } else if (isWithdrawalOperationAuthorization) {
            intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmOperationExtra);
            intentAuthentication.putExtra(WITHDRAWAL_OPERATION_EXTRA, true);
        } else if (isDirectDebitAuthorization) {
            intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmOperationExtra);
            intentAuthentication.putExtra(DIRECT_DEBITS_OPERATION_EXTRA, true);
        } else if (isCashbackAuthorization) {
            intentAuthentication.putExtra(BCM_OPERATION_EXTRA, bcmOperationExtra);
            intentAuthentication.putExtra(CASHBACK_OPERATION_EXTRA, true);
            intentAuthentication.putExtra(CASHBACK_AUTH_REQUEST_TYPE, cashbackAuthorizationTypeRequest);
        }
        activityResultLauncherRequestAuthentication.launch(intentAuthentication);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fingerprintAuthHelper != null) {
            fingerprintAuthHelper.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fingerprintAuthHelper != null && dialogFingerprint != null) {
            fingerprintAuthHelper.startListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onFingerprintDialogCancelPressed() {
        if (fingerprintAuthHelper != null) {
            fingerprintAuthHelper.stopListening();
        }
        dialogFingerprint.dismissAllowingStateLoss();
        goToAuthenticationPin();
    }

    @Override
    public void stateError(FingerprintState fingerprintState) {
        CustomLogger.d(TAG, "stateError " + fingerprintState);
        EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.ERROR_FATAL,
                getString(R.string.common_check_fingerprint_authentication_error, fingerprintState.name())));
    }

    @Override
    public void authenticationError(String errString) {
        CustomLogger.d(TAG, "authenticationError " + errString);
        EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.ERROR_FATAL,
                getString(R.string.common_check_fingerprint_authentication_error, errString)));
    }

    @Override
    public void authenticationFailed() {
        CustomLogger.d(TAG, "authenticationFailed ");
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
        EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.ERROR,
                getString(R.string.common_check_fingerprint_authentication_failed)));
    }

    @Override
    public void manageAuthenticationSuccess(byte[] data) {

        if (data != null) {

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
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_BIOMETRIC_AUTH, null, true);
                    } else if (getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2P, false)) {
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_BIOMETRIC_AUTH, null, true);
                    }
                }
            } else if (isCashbackAuthorization) {
                operationType = CASHBACK;
            }

            Task<?> t = BancomatPayApiInterface.Factory.getInstance().doGetAuthorizationToken(result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                if (isCashbackAuthorization) {

                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_BIOMETRY_AUTH, null, false);

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
                                    goToResultPostDelayed(
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
                    new AuthenticationFingerprintData(data),
                    operationType,
                    operationId,
                    amount,
                    msisdnSender,
                    receiver);

            addTask(t);

        } else {
            FingerprintDataManager.getInstance().delete();
            showErrorFingerprintAndUsePin(StatusCode.Mobile.FINGERPRINT_MODIFIED, (dialog, which) -> {
                AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                        .onOperationAuthenticationResult(
                                false, null, null, null);
                finish();
            });
        }
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

    private void goToResultPostDelayed(String authorizationToken, String loyaltyToken) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        handler.postDelayed(() -> {
            if (dialogFingerprint != null && dialogFingerprint.isVisible()) {
                dialogFingerprint.dismissAllowingStateLoss();
            }
            goToResult(authorizationToken, loyaltyToken);
        }, FingerprintAuthenticationDialog.SHORT_TIME_OUT);
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

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_AUTHENTICATION) {
            if (resultCode == RESULT_OK && data != null) {

                if (isOperationAuthentication) {
                    AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                            .onOperationAuthenticationResult(
                                    true, (BCMOperationAuthorization) data.getSerializableExtra(BCM_OPERATION_EXTRA),
                                    data.getStringExtra(AUTHORIZATION_TOKEN_EXTRA), data.getStringExtra(LOYALTY_TOKEN_EXTRA));
                } else if (isProviderAccessAuthorization) {
                    AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                            .onProviderAccessAuthenticationResult(true,
                                    data.getStringExtra(AUTHORIZATION_TOKEN_EXTRA));
                } else if (isWithdrawalOperationAuthorization) {
                    AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                            .onWithdrawalAuthenticationResult(true,
                                    data.getStringExtra(AUTHORIZATION_TOKEN_EXTRA));
                } else if (isDirectDebitAuthorization) {
                    AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                            .onDirectDebitAuthenticationResult(true,
                                    data.getStringExtra(AUTHORIZATION_TOKEN_EXTRA));
                } else if (isCashbackAuthorization) {
                    AppAuthenticationResultCallback.getInstance().getAuthenticationResultListener()
                            .onCashbackAuthenticationResult(true,
                                    (ECashbackActivationResult) data.getSerializableExtra(CASHBACK_RESULT_EXTRA));
                }

            } else if (resultCode == RESULT_CANCELED) {

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
            }
        }
        finish();
    }
}
