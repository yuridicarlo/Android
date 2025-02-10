package it.bancomatpay.sdk.manager.security.encryption;

import android.content.Context;

import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.Constants;

public interface MessageDecryption {

    /**
     * Decrypt message
     *
     * @param chiperText
     * @return
     */
    String getDecryptedMessage(String chiperText, Context context);

    class Factory {

        public static MessageDecryption create(Cmd cmd) {
            if (Constants.MESSAGE_DECRYPTION_ENABLED) {
                try {
                    return (MessageDecryption) cmd.messageDecryptionClass.newInstance();
                } catch (Exception e) {
                    return new NoMessageEncryption();
                }

            } else {
                return new NoMessageEncryption();
            }
        }
    }

}
