package it.bancomatpay.sdk.manager.task.model;

public interface AuthenticationInterface {
    byte[] getSeed();
    byte[] getHmacKey();
}
