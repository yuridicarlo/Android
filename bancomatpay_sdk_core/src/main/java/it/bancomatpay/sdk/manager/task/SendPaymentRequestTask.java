package it.bancomatpay.sdk.manager.task;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.db.UserFrequent;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSendPaymentRequestRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSendPaymentRequestResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class SendPaymentRequestTask extends ExtendedTask<PaymentRequestData> {

    private List<String> msisdnBeneficiary;
    private String amount;
    private String causal;
    private ItemInterface itemInterface;

    public SendPaymentRequestTask(OnCompleteResultListener<PaymentRequestData> mListener, List<String> msisdnBeneficiary, String amount, String causal,
                                  ItemInterface itemInterface) {
        super(mListener);
        this.msisdnBeneficiary = msisdnBeneficiary;
        this.amount = amount;
        this.causal = causal;
        this.itemInterface = itemInterface;
    }

    @Override
    protected void start() {
        DtoSendPaymentRequestRequest request = new DtoSendPaymentRequestRequest();
        request.setMsisdnBeneficiaries(msisdnBeneficiary);
        request.setAmount(amount);
        request.setCausal(causal);

        Single.fromCallable(new HandleRequestInteractor<>(DtoSendPaymentRequestResponse.class, request, Cmd.SEND_PAYMENT_REQUEST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSendPaymentRequestResponse>> listener = new NetworkListener<DtoSendPaymentRequestResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSendPaymentRequestResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<PaymentRequestData> r = new Result<>();
            prepareResult(r, response);

            if (r.isSuccess()) {
                r.setResult(Mapper.getPaymentRequestData(response.getRes()));

                if (response.getRes().getPaymentRequestState() != DtoSendPaymentRequestResponse.PaymentStateType.FAILED) {
                    String msisdn = !msisdnBeneficiary.isEmpty() ? msisdnBeneficiary.get(0) : "";
                    updateFrequentUser(itemInterface, msisdn);
                }

            }

            sendCompletition(r);
        }

    };

    protected static void updateFrequentUser(ItemInterface itemInterface, String msisdn){
        UserFrequent.Model userFrequent = new UserFrequent.Model();
        if (itemInterface.getType() == ItemInterface.Type.CONSUMER_PR || itemInterface.getType() == ItemInterface.Type.BOTH_PR) {
            userFrequent.setType(3);
        } else if (itemInterface.getType() == ItemInterface.Type.NONE) {
            userFrequent.setType(0);
        }
        userFrequent.setJsonObject(itemInterface.getJson());
        userFrequent.setUserFrequentId(msisdn);

        int operationCounter = 0;
        FrequentItem frequentItem = ApplicationModel.getInstance().getFrequentItem(msisdn);
        if (frequentItem != null) {
            operationCounter = frequentItem.getOperationCounter();
        }

        userFrequent.setOperationCounter(operationCounter);

        if (itemInterface.getType() != ItemInterface.Type.NONE) {
            UserDbHelper.getInstance().updateUserOperationCounter(userFrequent);
        }
    }
}
