package it.bancomat.pay.consumer.touchid;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import it.bancomat.pay.consumer.BancomatApplication;

@TargetApi(23)
public abstract class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;
    protected Callback listener;

    FingerprintHelper(Callback listener) {
        this.listener = listener;
    }

    public synchronized void startListening() {
        if (isStartable()) {
            mCancellationSignal = new CancellationSignal();
            mSelfCancelled = false;
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(BancomatApplication.getAppContext());
            if (ContextCompat.checkSelfPermission(BancomatApplication.getAppContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                fingerprintManager.authenticate(getCryptoObject(), 0, mCancellationSignal, this, null);
            }
        } else {
            if (listener != null) {
                listener.stateError(FingerprintDataManager.getInstance().getFingerprintState());
            }

        }
    }

    abstract boolean isStartable();

    abstract FingerprintManagerCompat.CryptoObject getCryptoObject();

    public synchronized void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public synchronized void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            if (listener != null) {
                listener.authenticationError(errString.toString());
            }
        }
        stopListening();
    }

    @Override
    public synchronized void onAuthenticationFailed() {
        if (!mSelfCancelled) {
            if (listener != null) {
                listener.authenticationFailed();
            }
        }
    }

    public interface Callback {
        void stateError(FingerprintState fingerprintState);

        void authenticationError(String errString);

        void authenticationFailed();
    }

}