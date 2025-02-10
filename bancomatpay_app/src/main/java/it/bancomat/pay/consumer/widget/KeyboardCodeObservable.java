package it.bancomat.pay.consumer.widget;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import org.jetbrains.annotations.NotNull;

import it.bancomatpay.consumer.BR;

public class KeyboardCodeObservable extends BaseObservable {

    public KeyboardCodeObservable(int length) {
        stringBuilder = new StringBuilder();
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int maxLength) {
        this.length = maxLength;
    }

    int length;

    StringBuilder stringBuilder;

    private boolean showError;

    @Bindable
    public String getCode() {
        return stringBuilder.toString();
    }

    public void cancelDigit(){
        if(stringBuilder.length() > 0){
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            notifyPropertyChanged(BR.code);
            notifyPropertyChanged(BR.validCode);
            notifyPropertyChanged(BR.cancelable);
            setShowError(false);
        }
    }


    public void cancelAllDigits(){
        if(stringBuilder.length() > 0){
            stringBuilder.delete(0, stringBuilder.length());
            notifyPropertyChanged(BR.code);
            notifyPropertyChanged(BR.validCode);
            notifyPropertyChanged(BR.cancelable);
            setShowError(false);
        }
    }

    public void setCode(String code){
        if(stringBuilder.length() > 0) {
            stringBuilder.delete(0, stringBuilder.length());
        }
        this.stringBuilder.append(code);
        notifyPropertyChanged(BR.code);
        notifyPropertyChanged(BR.validCode);
        notifyPropertyChanged(BR.cancelable);
        setShowError(false);
    }

    public void putDigit(@NotNull View view) {

        String digit = getText(view);
        if(digit != null) {
            if (stringBuilder.length() + digit.length() <= length) {
                append(digit);
            }
        }
    }

    private String getText(View view){
        if(view instanceof TextView){
            return ((TextView) view).getText().toString();
        }
        return null;
    }


    private void append(String digit){
        this.stringBuilder.append(digit);
        notifyPropertyChanged(BR.code);
        notifyPropertyChanged(BR.validCode);
        notifyPropertyChanged(BR.cancelable);
        setShowError(false);
    }

    @Bindable
    public boolean isCancelable(){
        return stringBuilder.length() > 0;
    }

    @Bindable
    public boolean isValidCode() {
        return stringBuilder.length() == length;
    }

    @Bindable
    public boolean isShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
        notifyPropertyChanged(BR.showError);
    }
}