package it.bancomatpay.sdk.manager.task;

import android.text.TextUtils;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoVerifyPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoVerifyPaymentResponse;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class VerifyPaymentTask extends ExtendedTask<VerifyPaymentData>{

    private String tag;
    private Long shopId;
    private BigInteger tillId;
    private String msisdn;
    private String amount;
    private boolean isP2P;
    private String qrCode;
    private String causal;
    private String paymentRequestId;

    //P2P
    public VerifyPaymentTask(OnCompleteResultListener<VerifyPaymentData> mListener, String msisdn, int amount, String paymentRequestId, String causal) {
        super(mListener);
        this.isP2P = true;
        this.msisdn = msisdn;
        this.amount = Integer.toString(amount);
        this.paymentRequestId = paymentRequestId;
        this.causal = causal;
    }

    //P2B
    public VerifyPaymentTask(OnCompleteResultListener<VerifyPaymentData> mListener, String tag, Long shopId , BigInteger tillId, int amount) {
        super(mListener);
        this.isP2P = false;
        this.tag = tag;
        this.shopId = shopId;
        this.tillId = tillId;
        this.amount = Integer.toString(amount);
    }

    //P2B QRCODE
    public VerifyPaymentTask(OnCompleteResultListener<VerifyPaymentData> mListener, String tag, Long shopId , BigInteger tillId, int amount, String qrCode, String causal) {
        super(mListener);
        this.isP2P = false;
        this.tag = tag;
        this.shopId = shopId;
        this.tillId = tillId;
        this.amount = Integer.toString(amount);
        this.qrCode = qrCode;
        this.causal = causal;
    }

    @Override
    protected void start() {
        DtoVerifyPaymentRequest dtoVerifyPaymentRequest = new DtoVerifyPaymentRequest();
        dtoVerifyPaymentRequest.setIsP2P(isP2P);
        dtoVerifyPaymentRequest.setAmount(amount);
        dtoVerifyPaymentRequest.setMsisdn(msisdn);
        dtoVerifyPaymentRequest.setShopId(shopId);
        dtoVerifyPaymentRequest.setTillId(tillId);
        dtoVerifyPaymentRequest.setTag(tag);
        dtoVerifyPaymentRequest.setPaymentRequestId(paymentRequestId);
        if(qrCode != null && !qrCode.isEmpty()) {
            dtoVerifyPaymentRequest.setQrCodeId(qrCode);
        }
        if(!TextUtils.isEmpty(causal)){
            dtoVerifyPaymentRequest.setCausal(causal);
        } else {
            dtoVerifyPaymentRequest.setCausal("");
        }

        BankServices.EBankService serviceType;
        if (isP2P) {
            serviceType = BankServices.EBankService.P2P;
        } else {
            serviceType = BankServices.EBankService.P2B;
        }

        Single.fromCallable(new HandleRequestInteractor<>(DtoVerifyPaymentResponse.class, dtoVerifyPaymentRequest, Cmd.VERIFY_PAYMENT, getJsessionClient(), serviceType))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoVerifyPaymentResponse>> listener = new NetworkListener<DtoVerifyPaymentResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoVerifyPaymentResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<VerifyPaymentData> r = new Result<>();
            prepareResult(r, response);

            if(r.isSuccess()){
                r.setResult(Mapper.getVerifyPaymentData(response.getRes()));
            }

            sendCompletition(r);
        }

    };

}
