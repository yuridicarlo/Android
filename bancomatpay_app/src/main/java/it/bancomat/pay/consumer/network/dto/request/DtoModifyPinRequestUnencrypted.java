package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoModifyPinRequestUnencrypted implements Serializable {


    protected String newPin;

    /**
     * Gets the value of the newPin property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNewPin() {
        return newPin;
    }

    /**
     * Sets the value of the newPin property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNewPin(String value) {
        this.newPin = value;
    }

}
