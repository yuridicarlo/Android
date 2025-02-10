package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoCancelPaymentRequest implements Serializable {

    protected String paymentId;
    protected String msisdn;

    /**
     * Gets the value of the paymentId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the value of the paymentId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPaymentId(String value) {
        this.paymentId = value;
    }

    /**
     * Gets the value of the causal property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the causal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

}
