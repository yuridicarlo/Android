package it.bancomatpay.sdk.manager.task;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;
import it.bancomatpay.sdk.manager.network.dto.request.DtoDenyPaymentRequestRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoDenyPaymentRequestResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.DenyPaymentData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class DenyPaymentRequestTask extends ExtendedTask<DenyPaymentData> {

    private boolean isP2P;
    private String msisdn;
    private String paymentId;
    private boolean addToBlackList;
    private String denyReason;

    public DenyPaymentRequestTask(OnCompleteResultListener<DenyPaymentData> mListener, String paymentId, String denyReason) {
        super(mListener);
        this.paymentId = paymentId;
        this.denyReason = denyReason;
        this.isP2P = false;
    }

    public DenyPaymentRequestTask(OnCompleteResultListener<DenyPaymentData> mListener, String paymentId, String msisdn, boolean addToBlackList) {
        super(mListener);
        this.paymentId = paymentId;
        isP2P = true;
        this.msisdn = msisdn;
        this.addToBlackList = addToBlackList;
    }

    @Override
    protected void start() {
        DtoDenyPaymentRequestRequest dtoDenyPaymentRequest = new DtoDenyPaymentRequestRequest();
        dtoDenyPaymentRequest.setPaymentId(paymentId);
        dtoDenyPaymentRequest.setMsisdn(msisdn);
        dtoDenyPaymentRequest.setIsP2P(isP2P);
        if (isP2P) {
            dtoDenyPaymentRequest.setAddToBlackList(addToBlackList);
        } else {
            dtoDenyPaymentRequest.setDenyReason(denyReason);
        }

        Single.fromCallable(new HandleRequestInteractor<>(DtoDenyPaymentRequestResponse.class, dtoDenyPaymentRequest, Cmd.DENY_PAYMENT_REQUEST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoDenyPaymentRequestResponse>> listener = new NetworkListener<DtoDenyPaymentRequestResponse>(this) {
        @Override
        protected void manageComplete(DtoAppResponse<DtoDenyPaymentRequestResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<DenyPaymentData> r = new Result<>();

            DenyPaymentData data = new DenyPaymentData();
            if(response.getRes() != null) {
                data.setAddedToBlackList(response.getRes().isAddedToBlackList());
                data.setPaymentState(response.getRes().getPaymentState());
            }
            r.setResult(data);

            prepareResult(r, response);
            sendCompletition(r);
        }
    };
}
