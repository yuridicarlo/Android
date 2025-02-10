package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoSetPinRequest implements Serializable {

    protected String pin;
    protected String token;
    protected String outgoingIban;

    /**
     * Gets the value of the pin property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPin() {
        return pin;
    }

    /**
     * Sets the value of the pin property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPin(String value) {
        this.pin = value;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOutgoingIban() {
        return outgoingIban;
    }

    public void setOutgoingIban(String outgoingIban) {
        this.outgoingIban = outgoingIban;
    }

}
