package it.bancomatpay.sdk.core;

public interface OnCompleteListener {
    void onComplete(Task<?>  task);
    void onCompleteWithError(Task<?>  task, Error e);
}
