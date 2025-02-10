package it.bancomat.pay.consumer.extended.flowmanager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import it.bancomat.pay.consumer.extended.activities.ProfileActivityExtended;
import it.bancomat.pay.consumer.extended.activities.SettingsActivityExtended;
import it.bancomat.pay.consumer.extended.activities.SupportActivity;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.storeLocator.StoreLocatorActivity;

public class HomeFlowManagerExtended {

    public static void goToMerchantList(Activity activity) {
        Intent intent = new Intent(activity, StoreLocatorActivity.class);
        activity.startActivity(intent);
    }

    public static void goToSettings(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivityExtended.class);
        activity.startActivity(intent);
    }

    public static void goToSupport(Activity activity) {
        if(!TextUtils.isEmpty(BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed())) {
            Intent intent = new Intent(activity, SupportActivity.class);
            activity.startActivity(intent);
        }
    }

    public static void goToProfile(Activity activity) {
        Intent intent = new Intent(activity, ProfileActivityExtended.class);
        activity.startActivity(intent);
    }

}
