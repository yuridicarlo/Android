package it.bancomat.pay.consumer.extended;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;

public interface BancomatFullStackSdkInterfaceExtended extends BancomatFullStackSdkInterface {

    void startBancomatPayFlowClearTask(@NonNull Activity activity, Drawable logoBank, boolean processPush, String sessionToken);

    void setLoyaltyToken(String loyaltyToken);

    class Factory {
        public static BancomatFullStackSdkInterfaceExtended getInstance() {
            return BancomatFullStackSdkExtended.getInstance();
        }
    }

}
