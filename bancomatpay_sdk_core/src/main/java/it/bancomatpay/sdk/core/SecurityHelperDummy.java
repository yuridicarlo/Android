package it.bancomatpay.sdk.core;

public class SecurityHelperDummy implements SecurityNetworkHelperInterface, SecurityStorageHelperInterface{
    @Override
    public String decryptMessage(String pem, String chiperText) {
        return chiperText;
    }

    @Override
    public String encryptMessage(String pem, String plainText) {
        return plainText;
    }

    @Override
    public String getMessageSignature(String pem, String message) {
        return "";
    }

    @Override
    public boolean checkMessageSignature(String pem, String message, String signature) {
        return true;
    }

    @Override
    public String encrypt(byte[] seed, String cleartext) throws Exception {
        return cleartext;
    }

    @Override
    public String decrypt(byte[] seed, String encrypted) throws Exception {
        return encrypted;
    }
}

