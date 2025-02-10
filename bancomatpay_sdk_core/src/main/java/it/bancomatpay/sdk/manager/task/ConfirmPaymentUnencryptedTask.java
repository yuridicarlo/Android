package it.bancomatpay.sdk.manager.task;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.db.UserFrequent;
import it.bancomatpay.sdk.manager.network.dto.request.DtoConfirmPaymentRequestUnencrypted;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoConfirmPaymentResponse;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class ConfirmPaymentUnencryptedTask extends ExtendedTask<PaymentData> {

    private UserFrequent.Model userFrequent;
    String paymentId;
    String paymentRequestId;
    String tag;
    Long shopId;
    BigInteger tillId;
    String msisdn;
    String amount;
    String causal;
    boolean isP2P;
    private String qrCodeId;
    private boolean qrCodeEmpsa;
    String authorizationToken;

    private boolean isContactKnown = false;

    //P2P
    public ConfirmPaymentUnencryptedTask(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId, String paymentRequestId,
                                         String msisdn, int amount, String causal, String authorizationToken) {
        super(mListener);
        userFrequent = new UserFrequent.Model();
        userFrequent.setType(0);
        ContactItem contact = ApplicationModel.getInstance().getContactItem(msisdn);
        if (contact == null) {
            ContactItem contactUnknown = new ContactItem();
            contactUnknown.setMsisdn(msisdn);
            userFrequent.setJsonObject(contactUnknown.getJson());
        } else {
            userFrequent.setJsonObject(itemInterface.getJson());
            isContactKnown = true;
        }
        userFrequent.setUserFrequentId(itemInterface.getPhoneNumber());
        this.isP2P = true;
        this.msisdn = msisdn;
        this.amount = Integer.toString(amount);
        this.paymentId = paymentId;
        this.paymentRequestId = paymentRequestId;
        this.causal = causal;
        this.authorizationToken = authorizationToken;
    }

    //P2B
    ConfirmPaymentUnencryptedTask(OnCompleteResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId,
                                  String tag, Long shopId, BigInteger tillId, int amount, String causal, String qrCodeId, boolean qrCodeEmpsa, String authorizationToken) {
        super(mListener);
        userFrequent = new UserFrequent.Model();
        userFrequent.setType(1);
        userFrequent.setJsonObject(itemInterface.getJson());
        userFrequent.setUserFrequentId(tag);
        this.isP2P = false;
        this.tag = tag;
        this.shopId = shopId;
        this.tillId = tillId;
        this.amount = Integer.toString(amount);
        this.paymentId = paymentId;
        this.causal = causal;
        this.qrCodeId = qrCodeId;
        this.qrCodeEmpsa = qrCodeEmpsa;
        this.authorizationToken = authorizationToken;
    }

    //P2B
    public ConfirmPaymentUnencryptedTask(OnProgressResultListener<PaymentData> mListener, ItemInterface itemInterface, String paymentId,
                                         String tag, Long shopId, BigInteger tillId, int amount, String causal, String qrCodeId, boolean qrCodeEmpsa, String authorizationToken) {
        super(mListener);
        userFrequent = new UserFrequent.Model();
        userFrequent.setType(1);
        userFrequent.setJsonObject(itemInterface.getJson());
        userFrequent.setUserFrequentId(tag);
        this.isP2P = false;
        this.tag = tag;
        this.shopId = shopId;
        this.tillId = tillId;
        this.amount = Integer.toString(amount);
        this.paymentId = paymentId;
        this.causal = causal;
        this.qrCodeId = qrCodeId;
        this.qrCodeEmpsa = qrCodeEmpsa;
        this.authorizationToken = authorizationToken;
    }


    @Override
    protected void start() {

        if (!TextUtils.isEmpty(qrCodeId) && mListener instanceof OnProgressResultListener) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                if (mListener != null) {
                    ((OnProgressResultListener<?>) mListener).onProgress();
                }
            }, 1000);
        }

        DtoConfirmPaymentRequestUnencrypted req = new DtoConfirmPaymentRequestUnencrypted();
        req.setIsP2P(isP2P);
        req.setAmount(amount);
        req.setMsisdn(msisdn);
        req.setShopId(shopId);
        req.setTillId(tillId);
        req.setTag(tag);
        req.setCausal(causal);
        req.setQrCodeId(qrCodeId);
        req.setQrCodeEmpsa(qrCodeEmpsa);
        req.setPaymentId(paymentId);
        req.setPaymentRequestId(paymentRequestId);
        req.setAuthorizationToken(authorizationToken);

        BankServices.EBankService serviceType;
        if (isP2P) {
            serviceType = BankServices.EBankService.P2P;
        } else {
            serviceType = BankServices.EBankService.P2B;
        }
        Single.fromCallable(new HandleRequestInteractor<>(DtoConfirmPaymentResponse.class, req, Cmd.CONFIRM_PAYMENT, getJsessionClient(), serviceType))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoConfirmPaymentResponse>> l = new NetworkListener<DtoConfirmPaymentResponse>(this) {

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

    void manageSuccess(PaymentData.State state) {
        if (state != PaymentData.State.FAILED) {

            int operationCounter = 0;
            FrequentItem frequentItem = ApplicationModel.getInstance().getFrequentItem(msisdn);
            if (frequentItem != null) {
                operationCounter = frequentItem.getOperationCounter();
            }

            userFrequent.setOperationCounter(operationCounter);

            if (isContactKnown) {
                UserDbHelper.getInstance().updateUserOperationCounter(userFrequent);
            }

        }
    }

}
