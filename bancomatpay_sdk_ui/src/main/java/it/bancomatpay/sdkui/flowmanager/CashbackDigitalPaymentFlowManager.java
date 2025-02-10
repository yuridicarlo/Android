package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.task.model.CashbackData;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.cashaback.CashBackRankingActivity;
import it.bancomatpay.sdkui.activities.cashaback.CashbackActivationResultActivity;
import it.bancomatpay.sdkui.activities.cashaback.CashbackConfirmActivationActivity;

public class CashbackDigitalPaymentFlowManager {

    public static final String CASHBACK_AUTHENTICATION_RESULT = "CASHBACK_AUTHENTICATION_RESULT";
    public static final String CASHBACK_DATA = "CASHBACK_DATA";
    public static final String CASHBACK_STATUS_DATA_EXTRA = "CASHBACK_BPAY_TERMS_AND_CONDITIONS_EXTRA";

    public static void goToCashbackConfirmActivation(Activity activity, CashbackStatusData cashbackStatusData) {
        Intent intent = new Intent(activity, CashbackConfirmActivationActivity.class);
        intent.putExtra(CASHBACK_STATUS_DATA_EXTRA, cashbackStatusData);
        activity.startActivity(intent);
    }

    public static void goToCashbackResult(Activity activity, ECashbackActivationResult result) {
        Intent intent = new Intent(activity, CashbackActivationResultActivity.class);
        intent.putExtra(CASHBACK_AUTHENTICATION_RESULT, result);
        activity.startActivity(intent);
    }

    public static void goToCashBackRanking(Activity activity, CashbackData cashbackData){
        Intent intent = new Intent(activity, CashBackRankingActivity.class);
        intent.putExtra(CASHBACK_DATA, cashbackData);
        activity.startActivity(intent);
    }

    public static void goToStore(Activity activity) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.cashback_app_io_gms_link))));
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getString(R.string.cashback_app_io_package_name));
            if (launchIntent != null) {
                activity.startActivity(launchIntent);
            } else {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getString(R.string.cashback_app_io_package_name))));
                } catch (ActivityNotFoundException e) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getString(R.string.cashback_app_io_package_name))));
                }
            }
        }
    }

}
