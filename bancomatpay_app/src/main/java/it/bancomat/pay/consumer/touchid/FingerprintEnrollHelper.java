package it.bancomat.pay.consumer.touchid;

import android.annotation.TargetApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import javax.crypto.Cipher;

@TargetApi(23)
public class FingerprintEnrollHelper extends FingerprintHelper {

    private byte[] dataToStore;

    public FingerprintEnrollHelper(Callback listener, byte[] dataToStore) {
        super(listener);
        this.dataToStore = dataToStore;
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        try {
            Cipher cipher = result.getCryptoObject().getCipher();

            FingerprintDataManager.getInstance().store(cipher, dataToStore);
            if (listener != null) {
                ((EnrollCallback) listener).manageAuthenticationSuccess();
            }

        } catch (Exception e) {
            if (listener != null) {
                listener.stateError(FingerprintDataManager.getInstance().getFingerprintState());
            }
        }
    }

    public interface EnrollCallback extends FingerprintHelper.Callback {
        void manageAuthenticationSuccess();
    }

    @Override
    boolean isStartable() {
        FingerprintState state = FingerprintDataManager.getInstance().getFingerprintState();
        switch (state) {
            case ENABLED:
                FingerprintDataManager.getInstance().delete();
                return true;
            case DISABLED:
                return true;
            default:
                return false;

        }
    }

    @Override
    FingerprintManagerCompat.CryptoObject getCryptoObject() {
        FingerprintDataManager.getInstance().generateKey();
        Cipher cipher = FingerprintDataManager.getInstance().cipherInit(Cipher.ENCRYPT_MODE);
        return new FingerprintManagerCompat.CryptoObject(cipher);
    }

}
