package it.bancomatpay.sdk.manager.lifecycle;

import java.io.Serializable;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class ObserverWrapperSingle<E extends Serializable> implements SingleObserver<E> {

    private final static String TAG = ObserverWrapperSingle.class.getSimpleName();
    private MutableLiveSingle<E> mutableLiveSingle;

    public ObserverWrapperSingle(MutableLiveSingle<E> mutableLiveSingle) {
        this.mutableLiveSingle = mutableLiveSingle;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mutableLiveSingle.setRunning(true);
    }

    @Override
    public void onSuccess(E e) {
        mutableLiveSingle.setSuccess(e);
        mutableLiveSingle.setRunning(false);
    }

    @Override
    public void onError(Throwable e) {
        mutableLiveSingle.setError(e);
        CustomLogger.w(TAG, e);
        mutableLiveSingle.setRunning(false);
    }

}
