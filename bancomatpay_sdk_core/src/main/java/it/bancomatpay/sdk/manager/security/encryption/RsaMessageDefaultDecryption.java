package it.bancomatpay.sdk.manager.security.encryption;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class RsaMessageDefaultDecryption implements MessageDecryption {

    @Override
    public String getDecryptedMessage(String chiperText, Context context) {

        String pem = Constants.PRIVATE_KEY_DEFAULT;
        if (!TextUtils.isEmpty(BancomatDataManager.getInstance().getMobileDevice().getKey())) {
            pem = BancomatDataManager.getInstance().getMobileDevice().getKey();
        }

        String decryptedMessage = "";
        try {
            decryptedMessage = SecurityHelperWrapper.decryptMessage(pem, chiperText);
        } catch (InvalidParameterException e) {
            if (TextUtils.isEmpty(pem)) {
                CustomLogger.e("MessageCipherException", "Pem is empty");
            } else if (TextUtils.isEmpty(chiperText)) {
                CustomLogger.e("MessageCipherException", "CipherText is empty");
            }
        }

        return decryptedMessage;
    }

}
