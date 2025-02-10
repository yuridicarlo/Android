package it.bancomatpay.sdk.manager.task;

import android.text.TextUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAllowPaymentRequestsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.storage.model.FlagModel;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.AllowPaymentRequest;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AllowPaymentRequestTask extends ExtendedTask<AllowPaymentRequest> {

    private Boolean allowP2PPaymentRequest;
    private Boolean allowP2BPaymentRequest;
    private String msisdn;

    public AllowPaymentRequestTask(OnCompleteResultListener<AllowPaymentRequest> mListener, Boolean allowP2PPaymentRequest, Boolean allowP2BPaymentRequest, String msisdn) {
        super(mListener);
        this.allowP2BPaymentRequest = allowP2BPaymentRequest;
        this.allowP2PPaymentRequest = allowP2PPaymentRequest;
        this.msisdn = msisdn;
    }

    @Override
    protected void start() {
        DtoAllowPaymentRequestsRequest req = new DtoAllowPaymentRequestsRequest();
        req.setAllowP2BPaymentRequest(allowP2BPaymentRequest);
        req.setAllowP2PPaymentRequest(allowP2PPaymentRequest);

        BankServices.EBankService serviceType = null;
        if (this.allowP2PPaymentRequest != null) {
            serviceType = BankServices.EBankService.PAYMENT_REQUEST;
        } else if (this.allowP2BPaymentRequest != null) {
            serviceType = BankServices.EBankService.P2B;
        }

        if (!TextUtils.isEmpty(msisdn)) {
            req.setMsisdn(msisdn);
            Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.ALLOW_PAYMENT_REQUESTS, getJsessionClient(), serviceType))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(lBlockMsisdn));
        } else {
            Single.fromCallable(new HandleRequestInteractor<>(Void.class, req, Cmd.ALLOW_PAYMENT_REQUESTS, getJsessionClient(), serviceType))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(l));
        }
    }

    private OnNetworkCompleteListener<DtoAppResponse<Void>> l = new NetworkListener<Void>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<AllowPaymentRequest> r = new Result<>();
            prepareResult(r, response);
            BancomatDataManager dm = BancomatDataManager.getInstance();
            FlagModel flagModel = dm.getFlagModel();
            if (r.isSuccess()) {
                if (allowP2BPaymentRequest != null) {
                    flagModel.setAllowP2BPaymentRequest(allowP2BPaymentRequest);
                }
                if (allowP2PPaymentRequest != null) {
                    flagModel.setAllowP2PPaymentRequest(allowP2PPaymentRequest);
                }
                dm.putFlagModel(flagModel);
            }
            AllowPaymentRequest allowPaymentRequest = new AllowPaymentRequest();
            allowPaymentRequest.setForP2B(flagModel.isAllowP2BPaymentRequest());
            allowPaymentRequest.setForP2P(flagModel.isAllowP2PPaymentRequest());
            r.setResult(allowPaymentRequest);
            sendCompletition(r);
        }

    };

    private OnNetworkCompleteListener<DtoAppResponse<Void>> lBlockMsisdn = new NetworkListener<Void>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<Void> response) {
            CustomLogger.d(TAG, response.toString());
            Result<AllowPaymentRequest> r = new Result<>();
            prepareResult(r, response);
            sendCompletition(r);
        }

    };

}
