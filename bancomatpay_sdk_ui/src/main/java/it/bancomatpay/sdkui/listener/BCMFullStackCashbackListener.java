package it.bancomatpay.sdkui.listener;

import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;

public interface BCMFullStackCashbackListener {
    void authenticationResult(boolean authenticated, ECashbackActivationResult result);
}