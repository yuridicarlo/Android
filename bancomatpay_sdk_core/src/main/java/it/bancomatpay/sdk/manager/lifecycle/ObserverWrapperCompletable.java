package it.bancomatpay.sdk.manager.lifecycle;


import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import it.bancomatpay.sdk.manager.model.VoidResponse;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class ObserverWrapperCompletable implements SingleObserver<VoidResponse> {

    private final static String TAG = ObserverWrapperCompletable.class.getSimpleName();
    private MutableLiveCompletable mutableLiveSingle;

    public ObserverWrapperCompletable(MutableLiveCompletable mutableLiveSingle) {
        this.mutableLiveSingle = mutableLiveSingle;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mutableLiveSingle.setRunning(true);
    }

    @Override
    public void onSuccess(@NonNull VoidResponse voidResponse) {
        mutableLiveSingle.setSuccess(voidResponse);
        mutableLiveSingle.setRunning(false);
    }

    @Override
    public void onError(Throwable e) {
        mutableLiveSingle.setError(e);
        CustomLogger.w(TAG, e);
        mutableLiveSingle.setRunning(false);
    }
}
