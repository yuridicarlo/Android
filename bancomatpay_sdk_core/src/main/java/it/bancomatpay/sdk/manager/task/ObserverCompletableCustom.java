package it.bancomatpay.sdk.manager.task;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;

public class ObserverCompletableCustom implements CompletableObserver {

    OnNetworkCompleteListener<DtoAppResponse<Void>> listener;

    public ObserverCompletableCustom(OnNetworkCompleteListener<DtoAppResponse<Void>> listener) {
        this.listener = listener;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {
        listener.onComplete(new DtoAppResponse<>());
    }

    @Override
    public void onError(Throwable e) {
        if(e instanceof ErrorException){
            ErrorException errorException = (ErrorException) e;
            listener.onCompleteWithError(errorException.getError());
        }

    }
}
