package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoInitUserRequest implements Serializable {

    protected String msisdn;

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

}
