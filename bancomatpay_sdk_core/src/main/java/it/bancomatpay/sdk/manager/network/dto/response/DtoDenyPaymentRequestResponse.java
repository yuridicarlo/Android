package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;

public class DtoDenyPaymentRequestResponse implements Serializable {

    protected PaymentStateType paymentState;
    protected boolean addedToBlackList;

    /**
     * Gets the value of the paymentState property.
     *
     * @return possible object is
     * {@link PaymentStateType }
     */
    public PaymentStateType getPaymentState() {
        return paymentState;
    }

    /**
     * Sets the value of the paymentState property.
     *
     * @param value allowed object is
     *              {@link PaymentStateType }
     */
    public void setPaymentState(PaymentStateType value) {
        this.paymentState = value;
    }

    public boolean isAddedToBlackList() {
        return addedToBlackList;
    }

    public void setAddedToBlackList(boolean addedToBlackList) {
        this.addedToBlackList = addedToBlackList;
    }

}
