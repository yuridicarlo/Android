package it.bancomat.pay.consumer.network.task;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.dto.request.DtoSetCustomerJourneyTagRequest;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetCustomerJourneyTagTask extends ExtendedTask<Void> {

    private static final String TAG = SetCustomerJourneyTagTask.class.getSimpleName();

    private static SetCustomerJourneyTagTask instance;

    private static AtomicBoolean busy;
    private static ArrayList<AppHandleRequestInteractor<DtoSetCustomerJourneyTagRequest, Void>> interactorList;

    public static SetCustomerJourneyTagTask newInstance(OnCompleteResultListener<Void> mListener, CustomerJourneyTag tag) {
        if (instance == null) {
            instance = new SetCustomerJourneyTagTask(mListener);
            interactorList = new ArrayList<>();
            busy = new AtomicBoolean(false);
        }

        interactorList.add(new AppHandleRequestInteractor<>(Void.class, tag, getJsessionClient()));
        CustomLogger.d(TAG, "Added interactor to list");

        return instance;
    }

    private SetCustomerJourneyTagTask(OnCompleteResultListener<Void> mListener) {
        super(mListener);
    }

    @Override
    protected synchronized void start() {

        if (busy.compareAndSet(false, true)) {

            if (!interactorList.isEmpty()) {
                Single.fromCallable(interactorList.get(0))
                        .doOnSuccess(voidDtoAppResponse -> {
                            CustomLogger.d(TAG, "Executing doOnSuccess");
                            interactorList.remove(0);
                            busy.set(false);
                            start();
                        })
                        .doOnError((error)->{
                            busy.set(false);
                        })
                        .doFinally(() -> {
                            CustomLogger.d(TAG, "Executing doFinally");
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new ObserverSingleCustom<>(listener));
            } else {
                busy.set(false);
            }

        }

    }

    private final OnNetworkCompleteListener<DtoAppResponse<Void>> listener = new NetworkListener<Void>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            sendCompletition(r);
        }

    };

}

