package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class NotificationPaymentData implements Serializable {

    protected ItemInterface item;
    protected PaymentItem paymentItem;

    public ItemInterface getItem() {
        return item;
    }

    public void setItem(ItemInterface item) {
        this.item = item;
    }

    public PaymentItem getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(PaymentItem paymentItem) {
        this.paymentItem = paymentItem;
    }

}
