package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.math.BigInteger;

public class DtoConfirmPaymentRequestRequestUnencrypted implements Serializable {

    protected boolean isP2P;
    protected String paymentRequestId;
    protected String causal;
    protected String msisdn;
    protected String tag;
    protected long shopId;
    protected BigInteger tillId;
    protected String amount;
    protected String authorizationToken;

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
     * Gets the value of the paymentRequestId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    /**
     * Sets the value of the paymentRequestId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPaymentRequestId(String value) {
        this.paymentRequestId = value;
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
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * Sets the value of the shopId property.
     */
    public void setShopId(long value) {
        this.shopId = value;
    }

    /**
     * Gets the value of the tillId property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getTillId() {
        return tillId;
    }

    /**
     * Sets the value of the tillId property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setTillId(BigInteger value) {
        this.tillId = value;
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

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

}
