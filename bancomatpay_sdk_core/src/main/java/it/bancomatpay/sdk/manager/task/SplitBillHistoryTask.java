package it.bancomatpay.sdk.manager.task;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSplitBillHistoryResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class SplitBillHistoryTask extends ExtendedTask<List<SplitBillHistory>> {

    public SplitBillHistoryTask(OnCompleteResultListener<List<SplitBillHistory>> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<>(DtoSplitBillHistoryResponse.class, null, Cmd.GET_SPLIT_BILL_HISTORY, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSplitBillHistoryResponse>> listener = new NetworkListener<DtoSplitBillHistoryResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSplitBillHistoryResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<List<SplitBillHistory>> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                r.setResult(Mapper.getSplitBillHistory(response.getRes()));
            }

            sendCompletition(r);
        }

    };

}
