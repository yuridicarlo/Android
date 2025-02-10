package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;

import it.bancomatpay.sdk.manager.task.model.ItemInterface;

public interface DisplayData extends SimpleDisplayData {
    String getTitle();
    String getDescription();
    String getPhoneNumber();
    Bitmap getBitmap();
    boolean showBancomatLogo();
    ItemInterface getItemInterface();
}
