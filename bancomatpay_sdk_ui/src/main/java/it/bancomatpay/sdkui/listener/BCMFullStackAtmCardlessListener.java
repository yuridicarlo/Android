package it.bancomatpay.sdkui.listener;

public interface BCMFullStackAtmCardlessListener {
    void authenticationResult(boolean authenticated, String authToken);
}