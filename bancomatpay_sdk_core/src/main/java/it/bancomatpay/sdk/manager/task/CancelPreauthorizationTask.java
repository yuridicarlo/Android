package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoCancelPreauthorizationRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class CancelPreauthorizationTask extends ExtendedTask<Void> {

    private String paymentId;
    private String paymentRequestId;
    private String amount;
    private String msisdnSender;

    public CancelPreauthorizationTask(OnCompleteResultListener<Void> mListener, String paymentId, String paymentRequestId, String amount, String msisdnSender) {
        super(mListener);
        this.paymentId = paymentId;
        this.paymentRequestId = paymentRequestId;
        this.amount = amount;
        this.msisdnSender = msisdnSender;
    }

    @Override
    protected void start() {
        DtoCancelPreauthorizationRequest request = new DtoCancelPreauthorizationRequest();
        request.setPaymentId(paymentId);
        request.setPaymentRequestId(paymentRequestId);
        request.setAmount(amount);
        request.setMsisdnSender(msisdnSender);

        Single.fromCallable(new HandleRequestInteractor<>(Void.class, request, Cmd.CANCEL_PREAUTHORIZATION, getJsessionClient()))
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
//            if(r.isSuccess()) {
//
//            }
            sendCompletition(r);
        }
    };

}
