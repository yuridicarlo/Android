package it.bancomat.pay.consumer.biometric.callable.simmetric;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.security.keystore.UserNotAuthenticatedException;

import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.security.KeyStore;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;

public class GetKeyguardCipher implements Callable<Cipher> {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final byte [] iv;

    private BiometryException biometryException;

    private Cipher cipher;

    public GetKeyguardCipher(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, byte[] iv) {
        this.activity = activity;
        this.biometric = biometric;
        this.iv = iv;
    }


    public GetKeyguardCipher(FragmentActivity activity, BiometricPrompt.PromptInfo biometric) {
        this.activity = activity;
        this.biometric = biometric;
        this.iv = null;
    }

    private CountDownLatch latch;

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityResult(@NotNull ActivityResult activityResult) {
        if (activityResult.getRequestCode() == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
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
    public Cipher call() throws Exception {
        EventBus.getDefault().register(this);
        KeyguardManager mKeyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(biometric.getTitle(), biometric.getDescription());

        if (intent != null) {
            activity.startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
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
        SecretKey key = new GetSecretKey().call();

        Cipher cipher = Cipher.getInstance(SecurityConstants.S_BIO_TRANSFORMATION);
        if(iv == null) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            AppBancomatDataManager.getInstance().putIvData(cipher.getIV());

        }else {
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

        }
        return cipher;
    }
}
