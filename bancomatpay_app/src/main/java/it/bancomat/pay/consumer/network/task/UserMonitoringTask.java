package it.bancomat.pay.consumer.network.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoUserMonitoringRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.NetworkListener;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class UserMonitoringTask extends ExtendedTask<Void> {

    private String bankUUID;
    private String tag;
    private String event;
    private String note;

    public UserMonitoringTask(OnCompleteResultListener<Void> mListener, String bankUUID, String tag, String event, String note) {
        super(mListener);
        this.bankUUID = bankUUID;
        this.tag = tag;
        this.event = event;
        this.note = note;
    }

    @Override
    protected void start() {
        DtoUserMonitoringRequest req = new DtoUserMonitoringRequest();
        req.setBankUUID(bankUUID);
        req.setTag(tag);
        req.setEvent(event);
        req.setNote(note);

        Single.fromCallable(new AppHandleRequestInteractor<>(Void.class, req, AppCmd.USER_MONITORING, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    OnNetworkCompleteListener<DtoAppResponse<Void>> l = new NetworkListener<Void>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> r = new Result<>();
            prepareResult(r, response);
            sendCompletition(r);
        }
    };
}

