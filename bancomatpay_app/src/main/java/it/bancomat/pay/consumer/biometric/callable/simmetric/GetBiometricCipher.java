package it.bancomat.pay.consumer.biometric.callable.simmetric;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.core.PayCore;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static it.bancomat.pay.consumer.biometric.SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE;

public class GetBiometricCipher implements Callable<Cipher> {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;
    private final byte[] iv;

    private BiometryException biometryException;

    private Cipher cipher;

    public GetBiometricCipher(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, byte[] iv) {
        this.biometric = biometric;
        this.activity = activity;
        this.iv = iv;
    }

    public GetBiometricCipher(FragmentActivity activity, BiometricPrompt.PromptInfo biometric) {
        this.activity = activity;
        this.biometric = biometric;
        this.iv = null;
    }

    @Override
    public Cipher call() throws Exception {


        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        keyStore.load(null);
        SecretKey key = new GetSecretKey().call();

        int canAuthenticate = BiometricManager.from(PayCore.getAppContext())
                .canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            throw new BiometryException(canAuthenticate, null);
        }

        final CountDownLatch latch = new CountDownLatch(1);
        Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                biometryException = new BiometryException(errorCode, errString);
                latch.countDown();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (result.getCryptoObject() != null) {
                        cipher = result.getCryptoObject().getCipher();
                    } else {
                        biometryException = new BiometryException(-99, null);
                    }
                } else {
                    try {
                        cipher = Cipher.getInstance(SecurityConstants.S_BIO_TRANSFORMATION);
                        init(cipher, key);
                    } catch (Exception e) {
                        biometryException = new BiometryException(-99, null);
                    }

                }
                latch.countDown();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //biometryException = new BiometryException(-999, null);
                //latch.countDown();
            }
        };

        final BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, callback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            cipher = Cipher.getInstance(SecurityConstants.S_BIO_TRANSFORMATION);
            init(cipher, key);
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = () -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                biometricPrompt.authenticate(biometric, new BiometricPrompt.CryptoObject(cipher));
            } else {
                biometricPrompt.authenticate(biometric);
            }

        };
        mainHandler.post(myRunnable);

        latch.await();

        if (biometryException != null) {
            throw biometryException;
        }



        return cipher;
    }

    private void init(Cipher cipher, SecretKey key) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (iv == null) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            AppBancomatDataManager.getInstance().putIvData(cipher.getIV());

        } else {
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        }
    }
}
