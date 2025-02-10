package it.bancomatpay.sdk.manager.task;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;

public class ObserverSingleCustom<E> implements SingleObserver<E> {

    OnNetworkCompleteListener<E> listener;

    public ObserverSingleCustom(OnNetworkCompleteListener<E> listener) {
        this.listener = listener;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onSuccess(E e) {
        listener.onComplete(e);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ErrorException) {
            ErrorException errorException = (ErrorException) e;
            listener.onCompleteWithError(errorException.getError());
        }else {
            listener.onCompleteWithError(new ExtendedError(null, GENERIC_ERROR));
        }
    }
}
