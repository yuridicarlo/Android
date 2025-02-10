package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetCashbackStatusResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetCashbackStatusTask extends ExtendedTask<CashbackStatusData> {

    public GetCashbackStatusTask(OnCompleteResultListener<CashbackStatusData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetCashbackStatusResponse>(DtoGetCashbackStatusResponse.class, null, Cmd.GET_CASHBACK_STATUS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private final OnNetworkCompleteListener<DtoAppResponse<DtoGetCashbackStatusResponse>> listener = new NetworkListener<DtoGetCashbackStatusResponse>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<DtoGetCashbackStatusResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<CashbackStatusData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess() && response.getRes() != null) {
                result.setResult(Mapper.getCashbackStatusData(response.getRes()));
            }
            sendCompletition(result);
        }
    };

}



