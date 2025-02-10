package it.bancomat.pay.consumer.login;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

public class LoginFlowManager {

    static final String IS_BLOCKED = "IS_BLOCKED";
    static final String PAYMENT_DATA_DEEPLINK = "PAYMENT_DATA_DEEPLINK";

    public static void goToLostPin(Activity activity, boolean isBlocked) {
        Intent i = new Intent(activity, LostPinActivity.class);
        /*if (isBlocked) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }*/
        i.putExtra(IS_BLOCKED, isBlocked);
        activity.startActivity(i);
        if (isBlocked) {
            activity.finish();
        }
    }

    public static void goToLogin(Activity activity) {
        Intent i = new Intent(activity, LoginActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goToLoginDeeplink(Activity activity, String paymentData) {
        Intent i = new Intent(activity, LoginActivity.class);
        i.putExtra(PAYMENT_DATA_DEEPLINK, paymentData);
        activity.startActivity(i);
        activity.finish();
    }

}
