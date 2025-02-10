package it.bancomat.pay.consumer.extended.activities;

import android.content.Context;
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
import it.bancomat.pay.consumer.network.dto.AuthenticationData;
import it.bancomat.pay.consumer.touchid.FingerprintDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintEnrollHelper;
import it.bancomat.pay.consumer.touchid.FingerprintState;
import it.bancomat.pay.consumer.touchid.dialog.FingerprintAuthenticationDialog;
import it.bancomat.pay.consumer.touchid.dialog.UpdateStatus;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.widget.LabelPasswordLogin;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivitySetFingerprintSettingsBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class SettingsSetFingerprintActivity extends AppGenericErrorActivity
        implements LabelPasswordLogin.LabelListener, FingerprintEnrollHelper.EnrollCallback, FingerprintAuthenticationDialog.InteractionListener {

    private ActivitySetFingerprintSettingsBinding binding;

    private static final String TAG = SettingsSetFingerprintActivity.class.getSimpleName();

    private boolean hasError;
    private Handler handler;

    private FingerprintAuthenticationDialog fingerprintDialog;
    private FingerprintEnrollHelper fingerprintEnrollHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetFingerprintSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.labelPin.changeColor();
        binding.keyboardPin.changeColor();

        binding.keyboardPin.setKeyboardListener(binding.labelPin);
        binding.labelPin.setMaxElements(5);
        binding.labelPin.setListener(this);

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
        binding.keyboardPin.reset();
    }

    @Override
    public void onPinInserted(String pin) {

        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doVerifyPin(result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    byte[] bytes = result.getResult().getFingerprintData();
                    fingerprintEnrollHelper = new FingerprintEnrollHelper(this, bytes);
                    fingerprintEnrollHelper.startListening();
                    fingerprintDialog = FingerprintAuthenticationDialog.newInstance(getString(R.string.use_fingerprint_description_generic));
                    fingerprintDialog.show(getSupportFragmentManager(), "");
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
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
            }
        }, new AuthenticationData(pin));
        addTask(t);

    }

    @Override
    public void onDeleteLongClicked() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(30, 100));
        } else {
            //deprecated in API 26
            v.vibrate(30);
        }
    }

    @Override
    public void onStartEditing() {
        if (hasError) {
            hasError = false;
            AnimationFadeUtil.startFadeOutAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        }
    }

    @Override
    public void manageAuthenticationSuccess() {
        //keyboardPin.reset();
        EventBus.getDefault().post(new UpdateStatus(UpdateStatus.Status.SUCCESS, getString(R.string.common_check_fingerprint_authentication_success)));
        goToNextPagePostDelayed();
    }

    private void goToNextPagePostDelayed() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        } else {
            handler.removeCallbacksAndMessages(null);
        }
        handler.postDelayed(this::finish, FingerprintAuthenticationDialog.LONG_TIME_OUT);
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
        binding.keyboardPin.reset();
    }

}
