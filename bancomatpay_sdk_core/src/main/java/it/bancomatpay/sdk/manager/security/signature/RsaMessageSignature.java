package it.bancomatpay.sdk.manager.security.signature;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Constants;

public class RsaMessageSignature implements MessageSignature, MessageSignatureVerify {

    @Override
    public String getMessageSignature(String message, Context context) throws InvalidParameterException {

        BancomatDataManager bcmDataManager = BancomatDataManager.getInstance();

        String pem = bcmDataManager.getMobileDevice().getKey();

        return SecurityHelperWrapper.getMessageSignature(pem, message);
    }

    @Override
    public boolean checkMessageSignature(String message, String signature) throws InvalidParameterException {
        String pem = Constants.PUBLIC_KEY;

        if (TextUtils.isEmpty(pem) || TextUtils.isEmpty(message) || TextUtils.isEmpty(signature)) {
            return false;
        }

        return SecurityHelperWrapper.checkMessageSignature(pem, message, signature);
    }
}
