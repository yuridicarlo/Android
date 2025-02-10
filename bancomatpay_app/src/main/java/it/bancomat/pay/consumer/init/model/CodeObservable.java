package it.bancomat.pay.consumer.init.model;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class CodeObservable extends BaseObservable {

    private int minLength;
    private String activationCode;
    private boolean showError;

    public CodeObservable(int minLength) {
        this.minLength = minLength;
    }

    @Bindable
    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
        this.showError = false;
        notifyPropertyChanged(BR.showError);
        notifyPropertyChanged(BR.activationCode);
        notifyPropertyChanged(BR.validCode);
    }

    @Bindable
    public boolean isValidCode(){
        if(TextUtils.isEmpty(activationCode)){
            return false;
        }
        return this.activationCode.length() >= minLength;
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
