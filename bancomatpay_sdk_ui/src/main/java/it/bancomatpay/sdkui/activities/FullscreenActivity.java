package it.bancomatpay.sdkui.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import it.bancomatpay.sdkui.utilities.SnackbarUtil;

public abstract class FullscreenActivity extends GenericErrorActivity {

    private static final String TAG = "INSETS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    public static int isEdgeToEdgeEnabled(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            return resources.getInteger(resourceId);
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SnackbarUtil.setMarginTop(90);
    }

    public boolean hasSoftKeys() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return false;
        }

        boolean hasSoftwareKeys;

        Display display = getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;

        return hasSoftwareKeys;
    }

}
