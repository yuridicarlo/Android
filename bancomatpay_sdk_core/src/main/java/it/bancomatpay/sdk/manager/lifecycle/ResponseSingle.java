package it.bancomatpay.sdk.manager.lifecycle;

import java.io.Serializable;

public class ResponseSingle<E extends Serializable> implements Serializable {

    private Throwable throwable;
    private E value;

    public ResponseSingle(Throwable throwable) {
        this.throwable = throwable;
    }

    public ResponseSingle(E value) {
        this.value = value;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public E getValue() {
        return value;
    }
}
