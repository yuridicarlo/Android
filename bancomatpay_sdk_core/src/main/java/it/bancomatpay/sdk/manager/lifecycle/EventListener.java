package it.bancomatpay.sdk.manager.lifecycle;

import androidx.lifecycle.Observer;

import java.io.Serializable;

public abstract class EventListener<E extends Serializable> implements Observer<ResponseEvent<E>> {

    @Override
    public void onChanged(ResponseEvent<E> resultStateConsumableValue) {
        if(!resultStateConsumableValue.isConsumed()){
            onEvent(resultStateConsumableValue.consume());
        }
    }

    public abstract void onEvent(E t);
}
