package it.bancomat.pay.consumer.network.security.encryption;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AppRsaMessageDefaultDecryption implements AppMessageDecryption {

    @Override
    public String getDecryptedMessage(String chiperText, Context context) {

        String pem = AppConstants.PRIVATE_KEY_DEFAULT;
        if (!TextUtils.isEmpty(AppBancomatDataManager.getInstance().getMobileDevice().getKey())) {
            pem = AppBancomatDataManager.getInstance().getMobileDevice().getKey();
        }

        String decryptedMessage = "";
        try {
            decryptedMessage = SecurityHelperWrapper.decryptMessage(pem, chiperText);
        } catch (InvalidParameterException e) {
            if (TextUtils.isEmpty(pem)) {
                CustomLogger.e("AppMessageCipherException", "Pem is empty");
            } else if (TextUtils.isEmpty(chiperText)) {
                CustomLogger.e("AppMessageCipherException", "CipherText is empty");
            }
        }

        return decryptedMessage;
    }

}
