package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSubscribeCashbackRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SubscribeCashbackTask extends ExtendedTask<Void> {

    private final String authorizationToken;

    public SubscribeCashbackTask(OnCompleteResultListener<Void> mListener, String authorizationToken) {
        super(mListener);
        this.authorizationToken = authorizationToken;
    }

    @Override
    protected void start() {
        DtoSubscribeCashbackRequest request = new DtoSubscribeCashbackRequest();
        request.setAuthorizationToken(this.authorizationToken);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, request, Cmd.SUBSCRIBE_CASHBACK, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private final OnNetworkCompleteListener<DtoAppResponse<Void>> listener = new NetworkListener<Void>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<Void> result = new Result<>();
            prepareResult(result, response);

            sendCompletition(result);
        }
    };

}



