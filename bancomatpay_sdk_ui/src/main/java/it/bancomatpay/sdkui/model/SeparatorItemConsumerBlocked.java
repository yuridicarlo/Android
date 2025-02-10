package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.text.TextUtils;

import it.bancomatpay.sdk.manager.task.model.ItemInterface;

public class SeparatorItemConsumerBlocked implements ItemInterfaceConsumer {

    private String letter;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public String getLetter() {
        return letter;
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
        return null;
    }

    @Override
    public boolean performFilter(String filter) {
        return TextUtils.isEmpty(filter);
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

}
