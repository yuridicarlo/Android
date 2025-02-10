package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DtoVerifyPaymentRequest implements Serializable {

    protected boolean isP2P;
    protected String msisdn;
    protected String tag;
    protected Long shopId;
    protected BigInteger tillId;
    protected String amount;
    protected String qrCodeId;
    protected String causal;
    protected String paymentRequestId;

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

    /**
     * Gets the value of the isP2P property.
     */
    public boolean isIsP2P() {
        return isP2P;
    }

    /**
     * Sets the value of the isP2P property.
     */
    public void setIsP2P(boolean value) {
        this.isP2P = value;
    }

    /**
     * Gets the value of the msisdn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the tag property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the value of the tag property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTag(String value) {
        this.tag = value;
    }

    /**
     * Gets the value of the shopId property.
     *
     * @return possible object is
     * {@link Long }
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * Sets the value of the shopId property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setShopId(Long value) {
        this.shopId = value;
    }

    /**
     * Gets the value of the tillId property.
     *
     * @return possible object is
     * {@link Integer }
     */
    public BigInteger getTillId() {
        return tillId;
    }

    /**
     * Sets the value of the tillId property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setTillId(BigInteger value) {
        this.tillId = value;
    }

    /**
     * Gets the value of the amount property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

}
