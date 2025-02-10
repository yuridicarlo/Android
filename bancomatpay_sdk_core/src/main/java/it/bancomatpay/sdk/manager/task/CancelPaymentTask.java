package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoCancelPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class CancelPaymentTask extends ExtendedTask<Void> {

    private String paymentId;
    private String description;

    public CancelPaymentTask(OnCompleteResultListener<Void> mListener, String paymentId, String description) {
        super(mListener);
        this.paymentId = paymentId;
        this.description = description;
    }

    @Override
    protected void start() {
        DtoCancelPaymentRequest dtoCancelPaymentRequest = new DtoCancelPaymentRequest();
        dtoCancelPaymentRequest.setMsisdn(description);
        dtoCancelPaymentRequest.setPaymentId(paymentId);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, dtoCancelPaymentRequest, Cmd.CANCEL_PAYMENT, getJsessionClient()))
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
