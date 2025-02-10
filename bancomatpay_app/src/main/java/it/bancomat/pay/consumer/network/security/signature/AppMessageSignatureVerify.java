package it.bancomat.pay.consumer.network.security.signature;

import java.security.InvalidParameterException;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.utilities.AppConstants;

public interface AppMessageSignatureVerify {

    /**
     * Check if the signature is correct
     *
     * @param message
     * @param signature
     * @return
     */
    boolean checkMessageSignature(String message, String signature) throws InvalidParameterException;

    class Factory {

        public static AppMessageSignatureVerify create(AppCmd cmd) {
            if (AppConstants.MESSAGE_VERIFICATION_ENABLED) {
                try {
                    return (AppMessageSignatureVerify) cmd.messageSignatureVerifyClass.newInstance();
                } catch (Exception e) {
                    return new AppNoMessageSignature();
                }

            } else {
                return new AppNoMessageSignature();
            }
        }
    }
}
