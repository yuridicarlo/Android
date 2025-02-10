package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoAllowPaymentRequestsRequest implements Serializable {

    protected Boolean allowP2BPaymentRequest;
    protected Boolean allowP2PPaymentRequest;
    protected String msisdn;

    /**
     * Gets the value of the allowP2BPaymentRequest property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isAllowP2BPaymentRequest() {
        return allowP2BPaymentRequest;
    }

    /**
     * Sets the value of the allowP2BPaymentRequest property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setAllowP2BPaymentRequest(Boolean value) {
        this.allowP2BPaymentRequest = value;
    }

    /**
     * Gets the value of the allowP2PPaymentRequest property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isAllowP2PPaymentRequest() {
        return allowP2PPaymentRequest;
    }

    /**
     * Sets the value of the allowP2PPaymentRequest property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setAllowP2PPaymentRequest(Boolean value) {
        this.allowP2PPaymentRequest = value;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

}
