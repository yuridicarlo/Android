package it.bancomatpay.sdk.manager.lifecycle;


import java.io.Serializable;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class LiveUtil {

    public static <T> Single<T> fromCallable(final Callable<T> callable) {
        return Single.fromCallable(callable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static ObserverWrapperCompletable getWrapper(MutableLiveCompletable mutableLiveCompletable){
        return new ObserverWrapperCompletable(mutableLiveCompletable);
    }

    public static <E extends Serializable> ObserverWrapperSingle<E> getWrapper(MutableLiveSingle<E> mutableLiveCompletable){
        return new ObserverWrapperSingle<>(mutableLiveCompletable);
    }

}
