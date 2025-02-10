package it.bancomatpay.sdk.manager.task;

public interface OnProgressResultListener<E> extends OnCompleteResultListener<E> {

    void onProgress();

}
