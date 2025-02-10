package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetCashbackDataResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.CashbackData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetCashbackDataTask extends ExtendedTask<CashbackData> {

    public GetCashbackDataTask(OnCompleteResultListener<CashbackData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetCashbackDataResponse>(DtoGetCashbackDataResponse.class, null, Cmd.GET_CASHBACK_DATA, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private final OnNetworkCompleteListener<DtoAppResponse<DtoGetCashbackDataResponse>> listener = new NetworkListener<DtoGetCashbackDataResponse>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<DtoGetCashbackDataResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<CashbackData> result = new Result<>();
            prepareResult(result, response);

            if (result.isSuccess() && response.getRes() != null) {
                result.setResult(Mapper.getCashbackData(response.getRes()));
            }
            sendCompletition(result);
        }
    };

}
