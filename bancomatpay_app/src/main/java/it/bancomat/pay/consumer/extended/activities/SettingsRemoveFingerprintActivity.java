package it.bancomat.pay.consumer.extended.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import it.bancomat.pay.consumer.login.LoginFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.AuthenticationData;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.widget.LabelPasswordLogin;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivitySetFingerprintSettingsBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class SettingsRemoveFingerprintActivity extends AppGenericErrorActivity
        implements LabelPasswordLogin.LabelListener {

    private ActivitySetFingerprintSettingsBinding binding;

    private boolean hasError;

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
    public void onPinInserted(String pin) {

        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doVerifyPin(result -> {
            if (result != null) {
                if (result.isSuccess()) {

                    BancomatPayApiInterface.Factory.getInstance().removeTouchId();
                    finish();

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

}
