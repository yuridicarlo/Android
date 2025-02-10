package it.bancomatpay.sdkui.model;


import java.math.BigDecimal;
import java.math.BigInteger;

import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.ShopItem;

public class MerchantQrPaymentData extends MerchantPaymentData {

    private PaymentItem paymentItem;
    private boolean qrcodeDynamic;

    public MerchantQrPaymentData(PaymentItem paymentItem, boolean qrcodeDynamic) {
        this.paymentItem = paymentItem;
        ShopItem shopItem = new ShopItem();
        shopItem.setMsisdn(paymentItem.getMsisdn());
        shopItem.setAddress(paymentItem.getAddress());
        shopItem.setInsignia(paymentItem.getInsignia());
        shopItem.setName(paymentItem.getShopName());
        shopItem.setTag(paymentItem.getTag());
        shopItem.setShopId(paymentItem.getShopId());
        if(paymentItem.getCategory() != null){
            shopItem.setPaymentCategory(paymentItem.getCategory().toString());
        }
        this.centsAmount = paymentItem.getCentsAmount();
        this.centsTotalAmount = paymentItem.getCentsTotalAmount();
        this.displayData = new ShopsDataMerchant(shopItem);
        this.qrCodeId = paymentItem.getQrCodeId();
        this.qrCodeEmpsa = paymentItem.isQrCodeEmpsa();
        this.localAmount = paymentItem.getLocalAmount();
        this.localCurrency = paymentItem.getLocalCurrency();
        this.qrcodeDynamic = qrcodeDynamic;
    }

    public PaymentItem getPaymentItem() {
        return paymentItem;
    }

    @Override
    public String getTag() {
        return paymentItem.getTag();
    }

    @Override
    public Long getShopId() {
        return paymentItem.getShopId();
    }

    @Override
    public BigInteger getTillId() {
        return paymentItem.getTillId();
    }

    public BigDecimal getAmount() {
        return paymentItem.getAmount();
    }


    public String getPaymentId() {
        return paymentId;
    }


    public BigDecimal getFee() {
        return paymentItem.getFee();
    }

    public DisplayData getDisplayData() {
        return displayData;
    }

    public void setDisplayData(DisplayData displayData) {
        this.displayData = displayData;
    }

    public String getCausal() {
        return paymentItem.getCausal();
    }

    public boolean isQrcodeDynamic() {
        return qrcodeDynamic;
    }

    public void setQrcodeDynamic(boolean qrcodeDynamic) {
        this.qrcodeDynamic = qrcodeDynamic;
    }

}
