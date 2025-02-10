package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSetBankIdStatusRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.EBankIdStatus;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SetBankIdStatusTask extends ExtendedTask<Void> {

    private EBankIdStatus status;

    public SetBankIdStatusTask(OnCompleteResultListener<Void> mListener, EBankIdStatus status) {
        super(mListener);
        this.status = status;
    }

    @Override
    protected void start() {
        DtoSetBankIdStatusRequest req = new DtoSetBankIdStatusRequest();
        req.setStatus(status.toString());

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.SET_BANK_ID_STATUS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<Void>> listener = new NetworkListener<Void>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> result = new Result<>();
            prepareResult(result, response);
            sendCompletition(result);
        }

    };


}
