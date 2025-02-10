package it.bancomatpay.sdkui.listener;

public interface BCMFullStackOperationHandlerListener {
    void authenticationOperationResult(boolean authenticated, String authToken, String loyaltyToken, String paymentId);
}