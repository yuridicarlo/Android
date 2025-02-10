package it.bancomatpay.sdk.manager.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.io.Serializable;

public class MutableLiveSingle<E extends Serializable> implements LiveSingle<E> {

    private final MutableLiveEvent<ResponseSingle<E>> mutableLiveEvent;

    public MutableLiveSingle() {
        mutableLiveEvent = new MutableLiveEvent<>();
    }

    public void postSuccess(E value) {
        mutableLiveEvent.postValue(new ResponseSingle<E>(value));
    }

    public void setSuccess(E value) {
        mutableLiveEvent.setValue(new ResponseSingle<E>(value));
    }

    public void postError(Throwable value) {
        mutableLiveEvent.postValue(new ResponseSingle<E>(value));
    }

    public void setError(Throwable value) {
        mutableLiveEvent.setValue(new ResponseSingle<E>(value));
    }

    @Override
    public void setListener(@NonNull LifecycleOwner owner, @NonNull SingleListener<E> observer) {
        mutableLiveEvent.setListener(owner, observer);
    }

    @Override
    public boolean hasListener() {
        return mutableLiveEvent.hasListener();
    }

    @Override
    public void removeListener() {
        mutableLiveEvent.removeListener();
    }

    @Override
    public E getValue() {
        if(mutableLiveEvent.getValue() != null) {
            return mutableLiveEvent.getValue().getValue();
        }else {
            return null;
        }
    }

    @Override
    public Throwable getThrowable() {
        if(mutableLiveEvent.getValue() != null) {
            return mutableLiveEvent.getValue().getThrowable();
        }else {
            return null;
        }
    }

    @Override
    public boolean hasExecuted() {
        if(mutableLiveEvent.getValue() != null) {
            return mutableLiveEvent.getValue().getValue() != null;
        }else {
            return false;
        }
    }

    @Override
    public synchronized void setRunning(boolean running) {
        mutableLiveEvent.setRunning(running);
    }


    @Override
    public synchronized boolean isNotPending(){
        return mutableLiveEvent.isNotPending();
    }

}
