package it.bancomat.pay.consumer.biometric.callable;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.security.keystore.UserNotAuthenticatedException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;

public class GetAesKeyFromKeyguard implements Callable<SecretKey> {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final int requestCode;
    private final ActivityResultLauncher<Intent> activityResultLauncher;


    public GetAesKeyFromKeyguard(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher) {
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
    public SecretKey call() throws Exception {

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

        KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        keyStore.load(null);
        Key key =  (PrivateKey) keyStore.getKey(SecurityConstants.BIOMETRIC_KEY_RSA_ALIAS, null);

        Cipher cipher = Cipher.getInstance(SecurityConstants.BIO_TRANSFORMATION_RSA);
        AlgorithmParameterSpec algorithmParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameterSpec);

        byte[] cipherKey = AppBancomatDataManager.getInstance().getBiometricAESKey();

        byte[] plainKey = cipher.doFinal(cipherKey);

        return new SecretKeySpec(plainKey, SecurityConstants.TYPE_AES);
    }

}