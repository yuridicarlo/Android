package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetTransactionDetailsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetTransactionDetailsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.TransactionDetails;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class GetTransactionDetailsTask extends ExtendedTask<TransactionDetails> {

    private String paymentId;
    private boolean isP2p;

    public GetTransactionDetailsTask(OnCompleteResultListener<TransactionDetails> mListener, String paymentId, boolean isP2p) {
        super(mListener);
        this.paymentId = paymentId;
        this.isP2p = isP2p;
    }

    @Override
    protected void start() {
        DtoGetTransactionDetailsRequest request = new DtoGetTransactionDetailsRequest();
        request.setPaymentId(paymentId);
        request.setP2p(isP2p);
        Single.fromCallable(new HandleRequestInteractor<>(DtoGetTransactionDetailsResponse.class, request, Cmd.GET_TRANSACTION_DETAILS, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetTransactionDetailsResponse>> listener = new NetworkListener<DtoGetTransactionDetailsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetTransactionDetailsResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<TransactionDetails> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                r.setResult(Mapper.getTransactionDetails(response.getRes()));
            }
            sendCompletition(r);
        }

    };

}
