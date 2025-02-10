package it.bancomat.pay.consumer.network.security.signature;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;

public class AppRsaMessageSignature implements AppMessageSignature, AppMessageSignatureVerify {

    @Override
    public String getMessageSignature(String message, Context context) throws InvalidParameterException {

        AppBancomatDataManager bcmDataManager = AppBancomatDataManager.getInstance();

        String pem = bcmDataManager.getMobileDevice().getKey();

        return SecurityHelperWrapper.getMessageSignature(pem, message);
    }

    @Override
    public boolean checkMessageSignature(String message, String signature) throws InvalidParameterException {
        String pem = AppConstants.PUBLIC_KEY;

        if (TextUtils.isEmpty(pem) || TextUtils.isEmpty(message) || TextUtils.isEmpty(signature)) {
            return false;
        }

        return SecurityHelperWrapper.checkMessageSignature(pem, message, signature);
    }
}
