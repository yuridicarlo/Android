package it.bancomatpay.sdk.manager.security;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.core.SecurityHelperCBC;
import it.bancomatpay.sdk.core.SecurityNetworkHelperInterface;

public class SecurityHelperWrapper {

    private static SecurityNetworkHelperInterface instance;

    static {
        instance = new SecurityHelperCBC();
    }

    public static String decryptMessage(String pem, String chiperText) throws InvalidParameterException {
        return instance.decryptMessage(pem, chiperText);
    }

    public static String encryptMessage(String pem, String plainText) throws InvalidParameterException {
        return instance.encryptMessage(pem, plainText);
    }

    public static String getMessageSignature(String pem, String message) throws InvalidParameterException {
        return instance.getMessageSignature(pem, message);
    }

    public static boolean checkMessageSignature(String pem, String message, String signature) throws InvalidParameterException {
        return instance.checkMessageSignature(pem, message, signature);
    }

}
