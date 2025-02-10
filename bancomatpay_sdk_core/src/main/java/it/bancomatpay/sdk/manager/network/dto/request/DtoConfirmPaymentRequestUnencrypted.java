package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.math.BigInteger;

public class DtoConfirmPaymentRequestUnencrypted implements Serializable {

    protected boolean isP2P;
    protected String causal;
    protected String paymentId;
    protected String paymentRequestId;
    protected String amount;
    protected String msisdn;
    protected String tag;
    protected Long shopId;
    protected BigInteger tillId;
    protected String qrCodeId;
    protected String authorizationToken;
    protected boolean qrCodeEmpsa;

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
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
     * Gets the value of the causal property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCausal() {
        return causal;
    }

    /**
     * Sets the value of the causal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCausal(String value) {
        this.causal = value;
    }

    /**
     * Gets the value of the paymentId property.
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the value of the paymentId property.
     */
    public void setPaymentId(String value) {
        this.paymentId = value;
    }

    /**
     * Gets the value of the amount property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAmount(String value) {
        this.amount = value;
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
     * Gets the value of the qrCodeId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getQrCodeId() {
        return qrCodeId;
    }

    /**
     * Sets the value of the qrCodeId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setQrCodeId(String value) {
        this.qrCodeId = value;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    /**
     * Gets the value of the qrCodeEmpsa property.
     */
    public boolean isQrCodeEmpsa() {
        return qrCodeEmpsa;
    }

    /**
     * Sets the value of the qrCodeEmpsa property.
     */
    public void setQrCodeEmpsa(boolean qrCodeEmpsa) {
        this.qrCodeEmpsa = qrCodeEmpsa;
    }
}
