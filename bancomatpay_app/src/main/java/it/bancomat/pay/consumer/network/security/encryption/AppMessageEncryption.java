package it.bancomat.pay.consumer.network.security.encryption;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.utilities.AppConstants;

public interface AppMessageEncryption {

    /**
     * Encrypt message
     *
     * @param plainText
     * @return
     */
    String getEncryptedMessage(String plainText);

    class Factory {

        public static AppMessageEncryption create(AppCmd cmd) {
            if (AppConstants.MESSAGE_ENCRYPTION_ENABLED) {
                try {
                    return (AppMessageEncryption) cmd.messageEncryptionClass.newInstance();
                } catch (Exception e) {
                    return new AppNoMessageEncryption();
                }
            }
            return new AppNoMessageEncryption();
        }
    }

}
