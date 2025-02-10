package it.bancomat.pay.consumer.utilities;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

public class AppFullscreenActivity extends AppGenericErrorActivity {

    private static final String TAG = AppFullscreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SnackbarUtil.setMarginTop(90);
    }

    public void showSystemUiAndEnableFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.show(WindowInsets.Type.systemBars());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }
    }

    public void hideSystemUi(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.systemBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
        saveInsets(activity.getWindow().getDecorView());
    }

    public void saveInsets(View view) {

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int insetTop = insets.getSystemWindowInsetTop();
            int insetBottom = insets.getMandatorySystemGestureInsets().bottom;

            if (insetTop != 0) {
                BancomatDataManager.getInstance().putScreenInsetTop(insetTop);
            }

            BancomatDataManager.getInstance().putScreenInsetBottom(insetBottom);

            CustomLogger.d(TAG, "Inset top = " + insetTop);
            CustomLogger.d(TAG, "Inset bottom = " + insetBottom);

            return insets;
        });
    }

    public static int isEdgeToEdgeEnabled(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            return resources.getInteger(resourceId);
        }
        return 0;
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
