package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;

public class DtoGetPaymentRequestRequest implements Serializable {

    protected String paymentRequestId;
    protected PaymentRequestType paymentRequestType;

    /**
     * Gets the value of the paymentRequestId property.
     *
     * @return possible object is
     * {@link Long }
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    /**
     * Sets the value of the paymentRequestId property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setPaymentRequestId(String value) {
        this.paymentRequestId = value;
    }


    public PaymentRequestType getPaymentRequestType() {
        return paymentRequestType;
    }

    public void setPaymentRequestType(PaymentRequestType paymentRequestType) {
        this.paymentRequestType = paymentRequestType;
    }
}
