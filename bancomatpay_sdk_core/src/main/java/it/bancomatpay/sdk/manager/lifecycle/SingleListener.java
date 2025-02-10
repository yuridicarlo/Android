package it.bancomatpay.sdk.manager.lifecycle;

import java.io.Serializable;

public abstract class SingleListener<E extends Serializable> extends EventListener<ResponseSingle<E>> {


    public void onEvent(ResponseSingle<E> t){
        if(t.getThrowable() == null){
            onSuccess(t.getValue());
        }else {
            onError(t.getThrowable());
        }
    }

    public abstract void onSuccess(E response);

    public abstract void onError(Throwable throwable);

}
