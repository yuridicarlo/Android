package it.bancomat.pay.consumer.biometric.callable;

import android.content.Intent;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.AuthenticationFingerprintData;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;


public class GetAppAuthentication implements Callable<AppAuthenticationInterface> {

    private static final String TAG = GetAppAuthentication.class.getSimpleName();

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final int requestCode;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    public GetAppAuthentication(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.activity = activity;
        this.biometric = biometric;
        this.requestCode = requestCode;
        this.activityResultLauncher = activityResultLauncher;
    }

    @Override
    public AppAuthenticationInterface call() throws Exception {

        SecretKey aesKey;


        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            aesKey = new GetAesKeyFromKeyguard(activity, biometric, requestCode, activityResultLauncher).call();
        } else {
            aesKey = new GetAesKeyFromBiometric(activity, biometric).call();
        }

        byte[] iv = AppBancomatDataManager.getInstance().getAESIV();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipherDecrypt = Cipher.getInstance(SecurityConstants.CHIPER_AES_CBC_PKCS7PADDING);
        cipherDecrypt.init(Cipher.DECRYPT_MODE, aesKey, ivParameterSpec);
        byte[] encryptedBytes = AppBancomatDataManager.getInstance().getDataAppAuthentication();
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);
        CustomLogger.d(TAG, "plain " + Conversion.byteArrayToStringBase64(decrypted));

        return new AuthenticationFingerprintData(decrypted);
    }

}