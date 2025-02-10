package it.bancomat.pay.consumer.network.security.signature;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;

public class AppRsaMessageDefaultSignature implements AppMessageSignature {

    @Override
    public String getMessageSignature(String message, Context context) throws InvalidParameterException {

        String pem = AppConstants.PRIVATE_KEY_DEFAULT;

        String messageSignature = "";
        if (!TextUtils.isEmpty(message)) {
            messageSignature = SecurityHelperWrapper.getMessageSignature(pem, message);
        }

        return messageSignature;
    }

}
