package it.bancomat.pay.consumer.network.security.signature;

import android.content.Context;

import java.security.InvalidParameterException;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.utilities.AppConstants;

public interface AppMessageSignature {

    /**
     * Calculate message signature
     *
     * @param message
     * @return
     */
    String getMessageSignature(String message, Context context) throws InvalidParameterException;

    class Factory {

        public static AppMessageSignature create(AppCmd cmd) {
            if (AppConstants.MESSAGE_SIGN_ENABLED) {
                try {
                    return (AppMessageSignature) cmd.messageSignatureClass.newInstance();
                } catch (Exception e) {
                    return new AppNoMessageSignature();
                }
            } else {
                return new AppNoMessageSignature();
            }
        }
    }

}
