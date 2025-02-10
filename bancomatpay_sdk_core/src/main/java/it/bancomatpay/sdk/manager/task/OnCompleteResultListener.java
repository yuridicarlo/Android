package it.bancomatpay.sdk.manager.task;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnCompleteTaskListener;

public interface OnCompleteResultListener<E> extends OnCompleteTaskListener<Result<E>> {

}