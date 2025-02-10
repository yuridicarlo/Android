package it.bancomat.pay.consumer.biometric.callable.simmetric;

import android.os.Build;

import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import javax.crypto.Cipher;

import it.bancomat.pay.consumer.biometric.callable.StoreDataAppAuthentication;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;

public class StoreBiometricData extends CallableVoid {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final byte[] data;
    private final static String TAG = StoreDataAppAuthentication.class.getSimpleName();

    public StoreBiometricData(BiometricPrompt.PromptInfo biometric, FragmentActivity activity, byte[] data) {
        this.biometric = biometric;
        this.activity = activity;
        this.data = data;
    }

    @Override
    public void execute() throws Exception {
        Cipher cipher;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            cipher = new GetKeyguardCipher(activity, biometric).call();

        } else {
            cipher = new GetBiometricCipher(activity, biometric).call();

        }

        byte[] newPaymentTokenCipher = cipher.doFinal(data);

        AppBancomatDataManager.getInstance().putDataAppAuthentication(newPaymentTokenCipher);


    }



}
