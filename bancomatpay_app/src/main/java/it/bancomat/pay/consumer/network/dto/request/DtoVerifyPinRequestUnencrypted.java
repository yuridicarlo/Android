package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

import it.bancomat.pay.consumer.network.dto.PinOperationType;

public class DtoVerifyPinRequestUnencrypted implements Serializable {

    protected PinOperationType operation;
    protected String timestamp;

    /**
     * Gets the value of the operation property.
     *
     * @return possible object is
     * {@link PinOperationType }
     */
    public PinOperationType getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     *
     * @param value allowed object is
     *              {@link PinOperationType }
     */
    public void setOperation(PinOperationType value) {
        this.operation = value;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

}
