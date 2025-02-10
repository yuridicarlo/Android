package it.bancomatpay.sdk.manager.security.signature;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;
import it.bancomatpay.sdk.manager.utilities.Constants;

public class RsaMessageDefaultSignature implements MessageSignature {

    @Override
    public String getMessageSignature(String message, Context context) throws InvalidParameterException {

        String pem = Constants.PRIVATE_KEY_DEFAULT;

        String messageSignature = "";
        if (!TextUtils.isEmpty(message)) {
            messageSignature = SecurityHelperWrapper.getMessageSignature(pem, message);
        }

        return messageSignature;
    }

}
