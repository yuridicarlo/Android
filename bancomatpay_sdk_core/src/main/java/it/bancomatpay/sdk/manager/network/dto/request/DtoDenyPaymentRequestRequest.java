package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoDenyPaymentRequestRequest implements Serializable {

    protected boolean isP2P;
    protected String paymentId;
    protected String denyReason;
    protected String msisdn;
    protected boolean addToBlackList;

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
     * Gets the value of the denyReason property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDenyReason() {
        return denyReason;
    }

    /**
     * Sets the value of the denyReason property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDenyReason(String value) {
        this.denyReason = value;
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

    public boolean isAddToBlackList() {
        return addToBlackList;
    }

    public void setAddToBlackList(boolean addToBlackList) {
        this.addToBlackList = addToBlackList;
    }

}
