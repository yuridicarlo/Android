package it.bancomat.pay.consumer.extended.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomat.pay.consumer.extended.activities.SettingsChangePinActivity;
import it.bancomat.pay.consumer.extended.activities.SettingsForceLoginActivity;
import it.bancomat.pay.consumer.extended.activities.SettingsRemoveFingerprintActivity;
import it.bancomat.pay.consumer.extended.activities.SettingsSetFingerprintActivity;

public class SettingsFlowManagerExtended {

    public static final String ENABLE_FORCE_LOGIN = "ENABLE_FORCE_LOGIN";

    public static void goToChangePin(Activity activity) {
        Intent i = new Intent(activity, SettingsChangePinActivity.class);
        activity.startActivity(i);
    }

    public static void goToSetFingerprint(Activity activity) {
        Intent intent = new Intent(activity, SettingsSetFingerprintActivity.class);
        activity.startActivity(intent);
    }

    public static void goToRemoveFingerprint(Activity activity) {
        Intent intent = new Intent(activity, SettingsRemoveFingerprintActivity.class);
        activity.startActivity(intent);
    }

    public static void goToSetForceLogin(Activity activity, boolean enable) {
        Intent i = new Intent(activity, SettingsForceLoginActivity.class);
        i.putExtra(ENABLE_FORCE_LOGIN, enable);
        activity.startActivity(i);
    }

}
