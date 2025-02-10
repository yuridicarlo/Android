package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

public class DtoVerifyPinResponse implements Serializable {

    protected String lastAttempts;

    /**
     * Gets the value of the lastAttempts property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLastAttempts() {
        return lastAttempts;
    }

    /**
     * Sets the value of the lastAttempts property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLastAttempts(String value) {
        this.lastAttempts = value;
    }

}
