package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoLoginRequest implements Serializable {

    protected DtoPinCrypted dtoPinCrypted;

    /**
     * Gets the value of the dtoPinCrypted property.
     *
     * @return possible object is
     * {@link DtoPinCrypted }
     */
    public DtoPinCrypted getDtoPinCrypted() {
        return dtoPinCrypted;
    }

    /**
     * Sets the value of the dtoPinCrypted property.
     *
     * @param value allowed object is
     *              {@link DtoPinCrypted }
     */
    public void setDtoPinCrypted(DtoPinCrypted value) {
        this.dtoPinCrypted = value;
    }

}
