package it.bancomat.pay.consumer.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.AuthenticationData;
import it.bancomat.pay.consumer.network.dto.AuthenticationFingerprintData;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintAuthHelper;
import it.bancomat.pay.consumer.touchid.FingerprintDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintState;
import it.bancomat.pay.consumer.touchid.dialog.FingerprintAuthenticationDialog;
import it.bancomat.pay.consumer.touchid.dialog.UpdateStatus;
import it.bancomat.pay.consumer.utilities.AppFullscreenActivity;
import it.bancomat.pay.consumer.widget.LabelPasswordLogin;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityLoginV2Binding;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomat.pay.consumer.login.LoginFlowManager.PAYMENT_DATA_DEEPLINK;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.BANKID;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.CASHBACK;
import static it.bancomatpay.sdk.manager.model.AuthenticationOperationType.PAYMENT;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Mobile.FINGERPRINT_MODIFIED;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.CASHBACK_UNDEFINED_ENROLLMENT;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.EXIT_APP;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_USER_NOT_ENABLED_ON_BCM_PAY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_PIN_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_BIOMETRIC_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_PIN_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_BIOMETRIC_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_PIN_AUTH;

public class AuthenticationActivity extends AppFullscreenActivity implements LabelPasswordLogin.LabelListener,
        FingerprintAuthenticationDialog.InteractionListener, FingerprintAuthHelper.AuthCallback {

    private ActivityLoginV2Binding binding;

    private static final String TAG = AuthenticationActivity.class.getSimpleName();
    private static final String FINGERPRINT_DIALOG_FRAGMENT_TAG = "FINGERPRINT_DIALOG_FRAGMENT_TAG";

    public static final String DISPOSITIVE_OPERATION_EXTRA = "DISPOSITIVE_OPERATION_EXTRA";
    public static final String ACCESS_REQUEST_OPERATION_EXTRA = "ACCESS_REQUEST_OPERATION_EXTRA";
    public static final String ACCESS_REQUEST_ID = "ACCESS_REQUEST_ID";
    public static final String ACCESS_REQUEST_TAG = "ACCESS_REQUEST_TAG";
    public static final String WITHDRAWAL_OPERATION_EXTRA = "ATM_CARDLESS_OPERATION_EXTRA";
    public static final String DIRECT_DEBITS_OPERATION_EXTRA = "DIRECT_DEBITS_OPERATION_EXTRA";
    public static final String CASHBACK_OPERATION_EXTRA = "CASHBACK_OPERATION_EXTRA";

    public static final String BCM_OPERATION_EXTRA = "BCM_OPERATION_EXTRA";
    public static final String AUTHORIZATION_TOKEN_EXTRA = "AUTHORIZATION_TOKEN_EXTRA";
    public static final String LOYALTY_TOKEN_EXTRA = "LOYALTY_TOKEN_EXTRA";
    public static final String CASHBACK_RESULT_EXTRA = "CASHBACK_RESULT_EXTRA";
    public static final String CASHBACK_AUTH_REQUEST_TYPE = "CASHBACK_AUTH_REQUEST_TYPE";

    public static final String PAYMENT_AUTHENTICATION_P2B = "PAYMENT_AUTHENTICATION_P2B";
    public static final String PAYMENT_AUTHENTICATION_P2P = "PAYMENT_AUTHENTICATION_P2P";

    private FingerprintAuthHelper fingerprintAuthHelper;
    private FingerprintAuthenticationDialog dialogFingerprint;

    private Handler handler;

    private BCMOperationAuthorization bcmOperationExtra;
    private String accessRequestId;
    private String accessRequestTag;

    private boolean isDispositiveOperation;
    private boolean isAccessRequestOperation;
    private boolean isWithDrawalOperation;
    private boolean isDirectDebitOperation;
    private boolean isCashbackOperation;
    private ECashbackAuthorizationTypeRequest cashbackAuthorizationTypeRequest;

    private boolean canCallPinOperation;

    private boolean hasError = false;
    private boolean isOperationAuthentication = false;
    private boolean isProviderAuthorization = false;
    private boolean isWithdrawalOperationAuthorization = false;
    private boolean isCashbackAuthorization = false;

    private String paymentDataDeeplink;
    private AuthenticationOperationType operationType;
    private String operationId;
    private String amount;
    private String msisdnSender;
    private String receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.keyboardPin.setKeyboardListener(binding.labelPin);
        binding.labelPin.setMaxElements(5);
        binding.labelPin.setListener(this);
        binding.toolbarSimple.setCenterImageVisibility(true);

        if (BancomatPayApiInterface.Factory.getInstance().isFingerprintEnrolled()) {
            fingerprintAuthHelper = new FingerprintAuthHelper(this);
        } else {
            binding.fabUseFingerprint.hide();
        }

        bcmOperationExtra = (BCMOperationAuthorization) getIntent().getSerializableExtra(BCM_OPERATION_EXTRA);
        if (bcmOperationExtra != null) {
            if (bcmOperationExtra.getOperation() == AuthenticationOperationType.BPLAY) {
                binding.textInsertPinLabel.setText(getString(R.string.access_bplay_title));
            } else {
                binding.textInsertPinLabel.setText(getString(R.string.insert_pin_title));
            }
        } /*else {
            if (BancomatPayApiInterface.Factory.getInstance().isFingerprintEnrolled()) {
                new Handler().postDelayed(this::clickFabUseFingerprint, 250);
            }
        }*/

        isDispositiveOperation = getIntent().getBooleanExtra(DISPOSITIVE_OPERATION_EXTRA, false);
        isAccessRequestOperation = getIntent().getBooleanExtra(ACCESS_REQUEST_OPERATION_EXTRA, false);
        isWithDrawalOperation = getIntent().getBooleanExtra(WITHDRAWAL_OPERATION_EXTRA, false);
        isDirectDebitOperation = getIntent().getBooleanExtra(DIRECT_DEBITS_OPERATION_EXTRA, false);
        isCashbackOperation = getIntent().getBooleanExtra(CASHBACK_OPERATION_EXTRA, false);
        cashbackAuthorizationTypeRequest = (ECashbackAuthorizationTypeRequest) getIntent().getSerializableExtra(CASHBACK_AUTH_REQUEST_TYPE);

        bcmOperationExtra = (BCMOperationAuthorization) getIntent().getSerializableExtra(BCM_OPERATION_EXTRA);
        accessRequestId = getIntent().getStringExtra(ACCESS_REQUEST_ID);
        accessRequestTag = getIntent().getStringExtra(ACCESS_REQUEST_TAG);

        if (isDispositiveOperation || isAccessRequestOperation || isWithDrawalOperation || isDirectDebitOperation || isCashbackOperation) {
            binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
            if ((isDispositiveOperation && bcmOperationExtra != null) || (isDirectDebitOperation && bcmOperationExtra != null)) {
                isOperationAuthentication = true;
            } else if (isAccessRequestOperation && !TextUtils.isEmpty(accessRequestId) && !TextUtils.isEmpty(accessRequestTag)) {
                isProviderAuthorization = true;
            } else if (isWithDrawalOperation) {
                isWithdrawalOperationAuthorization = true;
            } else if (isCashbackOperation) {
                isCashbackAuthorization = true;
            }
        } else {
            binding.toolbarSimple.setLeftImageVisibility(false);
        }

        binding.fabUseFingerprint.setOnClickListener(new CustomOnClickListener(v -> clickFabUseFingerprint()));
        binding.textLostPin.setOnClickListener(new CustomOnClickListener(v -> LoginFlowManager.goToLostPin(this, false)));

        paymentDataDeeplink = getIntent().getStringExtra(PAYMENT_DATA_DEEPLINK);

        canCallPinOperation = true;

        applyInsets();

    }

    private void applyInsets() {

        int insetTop = BancomatDataManager.getInstance().getScreenInsetTop();
        RelativeLayout.LayoutParams spaceParams = (RelativeLayout.LayoutParams) binding.toolbarSimple.getLayoutParams();
        if (insetTop != 0) {
            binding.toolbarSimple.post(() -> {
                spaceParams.topMargin = insetTop;
                binding.toolbarSimple.setLayoutParams(spaceParams);
                binding.toolbarSimple.requestLayout();
            });
        }
        if (!hasSoftKeys()) {
            binding.keyboardPin.setPadding(0, 0, 0, 0);
        } else {
            int insetBottom = BancomatDataManager.getInstance().getScreenInsetBottom();
            binding.keyboardPin.post(() -> {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.keyboardPin.getLayoutParams();
                layoutParams.bottomMargin = insetBottom;
                binding.keyboardPin.setLayoutParams(layoutParams);
            });
        }

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fingerprintAuthHelper != null) {
            fingerprintAuthHelper.stopListening();
        }
        if (dialogFingerprint != null && dialogFingerprint.isVisible()) {
            dialogFingerprint.dismiss();
        }
    }

    @Override
    public void onPinInserted(String pin) {
        //Per gestire pressione compulsiva e doppia chiamata server
        if (canCallPinOperation) {

            canCallPinOperation = false;

            if (isDispositiveOperation || isAccessRequestOperation || isWithDrawalOperation || isDirectDebitOperation || isCashbackOperation) {

                if (isProviderAuthorization) {
                    operationType = BANKID;
                    operationId = accessRequestId;
                    amount = "";
                    msisdnSender = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();
                    receiver = accessRequestTag;
                } else if (isOperationAuthentication || isWithdrawalOperationAuthorization) {
                    operationType = bcmOperationExtra.getOperation();
                    operationId = bcmOperationExtra.getPaymentId();
                    amount = bcmOperationExtra.getAmount();
                    msisdnSender = bcmOperationExtra.getSender();
                    receiver = bcmOperationExtra.getReceiver();

                    if (operationType == PAYMENT) {
                        if (getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2B, false)) {
                            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_PIN_AUTH, null, true);
                        } else if (getIntent().getBooleanExtra(PAYMENT_AUTHENTICATION_P2P, false)) {
                            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_PIN_AUTH, null, true);
                        }
                    }
                } else if (isCashbackAuthorization) {
                    operationType = CASHBACK;
                }

                LoaderHelper.showLoader(this);
                Task<?> t = BancomatPayApiInterface.Factory.getInstance()
                        .doGetAuthorizationToken(result -> {

                                    canCallPinOperation = true;

                                    if (result != null) {
                                        if (result.isSuccess()) {

                                            if (isCashbackAuthorization) {

                                                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_PIN_AUTH, null, false);

                                                switch (cashbackAuthorizationTypeRequest) {
                                                    case SUBSCRIBE_BCM_CASHBACK_TOOL:
                                                        doSubscribeUserForCashback(result.getResult().getAuthorizationToken());
                                                        break;
                                                    case UNSUBSCRIBE_BCM_CASHBACK_TOOL:
                                                        doUnsubscribeUserForCashback(result.getResult().getAuthorizationToken());
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

                                        } else if (result.isSessionExpired()) {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                        } else {
                                            if (result.getResult() != null
                                                    && !TextUtils.isEmpty(result.getResult().getLastAttempts())
                                                    && Integer.parseInt(result.getResult().getLastAttempts()) > 0) {
                                                binding.textError.setText(getString(R.string.insert_pin_last_attempts, result.getResult().getLastAttempts()));
                                                if (binding.textError.getVisibility() != View.VISIBLE) {
                                                    AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
                                                }
                                                hasError = true;
                                            } else if (result.getStatusCode() == EXIT_APP) {
                                                showError(result.getStatusCode());
                                            } else if (result.getStatusCode() == P2P_USER_NOT_ENABLED_ON_BCM_PAY) {
                                                showError(result.getStatusCode());
                                            }
                                            binding.labelPin.shake();
                                            binding.keyboardPin.reset();
                                        }
                                    }
                                },
                                new AuthenticationData(pin),
                                operationType,
                                operationId,
                                amount,
                                msisdnSender,
                                receiver);
                t.setMasterListener(this);

                addTask(t);

            } else {

                LoaderHelper.showLoader(this);
                Task<?> t = BancomatPayApiInterface.Factory.getInstance().doLogin(result -> {

                    canCallPinOperation = true;

                    if (result != null) {
                        if (result.isSuccess()) {
                            goToNextPage();
                        } else {
                            if (result.getResult() != null && result.getResult().getLastAttempts() > 0) {
                                binding.textError.setText(
                                        getString(R.string.insert_pin_last_attempts, String.valueOf(result.getResult().getLastAttempts())));
                                if (binding.textError.getVisibility() != View.VISIBLE) {
                                    AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
                                }

                                hasError = true;
                            } else if (result.getStatusCode() == EXIT_APP) {
                                showError(result.getStatusCode());
                            } else if (result.getStatusCode() == P2P_USER_NOT_ENABLED_ON_BCM_PAY) {
                                showError(result.getStatusCode());
                            }
                            binding.labelPin.shake();
                            binding.keyboardPin.reset();
                        }
                    }
                }, new AuthenticationData(pin));
                t.setMasterListener(this);

                addTask(t);

            }

        }

    }

    @Override
    public void onDeleteLongClicked() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(30, 100));
            } else {
                //deprecated in API 26
                v.vibrate(30);
            }
        }
    }

    @Override
    public void onStartEditing() {
        if (hasError) {
            hasError = false;
            AnimationFadeUtil.startFadeOutAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        }
    }

    private void clickFabUseFingerprint() {
        dialogFingerprint = new FingerprintAuthenticationDialog();
        dialogFingerprint.setCancelable(false);
        dialogFingerprint.show(getSupportFragmentManager(), FINGERPRINT_DIALOG_FRAGMENT_TAG);
        if (fingerprintAuthHelper != null) {
            fingerprintAuthHelper.startListening();
        }
    }

    @Override
    public void onFingerprintDialogCancelPressed() {
        if (fingerprintAuthHelper != null) {
            fingerprintAuthHelper.stopListening();
        }
        if (dialogFingerprint != null && dialogFingerprint.isVisible()) {
            dialogFingerprint.dismiss();
        }
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
        EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.ERROR,
                getString(R.string.common_check_fingerprint_authentication_failed)));
    }

    @Override
    public void manageAuthenticationSuccess(byte[] data) {

        if (data != null) {

            CustomLogger.d(TAG, "manageAuthenticationSuccess auth");
            EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.SUCCESS,
                    getString(R.string.common_check_fingerprint_authentication_success)));

            if (isDispositiveOperation || isAccessRequestOperation || isWithDrawalOperation || isDirectDebitOperation || isCashbackAuthorization) {

                //LoaderHelper.showLoader(this);
                if (isProviderAuthorization) {
                    operationType = BANKID;
                    operationId = accessRequestId;
                    amount = "";
                    msisdnSender = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();
                    receiver = accessRequestTag;
                } else if (isOperationAuthentication || isWithdrawalOperationAuthorization) {
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

                LoaderHelper.showLoader(this);
                Task<?> t = BancomatPayApiInterface.Factory.getInstance().doGetAuthorizationToken(result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    if (isCashbackAuthorization) {
                                        switch (cashbackAuthorizationTypeRequest) {
                                            case SUBSCRIBE_BCM_CASHBACK_TOOL:
                                                doSubscribeUserForCashback(result.getResult().getAuthorizationToken());
                                                break;
                                            case UNSUBSCRIBE_BCM_CASHBACK_TOOL:
                                                doUnsubscribeUserForCashback(result.getResult().getAuthorizationToken());
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
                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else {
                                    showError(result.getStatusCode());
                                }
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

                //LoaderHelper.showLoader(this);
                Task<?> t = BancomatPayApiInterface.Factory.getInstance().doLogin(result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            goToNextPagePostDelayed();
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                }, new AuthenticationFingerprintData(data));
                addTask(t);

            }

        } else {
            FingerprintDataManager.getInstance().delete();
            showErrorFingerprintAndUsePin(FINGERPRINT_MODIFIED, (dialog, which) -> binding.fabUseFingerprint.hide());
        }
    }

    private void doSubscribeUserForCashback(String authorizationToken) {
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

    private void doUnsubscribeUserForCashback(String authorizationToken) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
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
        Intent intentResult = new Intent();
        if (isOperationAuthentication) {
            intentResult.putExtra(BCM_OPERATION_EXTRA, bcmOperationExtra);
        } else if (isProviderAuthorization) {
            intentResult.putExtra(ACCESS_REQUEST_ID, accessRequestId);
        }
        intentResult.putExtra(AUTHORIZATION_TOKEN_EXTRA, authorizationToken);
        intentResult.putExtra(LOYALTY_TOKEN_EXTRA, loyaltyToken);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void goToResultCashbackOperation(ECashbackActivationResult result) {
        Intent intentResult = new Intent();
        intentResult.putExtra(CASHBACK_RESULT_EXTRA, result);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void goToNextPagePostDelayed() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        handler.postDelayed(() -> {
            if (dialogFingerprint != null && dialogFingerprint.isVisible()) {
                dialogFingerprint.dismissAllowingStateLoss();
            }
            goToNextPage();
        }, FingerprintAuthenticationDialog.SHORT_TIME_OUT);
    }

    private void goToNextPage() {
        if (!TextUtils.isEmpty(paymentDataDeeplink)) {

            BancomatFullStackSdkInterface.Factory.getInstance()
                    .startPaymentFromQrCode(
                            this, paymentDataDeeplink,
                            SessionManager.getInstance().getSessionToken(),
                            result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {
                                        CustomLogger.d(TAG, "startPaymentFromQrCode complete response = " + result.toString());
                                        finish();
                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                    } else {
                                        showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> finish());
                                    }
                                }
                            });

        } else {
            BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                    .startBancomatPayFlow(this, null, AppBancomatDataManager.getInstance().getTokens().getOauth());
            finish();
        }
    }

}
