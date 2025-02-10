package it.bancomat.pay.consumer.touchid;

import android.annotation.TargetApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import javax.crypto.Cipher;

@TargetApi(23)
public class FingerprintAuthHelper extends FingerprintHelper {

    public FingerprintAuthHelper(AuthCallback listener) {
        super(listener);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        try {
            Cipher cipher = result.getCryptoObject().getCipher();

            byte[] data = FingerprintDataManager.getInstance().read(cipher);
            if(listener != null) {
                ((AuthCallback) listener).manageAuthenticationSuccess(data);
            }

        } catch (Exception e){
            if(listener != null) {
                listener.stateError(FingerprintDataManager.getInstance().getFingerprintState());
            }
        }
    }

    @Override
    boolean isStartable() {
        return FingerprintDataManager.getInstance().isFingerprintEnrolled();
    }

    @Override
    FingerprintManagerCompat.CryptoObject getCryptoObject() {
        Cipher cipher = FingerprintDataManager.getInstance().cipherInit(Cipher.DECRYPT_MODE);
        return new FingerprintManagerCompat.CryptoObject(cipher);
    }

    public interface AuthCallback extends FingerprintHelper.Callback{
        void manageAuthenticationSuccess(byte[] data);
    }

}
