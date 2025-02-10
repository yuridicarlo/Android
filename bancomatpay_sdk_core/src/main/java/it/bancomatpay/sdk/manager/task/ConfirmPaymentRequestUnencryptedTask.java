package it.bancomatpay.sdk.manager.task;

import android.text.TextUtils;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmPaymentRequestRequestUnencrypted;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmPaymentRequestUnencrypted;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPaymentRequestResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPaymentResponse;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class ConfirmPaymentRequestUnencryptedTask extends ConfirmPaymentUnencryptedTask {

    public ConfirmPaymentRequestUnencryptedTask(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId, String paymentRequestId,
                                                String msisdn, int amount, String causal, String authorizationToken) {
        super(mListener, itemInterface, paymentId, paymentRequestId, msisdn, amount, causal, authorizationToken);
    }

    public ConfirmPaymentRequestUnencryptedTask(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId,
                                                String tag, Long shopId, BigInteger tillId, int amount, String causal, String authorizationToken) {
        super(mListener, itemInterface, paymentId, tag, shopId, tillId, amount, causal, null, false, authorizationToken);
    }

    @Override
    protected void start() {

        if (isP2P) {

            DtoConfirmPaymentRequestUnencrypted req = new DtoConfirmPaymentRequestUnencrypted();
            req.setIsP2P(isP2P);
            req.setAmount(amount);
            req.setCausal(causal);
            req.setMsisdn(msisdn);
            req.setPaymentId(paymentId);
            req.setPaymentRequestId(paymentRequestId);
            //req.setShopId(shopId);
            req.setTag(tag);
            req.setTillId(tillId);
            req.setAuthorizationToken(authorizationToken);

            BankServices.EBankService serviceType = BankServices.EBankService.P2P;

            Single.fromCallable(new HandleRequestInteractor<>(DtoConfirmPaymentResponse.class, req, Cmd.CONFIRM_PAYMENT, getJsessionClient(), serviceType))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(lp2p));

        } else {

            DtoConfirmPaymentRequestRequestUnencrypted req = new DtoConfirmPaymentRequestRequestUnencrypted();
            req.setIsP2P(isP2P);
            req.setAmount(amount);
            req.setCausal(causal);
            req.setMsisdn(msisdn);
            req.setPaymentRequestId(paymentId);
            req.setShopId(shopId);
            req.setTag(tag);
            req.setTillId(tillId);
            req.setAuthorizationToken(authorizationToken);

            BankServices.EBankService serviceType = BankServices.EBankService.P2B;

            Single.fromCallable(new HandleRequestInteractor<>(DtoConfirmPaymentRequestResponse.class, req, Cmd.CONFIRM_PAYMENT_REQUEST, getJsessionClient(), serviceType))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(l));
        }
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoConfirmPaymentRequestResponse>> l = new NetworkListener<DtoConfirmPaymentRequestResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoConfirmPaymentRequestResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<PaymentData> r = new Result<>();
            prepareResult(r, response);
            PaymentData paymentData = new PaymentData();
            if (r.isSuccess()) {
                paymentData.setState(Mapper.gePaymentState(response.getRes().getPaymentState()));
                manageSuccess(paymentData.getState());
            } else {
                try {
                    paymentData.setLastAttempts(Integer.parseInt(response.getRes().getLastAttempts()));
                } catch (Exception e) {
                    paymentData.setLastAttempts(-1);
                }
            }
            r.setResult(paymentData);
            sendCompletition(r);
        }

    };

    private OnNetworkCompleteListener<DtoAppResponse<DtoConfirmPaymentResponse>> lp2p = new NetworkListener<DtoConfirmPaymentResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoConfirmPaymentResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<PaymentData> r = new Result<>();
            prepareResult(r, response);
            PaymentData paymentData = new PaymentData();
            if (r.isSuccess()) {
                PollingData pollingData = new PollingData();
                if (response.getRes().getPollingMaxTimeSeconds() != null && TextUtils.isDigitsOnly(response.getRes().getPollingMaxTimeSeconds())) {
                    pollingData.setPollingMaxTimeSeconds(1000 * Integer.parseInt(response.getRes().getPollingMaxTimeSeconds()));
                }
                if (response.getRes().getPollingRangeTimeSeconds() != null && TextUtils.isDigitsOnly(response.getRes().getPollingRangeTimeSeconds())) {
                    pollingData.setPollingRangeTimeSeconds(1000 * Integer.parseInt(response.getRes().getPollingRangeTimeSeconds()));
                }
                paymentData.setPollingData(pollingData);
                paymentData.setState(Mapper.gePaymentState(response.getRes().getPaymentState()));
                manageSuccess(paymentData.getState());
            } else {
                try {
                    paymentData.setLastAttempts(Integer.parseInt(response.getRes().getLastAttempts()));
                } catch (Exception e) {
                    paymentData.setLastAttempts(-1);
                }
            }
            r.setResult(paymentData);
            sendCompletition(r);
        }

    };

}
