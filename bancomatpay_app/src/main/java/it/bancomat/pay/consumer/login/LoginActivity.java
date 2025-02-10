package it.bancomat.pay.consumer.login;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.greenrobot.eventbus.EventBus;

import java.security.InvalidKeyException;
import java.security.UnrecoverableKeyException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.biometric.ActivityResult;
import it.bancomat.pay.consumer.biometric.PromptInfoHelper;
import it.bancomat.pay.consumer.biometric.callable.GetAppAuthentication;
import it.bancomat.pay.consumer.biometric.exception.BiometryException;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityLoginBinding;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.lifecycle.LiveUtil;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveSingle;
import it.bancomatpay.sdk.manager.lifecycle.SingleListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;

import static it.bancomat.pay.consumer.login.LoginFlowManager.PAYMENT_DATA_DEEPLINK;


public class LoginActivity extends AppGenericErrorActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();
    private String paymentDataDeeplink;
    private ActivityLoginBinding binding;
    private MutableLiveSingle<AppAuthenticationInterface> getAppAuthenticationResponse;

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    ActivityResultLauncher<Intent> activityResultLauncherDeviceCredentials = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                EventBus.getDefault().post(new ActivityResult(REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, result.getResultCode(), data));
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue_status_bar));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.home_background));

        clearLightStatusBar(getWindow().getDecorView());

        //per evitare doppio click accidentali e crash dovuti a distruzione activity
        getAppAuthenticationResponse = new MutableLiveSingle<>();
        getAppAuthenticationResponse.setListener(this, new SingleListener<AppAuthenticationInterface>() {
            @Override
            public void onSuccess(AppAuthenticationInterface response) {
                login(response);
            }

            @Override
            public void onError(Throwable throwable) {
                String bankUUID = AppBancomatDataManager.getInstance().getBankUuid();
                Task<?> t = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    CustomLogger.d(TAG, "doUserMonitoringTask success");
                                } else {
                                    CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                                }
                            }
                        },
                        bankUUID,
                        UserMonitoringConstants.WRONG_FINGERPRINT_LOGIN,
                        UserMonitoringConstants.WRONG_FINGERPRINT_LOGIN_EVENT,
                        "");
                addTask(t);


                if(throwable instanceof KeyPermanentlyInvalidatedException || throwable instanceof UnrecoverableKeyException){
                    showExitAppError();
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                }


            }
        });

        paymentDataDeeplink = getIntent().getStringExtra(PAYMENT_DATA_DEEPLINK);
        binding.activationButton.setOnClickListener(v -> {
            if(getAppAuthenticationResponse.isNotPending()) {
                if (BancomatPayApiInterface.Factory.getInstance().isDeviceSecured()) {
                    Single.fromCallable(new GetAppAuthentication(LoginActivity.this, PromptInfoHelper.getPromptInfo(), REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, activityResultLauncherDeviceCredentials))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(LiveUtil.getWrapper(getAppAuthenticationResponse));

                } else {
                    AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(LoginActivity.this);
                    builder.setTitle(R.string.enable_biometry_dialog_title)
                            .setMessage(R.string.enable_biometry_dialog_description)
                            .setPositiveButton(R.string.enable_biometry_dialog_confirm, (dialog, id) -> {
                                Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                                try {
                                    startActivity(intent);
                                }catch (ActivityNotFoundException e){}
                                dialog.dismiss();
                            })
                            .setNegativeButton(R.string.enable_biometry_dialog_cancel, (dialog, id) -> {
                                dialog.dismiss();
                            })
                            .setCancelable(false);
                    builder.showDialog(LoginActivity.this);
                }
            }
        });

    }


    private void login(AppAuthenticationInterface appAuthenticationInterface){
        LoaderHelper.showLoader(this);
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doLogin(result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    goToNextPage();
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, appAuthenticationInterface);
        addTask(t);
    }


    private void goToNextPage() {

        BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                .startBancomatPayFlow(this, null, AppBancomatDataManager.getInstance().getTokens().getOauth());
        finish();

    }

    private void clearLightStatusBar(View view) {
        if (Build.VERSION.SDK_INT >= 30){
            WindowInsetsController ic = getWindow().getInsetsController();
            if(ic!= null){
                ic.setSystemBarsAppearance(0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

}
