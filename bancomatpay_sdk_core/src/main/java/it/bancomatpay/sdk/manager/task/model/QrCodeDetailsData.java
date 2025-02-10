package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class QrCodeDetailsData implements Serializable {

    private PaymentItem paymentItem;
    private ShopItem shopItem;

    public ShopItem getShopItem() {
        return shopItem;
    }

    public void setShopItem(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    public PaymentItem getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(PaymentItem paymentItem) {
        this.paymentItem = paymentItem;
    }
}
