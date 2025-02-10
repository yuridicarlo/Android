package it.bancomatpay.sdkui.model;

import java.math.BigInteger;

public class MerchantPaymentData extends AbstractPaymentData {

    private String tag;
    private Long shopId;
    private BigInteger tillId;
    protected String qrCodeId;

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

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

}
