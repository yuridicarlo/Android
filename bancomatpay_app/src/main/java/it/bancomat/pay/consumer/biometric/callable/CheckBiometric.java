package it.bancomat.pay.consumer.biometric.callable;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomatpay.sdk.core.PayCore;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class CheckBiometric extends CallableVoid {

    private final BiometricPrompt.PromptInfo biometric;
    private final FragmentActivity activity;

    private BiometryException biometryException;

    public CheckBiometric(FragmentActivity activity, BiometricPrompt.PromptInfo biometric) {
        this.activity = activity;
        this.biometric = biometric;
    }

    @Override
    public void execute() throws Exception {

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
                latch.countDown();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        };

        final BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, callback);


        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = () -> {
            biometricPrompt.authenticate(biometric);
        };
        mainHandler.post(myRunnable);

        latch.await();

        if (biometryException != null) {
            throw biometryException;
        }
    }


}