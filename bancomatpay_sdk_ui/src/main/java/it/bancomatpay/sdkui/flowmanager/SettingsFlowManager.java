package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdkui.activities.BlockedContactsActivity;
import it.bancomatpay.sdkui.activities.bankid.BancomatPayAccessListActivity;
import it.bancomatpay.sdkui.activities.bankid.BlockedMerchantListActivity;

public class SettingsFlowManager {

    public static void goToBlockedContacts(Activity activity) {
        Intent intent = new Intent(activity, BlockedContactsActivity.class);
        activity.startActivity(intent);
    }

    public static void goToBlockedMerchants(Activity activity) {
        Intent intent = new Intent(activity, BlockedMerchantListActivity.class);
        activity.startActivity(intent);
    }

    public static void goToBcmPayAccesses(Activity activity) {
        Intent intent = new Intent(activity, BancomatPayAccessListActivity.class);
        activity.startActivity(intent);
    }

}
