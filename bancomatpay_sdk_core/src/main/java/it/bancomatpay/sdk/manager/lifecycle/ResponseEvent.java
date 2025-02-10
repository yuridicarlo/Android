package it.bancomatpay.sdk.manager.lifecycle;

import java.io.Serializable;

public class ResponseEvent<E extends Serializable> implements Serializable {

    private E value;
    private boolean consumed;


    ResponseEvent(E value) {
        this.value = value;
        this.consumed = false;
    }

    public synchronized E consume(){
        if(!consumed){
            consumed = true;
            return value;
        }else {
            return null;
        }
    }

    public boolean isConsumed() {
        return consumed;
    }

    public E getValue() {
        return value;
    }
}
