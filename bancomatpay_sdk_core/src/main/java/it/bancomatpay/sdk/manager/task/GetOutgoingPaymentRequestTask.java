package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetOutgoingPaymentRequestResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetOutgoingPaymentRequestTask extends ExtendedTask<OutgoingPaymentRequestData> {

    public GetOutgoingPaymentRequestTask(OnCompleteResultListener<OutgoingPaymentRequestData> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        Single.fromCallable(new HandleRequestInteractor<Void, DtoGetOutgoingPaymentRequestResponse>(DtoGetOutgoingPaymentRequestResponse.class, null, Cmd.GET_OUTGOING_PAYMENT_REQUEST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetOutgoingPaymentRequestResponse>> listener = new NetworkListener<DtoGetOutgoingPaymentRequestResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetOutgoingPaymentRequestResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<OutgoingPaymentRequestData> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                OutgoingPaymentRequestData outgoingPaymentData = Mapper.getOutgoingPaymentData(response.getRes());
                r.setResult(outgoingPaymentData);
            }

            sendCompletition(r);
        }
    };

}
