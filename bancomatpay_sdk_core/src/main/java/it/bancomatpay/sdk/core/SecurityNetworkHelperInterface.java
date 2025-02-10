package it.bancomatpay.sdk.core;

public interface SecurityNetworkHelperInterface {

    String decryptMessage(String pem, String chiperText);

    String encryptMessage(String pem, String plainText);

    String getMessageSignature(String pem, String message);

    boolean checkMessageSignature(String pem, String message, String signature);

}
