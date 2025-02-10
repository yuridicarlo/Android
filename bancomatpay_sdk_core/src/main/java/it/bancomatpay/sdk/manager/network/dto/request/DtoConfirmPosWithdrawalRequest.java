package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.math.BigInteger;

public class DtoConfirmPosWithdrawalRequest implements Serializable {

    private String tag;
    private Long shopId;
    private BigInteger tillId;
    private String amount;
    private String totalAmount;
    private String authorizationToken;
    private String qrCodeId;
    private String causal;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
