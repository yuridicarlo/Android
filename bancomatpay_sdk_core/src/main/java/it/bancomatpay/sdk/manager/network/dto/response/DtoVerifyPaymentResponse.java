package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

public class DtoVerifyPaymentResponse implements Serializable {

    protected String paymentId;
    protected String fee;
    protected String contactName;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

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
     * Gets the value of the fee property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public String getFee() {
        return fee;
    }

    /**
     * Sets the value of the fee property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setFee(String value) {
        this.fee = value;
    }

}
