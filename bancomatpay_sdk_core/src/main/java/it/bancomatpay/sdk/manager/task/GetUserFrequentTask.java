package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.interactor.FrequentContactItemInteractor;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class GetUserFrequentTask extends ExtendedTask<ArrayList<FrequentItem>> {

    GetUserFrequentTask(OnCompleteResultListener<ArrayList<FrequentItem>> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new FrequentContactItemInteractor())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<ArrayList<ItemInterface>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull ArrayList<ItemInterface> itemInterfaces) {
                        ArrayList<FrequentItem> frequentItems = new ArrayList<>();
                        for (ItemInterface item : itemInterfaces) {
                            if (((FrequentItem) item).getOperationCounter() > 0) {
                                frequentItems.add((FrequentItem) item);
                            }
                        }

                        Result<ArrayList<FrequentItem>> r = new Result<>();
                        if (itemInterfaces.isEmpty()) {
                            r.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
                        } else {
                            r.setStatusCode(StatusCode.Mobile.OK);
                            r.setResult(frequentItems);
                        }
                        sendCompletition(r);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<ArrayList<FrequentItem>> r = new Result<>();
                        r.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
                        sendCompletition(r);
                        CustomLogger.e(TAG, "Error in complete FrequentContactItemInteractor: " + e.getMessage());
                    }
                });


    }

}
