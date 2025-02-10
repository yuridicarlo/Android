package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.interactor.CachedContactItemInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class CachedPhoneBookTask extends ExtendedTask<ArrayList<ContactItem>> {

    CachedPhoneBookTask(OnCompleteResultListener<ArrayList<ContactItem>> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new CachedContactItemInteractor())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<ArrayList<ContactItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull ArrayList<ContactItem> contactItems) {
                        Result<ArrayList<ContactItem>> r = new Result<>();
                        r.setStatusCode(StatusCode.Mobile.OK);
                        ArrayList<ContactItem> list = new ArrayList<>();
                        for (ItemInterface item : contactItems) {
                            list.add((ContactItem) item);
                        }
                        r.setResult(list);
                        sendCompletition(r);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<ArrayList<ContactItem>> r = new Result<>();
                        r.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
                        sendCompletition(r);
                        CustomLogger.e(TAG, "Error in complete CachedContactItemInteractor: " + e.getMessage());
                    }
                });

    }

}
