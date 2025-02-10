package it.bancomatpay.sdk.core;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class PayCore {

    private static final String TAG = PayCore.class.getSimpleName();

    private static Context context;

    public static void initialize(Activity activity) {
        PayCore.context = activity.getApplicationContext();
        try {
            ProviderInstaller.installIfNeeded(activity);
        } catch (Exception e) {
            CustomLogger.e(TAG, "Update security services error");
            if (e instanceof GooglePlayServicesRepairableException) {
                GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                apiAvailability.getErrorDialog(activity,
                        ((GooglePlayServicesRepairableException) e).getConnectionStatusCode(),
                        1000, dialog -> activity.finish())
                        .show();
            }
        }
    }

    public static void setAppContext(Context context) {
        PayCore.context = context;
    }

    public static Context getAppContext() {
        return context;
    }

}
