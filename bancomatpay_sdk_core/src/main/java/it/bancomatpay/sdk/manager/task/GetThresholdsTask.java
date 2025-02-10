package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetThresholdsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.UserThresholds;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetThresholdsTask extends ExtendedTask<UserThresholds> {

    public GetThresholdsTask(OnCompleteResultListener<UserThresholds> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetThresholdsResponse>(DtoGetThresholdsResponse.class, null, Cmd.GET_THRESHOLDS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetThresholdsResponse>> listener = new NetworkListener<DtoGetThresholdsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetThresholdsResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<UserThresholds> r = new Result<>();
            prepareResult(r, response);
            if(r.isSuccess()) {
                r.setResult(Mapper.getThresholds(response.getRes()));
            }
            sendCompletition(r);
        }

    };

}
