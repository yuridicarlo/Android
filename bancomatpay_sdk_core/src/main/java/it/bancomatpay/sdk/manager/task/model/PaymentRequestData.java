package it.bancomatpay.sdk.manager.task.model;

import it.bancomatpay.sdk.manager.network.dto.response.DtoSendPaymentRequestResponse;

public class PaymentRequestData {

    protected DtoSendPaymentRequestResponse.PaymentStateType paymentState;

    public DtoSendPaymentRequestResponse.PaymentStateType getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(DtoSendPaymentRequestResponse.PaymentStateType paymentState) {
        this.paymentState = paymentState;
    }

}
