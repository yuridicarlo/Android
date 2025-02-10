package it.bancomat.pay.consumer.biometric.callable;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.sdk.core.PayCore;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static it.bancomat.pay.consumer.biometric.SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE;

public class GetAesKeyFromBiometric implements Callable<SecretKey> {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;

    private BiometryException biometryException;

    private Cipher cipher;

    public GetAesKeyFromBiometric(FragmentActivity activity, BiometricPrompt.PromptInfo biometric) {
        this.activity = activity;
        this.biometric = biometric;
    }

    @Override
    public SecretKey call() throws Exception {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        keyStore.load(null);
        Key key = keyStore.getKey(SecurityConstants.BIOMETRIC_KEY_RSA_ALIAS, null);

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
                        cipher = Cipher.getInstance(SecurityConstants.BIO_TRANSFORMATION_RSA);
                        AlgorithmParameterSpec algorithmParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
                        cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameterSpec);
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
            cipher = Cipher.getInstance(SecurityConstants.BIO_TRANSFORMATION_RSA);
            AlgorithmParameterSpec algorithmParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameterSpec);
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

        byte[] cipherKey = AppBancomatDataManager.getInstance().getBiometricAESKey();

        byte[] plainKey = cipher.doFinal(cipherKey);

        return new SecretKeySpec(plainKey, SecurityConstants.TYPE_AES);
    }

}