package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.Beneficiary;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSplitBillRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSendPaymentRequestResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class SplitBillTask extends ExtendedTask<PaymentRequestData> {

    List<? extends SplitBeneficiary> beneficiaries;
    String totalAmount;
    String causal;
    String description;

    public SplitBillTask(OnCompleteResultListener<PaymentRequestData> mListener, List<? extends SplitBeneficiary> beneficiaries, String totalAmount, String causal, String description) {
        super(mListener);
        this.totalAmount = totalAmount;
        this.causal = causal;
        this.description = description;
        this.beneficiaries = beneficiaries;
    }

    @Override
    protected void start() {

        DtoSplitBillRequest request = new DtoSplitBillRequest();
        request.setCausal(causal);
        request.setDescription(description);
        request.setAmount(totalAmount);
        List<Beneficiary> beneficiariesDto = new ArrayList<>();
        request.setBeneficiaries(beneficiariesDto);
        for (SplitBeneficiary beneficiary: beneficiaries){
            Beneficiary b = new Beneficiary();
            b.setMsisdn(beneficiary.getBeneficiary().getPhoneNumber());
            if(beneficiary.getBeneficiary().getType() == ItemInterface.Type.CONSUMER || beneficiary.getBeneficiary().getType() == ItemInterface.Type.CONSUMER_PR){
                b.setBpayEnabled(true);
            }
            b.setAmount(beneficiary.getAmount());
            beneficiariesDto.add(b);
        }

        Single.fromCallable(new HandleRequestInteractor<>(DtoSendPaymentRequestResponse.class, request, Cmd.SPLIT_BILL, getJsessionClient()))
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
                    for (SplitBeneficiary splitBeneficiary : beneficiaries) {
                        String msisdn = splitBeneficiary.getBeneficiary().getPhoneNumber();
                        SendPaymentRequestTask.updateFrequentUser(splitBeneficiary.getBeneficiary(), msisdn);
                    }
                }

            }

            sendCompletition(r);
        }

    };

}
