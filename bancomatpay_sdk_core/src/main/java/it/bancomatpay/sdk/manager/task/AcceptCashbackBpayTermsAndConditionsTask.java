package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAcceptCashbackBpayTermsAndConditionsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AcceptCashbackBpayTermsAndConditionsTask extends ExtendedTask<Void> {

    private final String timestamp;

    public AcceptCashbackBpayTermsAndConditionsTask(OnCompleteResultListener<Void> mListener, String timestamp) {
        super(mListener);
        this.timestamp = timestamp;
    }

    @Override
    protected void start() {
        DtoAcceptCashbackBpayTermsAndConditionsRequest request = new DtoAcceptCashbackBpayTermsAndConditionsRequest();
        request.setTimestampTC(this.timestamp);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, request, Cmd.ACCEPT_CASHBACK_BPAY_TERMS_AND_CONDITIONS, getJsessionClient()))
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



