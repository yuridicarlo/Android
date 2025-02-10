package it.bancomatpay.sdk.manager.model;

import java.math.BigInteger;

import it.bancomatpay.sdk.manager.task.model.ItemInterface;

public class BCMPaymentRequest {

    private ItemInterface itemInterface;
    private String paymentId;
    private String paymentRequestId;
    private String msisdn;
    private int amount;
    private String causal;
    private String tag;
    private Long shopId;
    private BigInteger tillId;

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

}
