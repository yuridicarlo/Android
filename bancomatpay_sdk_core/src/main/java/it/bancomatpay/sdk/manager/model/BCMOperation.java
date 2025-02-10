package it.bancomatpay.sdk.manager.model;

import androidx.annotation.NonNull;

import java.math.BigInteger;

import it.bancomatpay.sdk.manager.task.model.ItemInterface;

public class BCMOperation {

    private ItemInterface itemInterface;
    private String paymentId;
    private String paymentRequestId;
    private String msisdn;
    private String tag;
    private Long shopId;
    private BigInteger tillId;
    private int amount;
    private String causal;
    private String qrCodeId;
    private boolean qrCodeEmpsa;

    public ItemInterface getItemInterface() {
        return itemInterface;
    }

    public void setItemInterface(ItemInterface itemInterface) {
        this.itemInterface = itemInterface;
    }

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

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public BigInteger getTillId() {
        return tillId;
    }

    public void setTillId(BigInteger tillId) {
        this.tillId = tillId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public boolean isQrCodeEmpsa() {
        return qrCodeEmpsa;
    }

    public void setQrCodeEmpsa(boolean qrCodeEmpsa) {
        this.qrCodeEmpsa = qrCodeEmpsa;
    }

    @NonNull
    @Override
    public String toString() {
        return "BCMPayment{" +
                "itemInterface=" + itemInterface +
                ", paymentId='" + paymentId + '\'' +
                ", tag='" + tag + '\'' +
                ", shopId=" + shopId +
                ", tillId=" + tillId +
                ", amount=" + amount +
                ", causal='" + causal + '\'' +
                ", qrCodeId='" + qrCodeId + '\'' +
                '}';
    }

}
