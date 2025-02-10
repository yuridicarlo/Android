package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;

public class DtoVerifyPaymentStateResponse implements Serializable {

    protected PaymentStateType paymentState;

    public PaymentStateType getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(PaymentStateType paymentState) {
        this.paymentState = paymentState;
    }

}
