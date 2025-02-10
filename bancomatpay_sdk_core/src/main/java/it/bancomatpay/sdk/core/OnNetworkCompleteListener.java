package it.bancomatpay.sdk.core;

public interface OnNetworkCompleteListener<T> {
    void onComplete(T response);
    void onCompleteWithError(Error e);
}