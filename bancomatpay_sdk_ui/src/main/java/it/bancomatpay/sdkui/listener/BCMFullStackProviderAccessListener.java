package it.bancomatpay.sdkui.listener;

public interface BCMFullStackProviderAccessListener {
    void authenticationResult(boolean authenticated, String authToken);
}