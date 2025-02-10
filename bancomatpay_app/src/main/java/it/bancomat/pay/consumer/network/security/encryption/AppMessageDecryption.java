package it.bancomat.pay.consumer.network.security.encryption;

import android.content.Context;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.utilities.AppConstants;

public interface AppMessageDecryption {

    /**
     * Decrypt message
     *
     * @param chiperText
     * @return
     */
    String getDecryptedMessage(String chiperText, Context context);

    class Factory {

        public static AppMessageDecryption create(AppCmd cmd) {
            if (AppConstants.MESSAGE_DECRYPTION_ENABLED) {
                try {
                    return (AppMessageDecryption) cmd.messageDecryptionClass.newInstance();
                } catch (Exception e) {
                    return new AppNoMessageEncryption();
                }
            } else {
                return new AppNoMessageEncryption();
            }
        }
    }

}
