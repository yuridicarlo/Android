package it.bancomat.pay.consumer.biometric;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.CompositeDisposable;
import it.bancomat.pay.consumer.biometric.callable.CheckCredential;
import it.bancomat.pay.consumer.biometric.callable.StoreDataAppAuthentication;
import it.bancomat.pay.consumer.extended.activities.HomeActivityExtended;
import it.bancomat.pay.consumer.extended.activities.SettingsChangePinActivity;
import it.bancomat.pay.consumer.login.LoginFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.widget.LabelPasswordLogin;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityChangePinSettingsBinding;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.lifecycle.CompletableListener;
import it.bancomatpay.sdk.manager.lifecycle.LiveUtil;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveCompletable;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class EnrollBiometricActivity extends AppGenericErrorActivity
        implements LabelPasswordLogin.LabelListener {

    private ActivityChangePinSettingsBinding binding;

    private MutableLiveCompletable checkCredentialResponse;
    private MutableLiveCompletable storeAppAuthenticationResponse;

    private static final String TAG = SettingsChangePinActivity.class.getSimpleName();

    private boolean hasError;
    CompositeDisposable compositeSubscription;

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    ActivityResultLauncher<Intent> activityResultLauncherDeviceCredentials = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                EventBus.getDefault().post(new ActivityResult(REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, result.getResultCode(), data));
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePinSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        compositeSubscription = new CompositeDisposable();
        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.labelPin.changeColor();
        binding.keyboardPin.changeColor();

        binding. keyboardPin.setKeyboardListener(binding.labelPin);
        binding.labelPin.setMaxElements(5);
        binding.labelPin.setListener(this);

        binding.notRememberPin.setOnClickListener(new CustomOnClickListener(v -> LoginFlowManager.goToLostPin(this, false)));
        checkCredentialResponse = new MutableLiveCompletable();
        storeAppAuthenticationResponse = new MutableLiveCompletable();
    }


    @Override
    public void onPinInserted(String pin) {

        checkCredentialResponse.setListener(this, new CompletableListener() {
            @Override
            protected void onComplete() {
                LoaderHelper.showLoader(EnrollBiometricActivity.this);
                Task<?> t = BancomatPayApiInterface.Factory.getInstance().doMigratePinTask(result -> {
                    binding.keyboardPin.reset();
                    if (result != null) {
                        if (result.isSuccess()) {
                            binding.toolbarSimple.setLeftImageVisibility(false);
                            binding.keyboardPin.changeColor();
                            byte[] bytes = result.getResult().getFingerprintData();

                            BiometricEnrollData biometricEnrollData = new BiometricEnrollData();
                            biometricEnrollData.setBiometricData(bytes);
                            LiveUtil.fromCallable(new StoreDataAppAuthentication(biometricEnrollData.getBiometricData()))
                                    .subscribe(LiveUtil.getWrapper(storeAppAuthenticationResponse));



                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(EnrollBiometricActivity.this, BCMAbortCallback.getInstance().getSessionRefreshListener());
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
                    } else {
                        binding.textTitle.setText(R.string.change_pin_activity_old_pin);
                    }
                }, pin);
                addTask(t);
            }

            @Override
            public void onError(Throwable throwable) {
                binding.labelPin.shake();
                binding.keyboardPin.reset();
            }
        });

        storeAppAuthenticationResponse.setListener(this, new CompletableListener() {
            @Override
            protected void onComplete() {
                Intent intentHome = new Intent(EnrollBiometricActivity.this, HomeActivityExtended.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                EnrollBiometricActivity.this.startActivity(intentHome);
            }

            @Override
            public void onError(Throwable throwable) {
                CustomLogger.d(TAG, throwable);
                showError(StatusCode.Mobile.GENERIC_ERROR);
            }
        });

        LiveUtil.fromCallable(new CheckCredential(this, PromptInfoHelper.getPromptInfo(), REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, activityResultLauncherDeviceCredentials))
                .subscribe(LiveUtil.getWrapper(checkCredentialResponse));


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
            binding.textError.setText(R.string.change_pin_activity_old_pin);
            AnimationFadeUtil.startFadeOutAnimationV1(binding.textError, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(compositeSubscription != null && !compositeSubscription.isDisposed()){
            compositeSubscription.dispose();
        }
    }
}
