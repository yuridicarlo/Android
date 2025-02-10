package it.bancomatpay.sdk.core;

public interface SecurityStorageHelperInterface {

    String encrypt(byte[] seed, String cleartext) throws Exception;

    String decrypt(byte[] seed, String encrypted) throws Exception;

}
