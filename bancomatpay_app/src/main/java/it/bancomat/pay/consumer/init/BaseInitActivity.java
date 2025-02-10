package it.bancomat.pay.consumer.init;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProvider;

import org.greenrobot.eventbus.EventBus;

import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomatpay.sdkui.utilities.ExtendedProgressDialogFragment;
import it.bancomat.pay.consumer.viewmodel.InitViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;
import it.bancomatpay.consumer.R;

import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_CODE;
import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_FROM_DEEP_LINK;


public abstract class BaseInitActivity extends AppCompatActivity {

    private final static String TAG = BaseInitActivity.class.getSimpleName();
    protected WindowViewModel windowViewModel;
    protected InitViewModel initViewModel;


    abstract int getContentView();

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    ActivityResultLauncher<Intent> activityResultLauncherDeviceCredentials = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                EventBus.getDefault().post(new ActivityResult(REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, result.getResultCode(), data));
            });

    public  ActivityResultLauncher<Intent> getActivityResultLauncherDeviceCredentials() {
        return activityResultLauncherDeviceCredentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        initViewModel = new ViewModelProvider(this).get(InitViewModel.class);
        windowViewModel = new ViewModelProvider(this).get(WindowViewModel.class);
        initViewModel.restoreSaveInstanceState(savedInstanceState);
        windowViewModel.getStatusBarColor().observe(this, color -> {
            if (color == R.color.white_background) { //qui gestionamo i background chiari (icone nere)
                getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
                setLightStatusBar(getWindow().getDecorView());
            } else if (color == R.color.blue_status_bar){ // qui gestiamo i background scuri (icone chiare)
                getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
                clearLightStatusBar(getWindow().getDecorView());
            } else { // gestore di default
                getWindow().setStatusBarColor(color);
                if (ColorUtils.calculateLuminance(color) < 0.5) {
                    clearLightStatusBar(getWindow().getDecorView());
                } else {
                    setLightStatusBar(getWindow().getDecorView());
                }
            }

        });

        windowViewModel.getNavigationBarColor().observe(this, color -> {

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, color));

        });

        if(getIntent().getBooleanExtra(ACTIVATION_FROM_DEEP_LINK, false)){
            initViewModel.setFromDeepLink(true);
            initViewModel.setActivationCodeFromDeepLink(getIntent().getStringExtra(ACTIVATION_CODE));
        }


        windowViewModel.getLoader().observe(this, integer -> {

            new ExtendedProgressDialogFragment().showNow(getSupportFragmentManager(), "");

        });

    }


    private void setLightStatusBar(View view) {
        if (Build.VERSION.SDK_INT >= 30){
            WindowInsetsController ic = getWindow().getInsetsController();
            if(ic!= null){
                ic.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            }
        } else {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }


    private void clearLightStatusBar(View view) {
        if (Build.VERSION.SDK_INT >= 30){
            WindowInsetsController ic = getWindow().getInsetsController();
            if(ic!= null){
                ic.setSystemBarsAppearance(0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            }
        } else {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        initViewModel.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
