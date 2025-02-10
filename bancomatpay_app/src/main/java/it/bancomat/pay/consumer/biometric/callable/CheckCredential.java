package it.bancomat.pay.consumer.biometric.callable;

import android.content.Intent;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import it.bancomat.pay.consumer.network.dto.response.CallableVoid;

public class CheckCredential extends CallableVoid {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final int requestCode;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    public CheckCredential(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.activity = activity;
        this.biometric = biometric;
        this.requestCode = requestCode;
        this.activityResultLauncher = activityResultLauncher;
    }


    @Override
    public void execute() throws Exception {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            new CheckKeyguard(activity, biometric, requestCode, activityResultLauncher).call();
        } else {
            new CheckBiometric(activity, biometric).call();
        }
    }
}
