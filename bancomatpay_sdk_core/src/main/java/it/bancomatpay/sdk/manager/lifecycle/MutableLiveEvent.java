package it.bancomatpay.sdk.manager.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.io.Serializable;

public class MutableLiveEvent<E extends Serializable> implements LiveEvent<E> {

    private transient boolean running;
    private boolean backupRunning;
    private transient MutableLiveData<ResponseEvent<E>> mutableLiveData;
    private ResponseEvent<E> responseEvent;

    private transient EventListener<E> eventListener;

    private MutableLiveData<ResponseEvent<E>> getMutableLiveData(){
        if(mutableLiveData == null){
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    public void postValue(E value) {
        responseEvent = new ResponseEvent<>(value);
        getMutableLiveData().postValue(responseEvent);
    }

    public void setValue(E value) {
        responseEvent = new ResponseEvent<>(value);
        getMutableLiveData().setValue(responseEvent);
    }

    @Override
    public void setListener(@NonNull LifecycleOwner owner, @NonNull EventListener<E> observer) {
        if(this.eventListener != null){
            getMutableLiveData().removeObserver(this.eventListener);
        }
        this.eventListener = observer;
        getMutableLiveData().observe(owner, observer);
    }

    @Override
    public boolean hasListener() {
        return getMutableLiveData().hasActiveObservers();
    }

    @Override
    public void removeListener() {
        if (eventListener != null) {
            getMutableLiveData().removeObserver(eventListener);
            eventListener = null;
        }
    }

    @Override
    public E getValue() {
        if(responseEvent != null && responseEvent.getValue() != null) {
            return responseEvent.getValue();
        }else {
            return null;
        }
    }

    protected boolean isConsumed(){
        if(responseEvent != null) {
            return responseEvent.isConsumed();
        }else {
            return true;
        }
    }

    @Override
    public synchronized void setRunning(boolean running) {
        this.running = running;
        this.backupRunning = running;
    }

    public boolean isAborted(){
        return running != backupRunning;
    }

    @Override
    public synchronized boolean isNotPending(){
        return !running && isConsumed();
    }
}
