package it.bancomatpay.sdk.manager.network.dto.request;

import android.text.TextUtils;

import java.io.Serializable;

public class DtoP2PPaymentRequest implements Serializable {

    protected String senderName;
    protected String beneficiaryName;
    protected String amount;
    protected String expirationDate;
    protected String paymentRequestDate;
    protected String msisdnSender;
    protected String msisdnBeneficiary;
    protected String paymentRequestId;
    protected String causal;
    protected String paymentRequestState;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPaymentRequestDate() {
        return paymentRequestDate;
    }

    public void setPaymentRequestDate(String paymentRequestDate) {
        this.paymentRequestDate = paymentRequestDate;
    }

    public String getMsisdnSender() {
        return msisdnSender;
    }

    public void setMsisdnSender(String msisdnSender) {
        this.msisdnSender = msisdnSender;
    }

    public String getMsisdnBeneficiary() {
        return msisdnBeneficiary;
    }

    public void setMsisdnBeneficiary(String msisdnBeneficiary) {
        this.msisdnBeneficiary = msisdnBeneficiary;
    }

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getPaymentRequestState() {
        return paymentRequestState;
    }

    public void setPaymentRequestState(String paymentRequestState) {
        this.paymentRequestState = paymentRequestState;
    }

    public String getLetter() {
        if (!TextUtils.isEmpty(this.senderName)) {
            return this.senderName.substring(0, 1);
        }
        return "";
    }

}
