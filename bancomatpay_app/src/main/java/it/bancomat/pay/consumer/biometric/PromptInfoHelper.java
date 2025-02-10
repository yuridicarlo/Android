package it.bancomat.pay.consumer.biometric;

import android.content.Context;
import android.os.Build;

import androidx.biometric.BiometricPrompt;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.core.PayCore;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class PromptInfoHelper {


    public static BiometricPrompt.PromptInfo getPromptInfo() {
        Context context = PayCore.getAppContext();
        BiometricPrompt.PromptInfo.Builder promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.biometric_prompt_title))
                .setSubtitle(context.getString(R.string.biometric_prompt_subtitle));
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            promptInfo.setAllowedAuthenticators(BIOMETRIC_STRONG)
                    .setNegativeButtonText(context.getString(R.string.cancel));
        } else {
            promptInfo.setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        }
        return promptInfo.build();
    }
}
