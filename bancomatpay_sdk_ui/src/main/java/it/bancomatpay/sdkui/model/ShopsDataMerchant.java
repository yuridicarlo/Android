package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;
import java.math.BigDecimal;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class ShopsDataMerchant implements ItemInterfaceConsumer, Serializable, MerchantDisplayData {

    private ShopItem shopItem;
    private PaymentItem paymentItem;
    private transient Bitmap bitmap;
    private boolean isImageAvailable;

    public ShopsDataMerchant(ShopItem shopItem, PaymentItem paymentItem) {
        this.shopItem = shopItem;
        this.paymentItem = paymentItem;
    }

    public ShopsDataMerchant(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    public long getShopId() {
        return shopItem.getShopId();
    }

    public BigDecimal getDistance() {
        return shopItem.getDistance();
    }

    @Override
    public String getLetter() {
        return "";
    }

    @Override
    public String getLetterSurname() {
        return getLetter();
    }

    @Override
    public String getInitials() {
        return "";
    }

    @Override
    public boolean showBancomatLogo() {
        return false;
    }

    @Override
    public ItemInterface getItemInterface() {
        return shopItem;
    }

    @Override
    public boolean performFilter(String filter) {
        return StringUtils.contains(new String[]{shopItem.getMsisdn(), shopItem.getDescription(), shopItem.getTitle()}, filter);
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    public PaymentItem getPaymentItem() {
        return paymentItem;
    }

    @Override
    public String getTitle() {
        return shopItem.getTitle();
    }

    @Override
    public String getDescription() {
        return shopItem.getDescription();
    }

    @Override
    public String getPhoneNumber() {
        return shopItem.getPhoneNumber();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        try {
            if (this.bitmap == null) {
                byte[] decodedString = Base64.decode(shopItem.getImage(), Base64.NO_WRAP);
                this.bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            setImageAvailable(true);
            return this.bitmap;
        } catch (Exception e) {
            setImageAvailable(false);
            return BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                    R.drawable.placeholder_merchant);
        }
    }

    public boolean isImageAvailable() {
        return isImageAvailable;
    }

    public void setImageAvailable(boolean imageAvailable) {
        isImageAvailable = imageAvailable;
    }

    @Override
    public double getLatitude() {
        return shopItem.getLatitude();
    }

    @Override
    public double getLongitude() {
        return shopItem.getLongitude();
    }

}
