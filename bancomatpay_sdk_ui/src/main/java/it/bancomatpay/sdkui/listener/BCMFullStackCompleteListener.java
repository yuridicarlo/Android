package it.bancomatpay.sdkui.listener;

import it.bancomatpay.sdk.Result;

public interface BCMFullStackCompleteListener {
    void onComplete(Result<Void> result);
}
