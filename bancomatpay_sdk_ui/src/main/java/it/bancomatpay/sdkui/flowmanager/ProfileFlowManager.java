package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdkui.activities.AccountsManagementActivity;
import it.bancomatpay.sdkui.activities.SpendingLimitsActivity;

public class ProfileFlowManager {

    public static void goToAccountsManagement(Activity activity) {
        Intent intent = new Intent(activity, AccountsManagementActivity.class);
        activity.startActivity(intent);
    }

    public static void goToSpendingLimits(Activity activity) {
        Intent intent = new Intent(activity, SpendingLimitsActivity.class);
        activity.startActivity(intent);
    }

}
