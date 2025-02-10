package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoSendPaymentRequestResponse implements Serializable {

    public enum PaymentStateType {
        FAILED,
        SENT,
        ACCEPTED //Not used
    }

    protected PaymentStateType paymentRequestState;

    public PaymentStateType getPaymentRequestState() {
        return paymentRequestState;
    }

    public void setPaymentRequestState(PaymentStateType paymentState) {
        this.paymentRequestState = paymentState;
    }

}
