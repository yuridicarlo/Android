package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

import it.bancomat.pay.consumer.network.dto.response.DtoPendingPayment;


public class PendingPayment implements Serializable {

    protected String msisdnSender;
    protected String amount; //in cents
    protected String causal;
    protected String paymentId;
    protected DtoPendingPayment dtoPendingPayment;

    public String getMsisdnSender() {
        return msisdnSender;
    }

    public void setMsisdnSender(String msisdnSender) {
        this.msisdnSender = msisdnSender;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public DtoPendingPayment getDtoPendingPayment() {
        return dtoPendingPayment;
    }

    public void setDtoPendingPayment(DtoPendingPayment dtoPendingPayment) {
        this.dtoPendingPayment = dtoPendingPayment;
    }

}
