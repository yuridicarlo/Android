package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoGetQrCodeDetailsRequest implements Serializable {

    protected String qrCodeId;

    /**
     * Gets the value of the qrCodeId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getQrCode() {
        return qrCodeId;
    }

    /**
     * Sets the value of the qrCodeId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setQrCode(String value) {
        this.qrCodeId = value;
    }

}
