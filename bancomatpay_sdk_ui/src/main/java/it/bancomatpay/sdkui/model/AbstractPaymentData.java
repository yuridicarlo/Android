package it.bancomatpay.sdkui.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import it.bancomatpay.sdk.manager.task.model.PaymentItem;

public class AbstractPaymentData implements Serializable {

    protected DisplayData displayData;
    protected String paymentId;
    protected String paymentRequestId;
    protected BigDecimal fee;
    protected BigDecimal amount;
    protected BigDecimal totalAmount;
    protected int centsAmount;
    protected int centsTotalAmount;
    protected String causal;
    protected boolean isRequestPayment = false;
    protected PaymentItem.EPaymentRequestType type;
    protected PaymentItem.EPaymentCategory category;
    protected Date expirationDate;
    protected String localCurrency;
    protected BigDecimal localAmount;
    protected boolean qrCodeEmpsa;

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public boolean isRequestPayment() {
        return isRequestPayment;
    }

    public void setRequestPayment(boolean isRequestPayment) {
        this.isRequestPayment = isRequestPayment;
    }

    public int getCentsAmount() {
        return centsAmount;
    }

    public void setCentsAmount(int centsAmount) {
        this.centsAmount = centsAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public DisplayData getDisplayData() {
        return displayData;
    }

    public void setDisplayData(DisplayData displayData) {
        this.displayData = displayData;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public PaymentItem.EPaymentRequestType getType() {
        return type;
    }

    public void setType(PaymentItem.EPaymentRequestType type) {
        this.type = type;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public PaymentItem.EPaymentCategory getCategory() {
        return category;
    }

    public void setCategory(PaymentItem.EPaymentCategory category) {
        this.category = category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getCentsTotalAmount() {
        return centsTotalAmount;
    }

    public void setCentsTotalAmount(int centsTotalAmount) {
        this.centsTotalAmount = centsTotalAmount;
    }

    public String getLocalCurrency() {
        return localCurrency;
    }

    public void setLocalCurrency(String localCurrency) {
        this.localCurrency = localCurrency;
    }

    public BigDecimal getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(BigDecimal localAmount) {
        this.localAmount = localAmount;
    }

    public boolean isQrCodeEmpsa() {
        return qrCodeEmpsa;
    }

    public void setQrCodeEmpsa(boolean qrCodeEmpsa) {
        this.qrCodeEmpsa = qrCodeEmpsa;
    }
}
