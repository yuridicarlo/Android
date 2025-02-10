package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoUnsubscribeCashbackRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class UnsubscribeCashbackTask extends ExtendedTask<Void> {

    private final String authorizationToken;

    public UnsubscribeCashbackTask(OnCompleteResultListener<Void> mListener, String authorizationToken) {
        super(mListener);
        this.authorizationToken = authorizationToken;
    }

    @Override
    protected void start() {
        DtoUnsubscribeCashbackRequest request = new DtoUnsubscribeCashbackRequest();
        request.setAuthorizationToken(this.authorizationToken);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, request, Cmd.UNSUBSCRIBE_CASHBACK, getJsessionClient()))
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



