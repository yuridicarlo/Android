package it.bancomat.pay.consumer.network.callable;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import it.bancomat.pay.consumer.biometric.callable.CheckCredential;
import it.bancomat.pay.consumer.biometric.callable.StoreDataAppAuthentication;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;

public class StoreBiometricEnrollData extends CallableVoid {

    private final static String TAG = StoreBiometricEnrollData.class.getSimpleName();
    private FragmentActivity fragmentActivity;
    private BiometricPrompt.PromptInfo biometricPrompt;
    private BiometricEnrollData biometricEnrollData;
    private final int requestCode;
    private final ActivityResultLauncher<Intent> activityResultLauncher;


    public StoreBiometricEnrollData(FragmentActivity fragmentActivity, BiometricPrompt.PromptInfo biometricPrompt, BiometricEnrollData biometricEnrollData, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.fragmentActivity = fragmentActivity;
        this.biometricPrompt = biometricPrompt;
        this.biometricEnrollData = biometricEnrollData;
        this.requestCode = requestCode;
        this.activityResultLauncher = activityResultLauncher;
    }

    @Override
    public void execute() throws Exception {
        new CheckCredential(fragmentActivity, biometricPrompt, requestCode, activityResultLauncher).execute();
        new StoreDataAppAuthentication(biometricEnrollData.getBiometricData()).call();
        AppBancomatDataManager.getInstance().putPskc(biometricEnrollData.getPskc());

        //new GetAuthorizationToken(new AuthenticationFingerprintData(biometricEnrollData.getBiometricData()), AuthenticationOperationType.BPLAY, null, null, null, null).call();


    }

}
