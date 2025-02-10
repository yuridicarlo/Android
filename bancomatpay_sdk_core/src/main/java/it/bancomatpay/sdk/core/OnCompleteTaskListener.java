package it.bancomatpay.sdk.core;

public interface OnCompleteTaskListener<E> {
    void onComplete(E result);
}
