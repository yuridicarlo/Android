package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBankIdStatusResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.EBankIdStatus;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class GetBankIdStatusTask extends ExtendedTask<EBankIdStatus> {

    public GetBankIdStatusTask(OnCompleteResultListener<EBankIdStatus> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetBankIdStatusResponse>(DtoGetBankIdStatusResponse.class, null, Cmd.GET_BANK_ID_STATUS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetBankIdStatusResponse>> listener = new NetworkListener<DtoGetBankIdStatusResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetBankIdStatusResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<EBankIdStatus> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess()) {
                try {
                    result.setResult(EBankIdStatus.valueOf(response.getRes().getStatus()));
                } catch (IllegalArgumentException e) {
                    result.setResult(EBankIdStatus.UNDEFINED);
                }
            }

            sendCompletition(result);
        }

    };


}
