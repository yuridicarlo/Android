package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoPaymentRequest implements Serializable {

    protected DtoP2BPaymentRequest dtoP2BPaymentRequest;
    protected DtoP2PPaymentRequest dtoP2PPaymentRequest;

    /**
     * Gets the value of the dtoP2BPaymentRequest property.
     *
     * @return possible object is
     * {@link DtoP2BPaymentRequest }
     */
    public DtoP2BPaymentRequest getDtoP2BPaymentRequest() {
        return dtoP2BPaymentRequest;
    }

    /**
     * Sets the value of the dtoP2BPaymentRequest property.
     *
     * @param value allowed object is
     *              {@link DtoP2BPaymentRequest }
     */
    public void setDtoP2BPaymentRequest(DtoP2BPaymentRequest value) {
        this.dtoP2BPaymentRequest = value;
    }

    /**
     * Gets the value of the dtoP2PPaymentRequest property.
     *
     * @return possible object is
     * {@link DtoP2PPaymentRequest }
     */
    public DtoP2PPaymentRequest getDtoP2PPaymentRequest() {
        return dtoP2PPaymentRequest;
    }

    /**
     * Sets the value of the dtoP2PPaymentRequest property.
     *
     * @param value allowed object is
     *              {@link DtoP2PPaymentRequest }
     */
    public void setDtoP2PPaymentRequest(DtoP2PPaymentRequest value) {
        this.dtoP2PPaymentRequest = value;
    }

}
