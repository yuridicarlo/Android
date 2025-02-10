package it.bancomat.pay.consumer.extended.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import it.bancomat.pay.consumer.login.LoginFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.touchid.FingerprintDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintEnrollHelper;
import it.bancomat.pay.consumer.touchid.FingerprintState;
import it.bancomat.pay.consumer.touchid.dialog.FingerprintAuthenticationDialog;
import it.bancomat.pay.consumer.touchid.dialog.UpdateStatus;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.widget.LabelPasswordLogin;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityChangePinSettingsBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class SettingsChangePinActivity extends AppGenericErrorActivity
        implements LabelPasswordLogin.LabelListener, FingerprintEnrollHelper.EnrollCallback, FingerprintAuthenticationDialog.InteractionListener {

    private ActivityChangePinSettingsBinding binding;

    private static final String TAG = SettingsChangePinActivity.class.getSimpleName();

    private boolean hasError;
    private String oldPin;
    private String newPin;
    private Status status = Status.OLD_PIN;

    private Handler handler;

    private FingerprintAuthenticationDialog fingerprintDialog;
    private FingerprintEnrollHelper fingerprintEnrollHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePinSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.labelPin.changeColor();
        binding.keyboardPin.changeColor();

        binding. keyboardPin.setKeyboardListener(binding.labelPin);
        binding.labelPin.setMaxElements(5);
        binding.labelPin.setListener(this);

        binding.textTitle.setText(R.string.change_pin_activity_old_pin);

        binding.notRememberPin.setOnClickListener(new CustomOnClickListener(v -> LoginFlowManager.goToLostPin(this, false)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fingerprintEnrollHelper != null) {
            fingerprintEnrollHelper.stopListening();
        }
        if (fingerprintDialog != null && fingerprintDialog.isVisible()) {
            fingerprintDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onPinInserted(String pin) {

        switch (status) {

            case OLD_PIN:
                this.oldPin = pin;
                binding.textTitle.setText(R.string.change_pin_activity_new_pin);
                status = Status.NEW_PIN;
                resetKeyboard();
                break;

            case NEW_PIN:
                BancomatPayApiInterface sdk = BancomatPayApiInterface.Factory.getInstance();
                if (oldPin.equals(pin)) {
                    hasError = true;
                    binding.textError.setText(R.string.change_pin_activity_new_pin_identical);
                    if (binding.textError.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
                    }
                } else if (sdk.isValidPin(pin)) {
                    newPin = pin;
                    binding.textTitle.setText(R.string.change_pin_activity_repeat_pin);
                    status = Status.REPEAT_NEW_PIN;
                } else {
                    hasError = true;
                    binding.textError.setText(R.string.invalid_pin_format);
                    if (binding.textError.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
                    }
                }
                resetKeyboard();
                break;

            case REPEAT_NEW_PIN:

                if (newPin.equals(pin)) {
                    LoaderHelper.showLoader(this);
                    Task<?> t = BancomatPayApiInterface.Factory.getInstance().doModifyPinTask(result -> {
                        binding.keyboardPin.reset();
                        if (result != null) {
                            if (result.isSuccess()) {
                                binding.toolbarSimple.setLeftImageVisibility(false);
                                binding.keyboardPin.changeColor();
                                if (result.getResult().getFingerprintData() != null) {

                                    byte[] bytes = result.getResult().getFingerprintData();
                                    fingerprintEnrollHelper = new FingerprintEnrollHelper(this, bytes);
                                    fingerprintEnrollHelper.startListening();
                                    fingerprintDialog = new FingerprintAuthenticationDialog();
                                    fingerprintDialog.show(getSupportFragmentManager(), "");

                                } else {
                                    goToNextPagePostDelayed();
                                }
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                status = Status.OLD_PIN;
                                if (result.getResult() != null && result.getResult().getLastAttempts() > 0) {
                                    binding.textError.setText(
                                            getString(R.string.insert_pin_last_attempts, String.valueOf(result.getResult().getLastAttempts()))
                                    );
                                    if (binding.textError.getVisibility() != View.VISIBLE) {
                                        AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
                                    }
                                } else {
                                    showError(result.getStatusCode());
                                }
                                hasError = true;
                                binding.labelPin.shake();
                                binding.keyboardPin.reset();
                            }
                        } else {
                            status = Status.OLD_PIN;
                            binding.textTitle.setText(R.string.change_pin_activity_old_pin);
                        }
                    }, oldPin, newPin);
                    addTask(t);

                } else {
                    hasError = true;
                    status = Status.NEW_PIN;
                    binding.textTitle.setText(R.string.change_pin_activity_new_pin);
                    binding.textError.setText(R.string.pin_not_corresponding);
                    if (binding.textError.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION);
                    }
                    resetKeyboard();
                }
                break;
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
            switch (status) {
                case NEW_PIN:
                    binding.textError.setText(R.string.change_pin_activity_new_pin);
                    break;
                case OLD_PIN:
                    binding.textError.setText(R.string.change_pin_activity_old_pin);
                    break;
            }
            AnimationFadeUtil.startFadeOutAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        }
    }

    @Override
    public void manageAuthenticationSuccess() {
        EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.SUCCESS, getString(R.string.common_check_fingerprint_authentication_success)));
        goToNextPagePostDelayed();
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
    public void onFingerprintDialogCancelPressed() {
        if (fingerprintEnrollHelper != null) {
            fingerprintEnrollHelper.stopListening();
        }
        if (fingerprintDialog != null && fingerprintDialog.isVisible()) {
            fingerprintDialog.dismissAllowingStateLoss();
        }
        FingerprintDataManager.getInstance().delete();
        goToNextPage();
    }

    private void resetKeyboard() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        handler.postDelayed(() -> binding.keyboardPin.reset(), 300);
    }

    private void goToNextPagePostDelayed() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        handler.postDelayed(this::goToNextPage, FingerprintAuthenticationDialog.SHORT_TIME_OUT);
    }

    private void goToNextPage() {
        Intent intent = new Intent(this, SettingsChangePinSuccessActivity.class);
        startActivity(intent);
        finish();
    }

    public enum Status {
        OLD_PIN,
        NEW_PIN,
        REPEAT_NEW_PIN
    }

}
