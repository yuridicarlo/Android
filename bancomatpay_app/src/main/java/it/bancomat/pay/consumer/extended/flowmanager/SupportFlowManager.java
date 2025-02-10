package it.bancomat.pay.consumer.extended.flowmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Locale;

import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.BuildConfig;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;

public class SupportFlowManager {

    public static void goToOpenPhone(Activity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        activity.startActivity(intent);
    }

    public static void goToOpenEmail(Activity activity, String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(it.bancomatpay.sdkui.R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getEmailBodyString(activity));
        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(emailIntent, activity.getString(it.bancomatpay.sdkui.R.string.share)));
        }
    }

    private static String getEmailBodyString(Activity activity) {

        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        String msisdn = ApplicationModel.getInstance().getUserData() != null
                ? ApplicationModel.getInstance().getUserData().getMsisdn() : "";

        String sRet = "";
        sRet += "- Mobile number: " + (!TextUtils.isEmpty(msisdn) ? msisdn : "N/A");
        sRet += "\n";
        sRet += "- App version: " + BuildConfig.APP_NAME_VERSION + " v" + BuildConfig.APP_BUILD_VERSION;
        sRet += "\n";
        sRet += "- OS: Android";
        sRet += "\n";
        sRet += "- OS version: " + Build.VERSION.RELEASE;
        sRet += "\n";
        sRet += "- Device: " + android.os.Build.MODEL;
        sRet += "\n";
        sRet += "- Country: " + (!TextUtils.isEmpty(countryCodeValue) ? countryCodeValue.toUpperCase() : Locale.getDefault().getCountry().toUpperCase());
        sRet += "\n";
        sRet += "- Language: " + Locale.getDefault().getDisplayLanguage();
        sRet += "\n";
        sRet += "- Device ID: " + AppBancomatDataManager.getInstance().getMobileDevice().getUuid();
        sRet += "\n";
        sRet += "- User ID: " + AppBancomatDataManager.getInstance().getUserAccountId();
        sRet += "\n";
        sRet += "---";
        sRet += "\n";
        sRet += activity.getString(R.string.support_email_body_your_message) + ":\n\n";

        return sRet;
    }

}
