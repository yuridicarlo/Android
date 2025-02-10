package it.bancomatpay.sdk.manager.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import it.bancomatpay.sdk.manager.model.VoidResponse;


public class MutableLiveCompletable implements LiveCompletable {

    private MutableLiveEvent<ResponseSingle<VoidResponse>> mutableLiveEvent;

    public MutableLiveCompletable() {
        mutableLiveEvent = new MutableLiveEvent<ResponseSingle<VoidResponse>>();
    }

    public void postSuccess(VoidResponse value) {
        mutableLiveEvent.postValue(new ResponseSingle<VoidResponse>(value));
    }

    public void setSuccess(VoidResponse value) {
        mutableLiveEvent.setValue(new ResponseSingle<VoidResponse>(value));
    }

    public void postError(Throwable value) {
        mutableLiveEvent.postValue(new ResponseSingle<VoidResponse>(value));
    }

    public void setError(Throwable value) {
        mutableLiveEvent.setValue(new ResponseSingle<VoidResponse>(value));
    }

    @Override
    public void setListener(@NonNull LifecycleOwner owner, @NonNull CompletableListener observer) {
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
    public boolean hasExecuted() {
        if(mutableLiveEvent.getValue() != null) {
            return mutableLiveEvent.getValue().getValue() != null;
        }else {
            return false;
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
    public void setRunning(boolean b) {
        mutableLiveEvent.setRunning(b);
    }

    @Override
    public boolean isNotPending() {
        return mutableLiveEvent.isNotPending();
    }

}
