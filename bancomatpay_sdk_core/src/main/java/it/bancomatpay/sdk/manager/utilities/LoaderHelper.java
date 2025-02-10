package it.bancomatpay.sdk.manager.utilities;

import androidx.appcompat.app.AppCompatActivity;

public class LoaderHelper {

    private static CustomProgressDialogFragment progressDialog;

    public static void showLoader(AppCompatActivity activity) {
        progressDialog = new CustomProgressDialogFragment();
        progressDialog.showNow(activity.getSupportFragmentManager(), "");
    }

    public static void dismissLoader() {
        if (progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
            progressDialog = null;
        }
    }

}
