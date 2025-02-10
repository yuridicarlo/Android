package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoSetIncomingDefaultInstrumentRequest implements Serializable {

    protected String iban;

    /**
     * Gets the value of the iban property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets the value of the iban property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIban(String value) {
        this.iban = value;
    }

}
