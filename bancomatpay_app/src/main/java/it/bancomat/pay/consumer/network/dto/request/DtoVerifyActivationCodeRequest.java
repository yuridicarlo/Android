package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoVerifyActivationCodeRequest implements Serializable {

    protected String activationCode;
    protected String bankUUID;

    /**
     * Gets the value of the activationCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getActivationCode() {
        return activationCode;
    }

    /**
     * Sets the value of the activationCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setActivationCode(String value) {
        this.activationCode = value;
    }

    public String getBankUUID() {
        return bankUUID;
    }

    public void setBankUUID(String bankUUID) {
        this.bankUUID = bankUUID;
    }

}
