package it.bancomatpay.sdk.manager.task.model;

import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;



public class DenyPaymentData {

    private PaymentStateType paymentState;
    private boolean addedToBlackList;

    public PaymentStateType getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(PaymentStateType paymentState) {
        this.paymentState = paymentState;
    }

    public boolean isAddedToBlackList() {
        return addedToBlackList;
    }

    public void setAddedToBlackList(boolean addedToBlackList) {
        this.addedToBlackList = addedToBlackList;
    }

}
