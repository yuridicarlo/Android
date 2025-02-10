package it.bancomat.pay.consumer.extended.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomat.pay.consumer.biometric.PromptInfoHelper;
import it.bancomat.pay.consumer.biometric.callable.GetAppAuthentication;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.lifecycle.LiveUtil;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveSingle;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdkui.activities.SettingsActivity;

public class SettingsActivityExtended extends SettingsActivity {

    private SwitchCompat switchEnableLogin;
    private MutableLiveSingle<AppAuthenticationInterface> getAppAuthenticationResponse;

    CompoundButton.OnCheckedChangeListener enableLogin = (buttonView, isChecked) -> {
        if(isChecked) {
            showBiometric();
        }else {
            showWarningDialog();
        }
    };

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    ActivityResultLauncher<Intent> activityResultLauncherDeviceCredentials = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                EventBus.getDefault().post(new ActivityResult(REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, result.getResultCode(), data));
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switchEnableLogin = findViewById(R.id.switch_enable_login);

        getAppAuthenticationResponse = new MutableLiveSingle<>();
        getAppAuthenticationResponse.setListener(this, new SingleListener<AppAuthenticationInterface>() {
            @Override
            public void onSuccess(AppAuthenticationInterface response) {
                BancomatPayApiInterface.Factory.getInstance().setLoginRequired(switchEnableLogin.isChecked());
            }

            @Override
            public void onError(Throwable throwable) {
                switchEnableLogin.setOnCheckedChangeListener(null);
                switchEnableLogin.setChecked(BancomatPayApiInterface.Factory.getInstance().isLoginRequired());
                switchEnableLogin.setOnCheckedChangeListener(enableLogin);
            }
        });

        switchEnableLogin.setOnCheckedChangeListener(null);
        switchEnableLogin.setChecked(BancomatPayApiInterface.Factory.getInstance().isLoginRequired());
        switchEnableLogin.setOnCheckedChangeListener(enableLogin);
        switchEnableLogin.setEnabled(BancomatPayApiInterface.Factory.getInstance().isBiometricConfigured());
    }

    private void showBiometric() {
        if (getAppAuthenticationResponse.isNotPending()) {
            Single.fromCallable(new GetAppAuthentication(SettingsActivityExtended.this, PromptInfoHelper.getPromptInfo(), REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, activityResultLauncherDeviceCredentials))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(LiveUtil.getWrapper(getAppAuthenticationResponse));
        }
    }


    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.warning_title));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.settings_extended_disable_login_waring))
                .setPositiveButton(R.string.ok, (dialog, which) -> showBiometric())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    switchEnableLogin.setOnCheckedChangeListener(null);
                    switchEnableLogin.setChecked(true);
                    switchEnableLogin.setOnCheckedChangeListener(enableLogin);
                });
        builder.show();
    }
}
