package it.bancomat.pay.consumer.biometric.callable;



import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.security.keystore.UserNotAuthenticatedException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;

import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;

public class CheckKeyguard extends CallableVoid {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final int requestCode;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    public CheckKeyguard(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.activity = activity;
        this.biometric = biometric;
        this.requestCode = requestCode;
        this.activityResultLauncher = activityResultLauncher;
    }

    private BiometryException biometryException;
    private CountDownLatch latch;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityResult(@NotNull ActivityResult activityResult) {
        if (activityResult.getRequestCode() == requestCode) {
            if (activityResult.getResultCode() != FragmentActivity.RESULT_OK) {
                String message = "";
                if (activityResult.getData() != null) {
                    message = activityResult.getData().getDataString();
                }
                biometryException = new BiometryException(activityResult.getResultCode(), message);
            }
            latch.countDown();
        }
    }

    @Override
    public void execute() throws Exception {
        EventBus.getDefault().register(this);
        KeyguardManager mKeyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(biometric.getTitle(), biometric.getDescription());

        if (intent != null) {
            activityResultLauncher.launch(intent);
        } else {
            throw new UserNotAuthenticatedException();
        }

        latch = new CountDownLatch(1);

        latch.await();
        EventBus.getDefault().unregister(this);
        if (biometryException != null) {
            throw biometryException;
        }
    }

}