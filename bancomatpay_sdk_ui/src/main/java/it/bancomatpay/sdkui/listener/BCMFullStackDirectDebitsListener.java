package it.bancomatpay.sdkui.listener;

public interface BCMFullStackDirectDebitsListener {
    void authenticationResult(boolean authenticated, String authToken);
}