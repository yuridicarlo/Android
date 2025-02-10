package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoCancelPreauthorizationRequest implements Serializable {

    protected String paymentId;
    protected String paymentRequestId;
    protected String amount;
    protected String msisdnSender;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMsisdnSender() {
        return msisdnSender;
    }

    public void setMsisdnSender(String msisdnSender) {
        this.msisdnSender = msisdnSender;
    }

}
