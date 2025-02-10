package it.bancomatpay.sdkui.utilities;

import android.graphics.drawable.Drawable;

public class LogoBankSingleton {

    private static LogoBankSingleton instance;

    private Drawable logoBank;

    public static LogoBankSingleton getInstance() {
        if (instance == null) {
            instance = new LogoBankSingleton();
        }
        return instance;
    }

    public void setLogoBank(Drawable logoBank) {
        this.logoBank = logoBank;
    }

    public Drawable getLogoBank() {
        return logoBank;
    }

}
