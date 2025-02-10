package it.bancomat.pay.consumer.biometric.callable.simmetric;

import android.os.Build;

import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Callable;

import javax.crypto.Cipher;

import it.bancomat.pay.consumer.biometric.callable.StoreDataAppAuthentication;
import it.bancomat.pay.consumer.network.dto.AuthenticationFingerprintData;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class GetBiometricData implements Callable<AuthenticationFingerprintData> {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final static String TAG = StoreDataAppAuthentication.class.getSimpleName();

    public GetBiometricData(BiometricPrompt.PromptInfo biometric, FragmentActivity activity) {
        this.biometric = biometric;
        this.activity = activity;
    }

    @Override
    public AuthenticationFingerprintData call() throws Exception {
        Cipher cipher;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            cipher = new GetKeyguardCipher(activity, biometric,  AppBancomatDataManager.getInstance().getIvData()).call();

        } else {
            cipher = new GetBiometricCipher(activity, biometric, AppBancomatDataManager.getInstance().getIvData()).call();

        }

        byte[] encryptedBytes = AppBancomatDataManager.getInstance().getDataAppAuthentication();
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        CustomLogger.d(TAG, "plain " + Conversion.byteArrayToStringBase64(decrypted));

        return new AuthenticationFingerprintData(decrypted);


    }




}
