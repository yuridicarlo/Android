package it.bancomat.pay.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.huawei.hms.api.HuaweiApiAvailability;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.login.LoginFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.callable.GetBanksConfigurationBank;
import it.bancomat.pay.consumer.notification.MyFirebaseMessagingService;
import it.bancomat.pay.consumer.notification.MyHmsMessageService;
import it.bancomat.pay.consumer.notification.Push;
import it.bancomat.pay.consumer.notification.PushConstant;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppApplicationModel;
import it.bancomat.pay.consumer.utilities.AppCjUtils;
import it.bancomat.pay.consumer.utilities.AppErrorMapper;
import it.bancomat.pay.consumer.utilities.AppFullscreenActivity;
import it.bancomat.pay.consumer.utilities.DeepLink;
import it.bancomat.pay.consumer.utilities.apptoapp.AppToAppData;
import it.bancomatpay.consumer.BuildConfig;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivitySplashBinding;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.lifecycle.CompletableListener;
import it.bancomatpay.sdk.manager.lifecycle.LiveUtil;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveCompletable;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveSingle;
import it.bancomatpay.sdk.manager.model.VoidResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.EPlayServicesType;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2B_QRCODE_IS_NOT_VALID;

public class SplashActivity extends AppFullscreenActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final String PARAMETER_PAYMENT_ID = "/?#";

    private Push pushEvent;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pushEvent = (Push) getIntent().getSerializableExtra(PushConstant.EXTRA_PUSH);

        CustomLogger.d(TAG, "pushEvent " + pushEvent);

        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (Exception e) {
            CustomLogger.e(TAG, "Update security services error");
        }

        BancomatPayApiInterface sdk = BancomatPayApiInterface.Factory.getInstance();

        PayCore.initialize(this);

        updatePlayServicesAvailability();
        AppCjUtils.updateAppsFlyerCuidIfNeeded();

        BancomatDataManager.getInstance().putAntirootCheckEnabled(!BuildConfig.DEBUG);
        BancomatDataManager.getInstance().putBlockIfRooted(true);

        AppApplicationModel.getInstance().updateCustomerJourneyRowCount();


        hideSystemUi(this);
        mHandler.postDelayed(this::showSystemUiAndEnableFullScreen, 1500);

        mHandler.postDelayed(() -> {

            if (sdk.isUserRegistered()) {

                int timeAccessHome = FullStackSdkDataManager.getInstance().getTimeToAccessInHome();
                FullStackSdkDataManager.getInstance().putTimeToAccessInHome(timeAccessHome + 1);

                Intent intent = getIntent();
                String paymentData = intent.getDataString();

                if (checkPaymentData(paymentData)) {
                    if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
                        MyFirebaseMessagingService.registerCurrentToken(this);
                    } else if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
                        MyHmsMessageService.registerCurrentToken(this);
                    }
                    goToNextPage();
                }

            } else {
                Result<AppToAppData> result = sdk.checkActivationFromDeepLink(getIntent());
                if (result.isSuccess()) {
                    DataBank dataBank = DataBankManager.getDataBank(result.getResult().getBankUuid());
                    ActivationFlowManager.goToChooseActivationModeDeepLink(
                            this, dataBank, result.getResult().getToken(), result.getResult().getActivationCode());
                } else {
                    manageInit();
                }
            }

        }, 1200);

    }

    private void updatePlayServicesAvailability() {
        int resultGoogle = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        boolean isGoogleServicesAvailable = (ConnectionResult.SUCCESS == resultGoogle);
        CustomLogger.d(TAG, "isPlayServicesAvailable: " + isGoogleServicesAvailable);

        if (isGoogleServicesAvailable) {
            BancomatDataManager.getInstance().putPlayServicesAvailability(EPlayServicesType.GOOGLE_PLAY_SERVICES);
        } else {
            int resultHuawei = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this);
            boolean isHuaweiServicesAvailable = (com.huawei.hms.api.ConnectionResult.SUCCESS == resultHuawei);
            CustomLogger.d(TAG, "isHmsAvailable: " + isHuaweiServicesAvailable);
            if (isHuaweiServicesAvailable) {
                BancomatDataManager.getInstance().putPlayServicesAvailability(EPlayServicesType.HUAWEI_SERVICES);
            } else {
                BancomatDataManager.getInstance().putPlayServicesAvailability(EPlayServicesType.NONE);
            }
        }
    }

    private boolean checkPaymentData(String paymentData) {
        if(!TextUtils.isEmpty(paymentData) && !BancomatPayApiInterface.Factory.getInstance().isBiometricMigrationNeed()) {

            if (paymentData.lastIndexOf(PARAMETER_PAYMENT_ID) > 0) {
                String qrCodeId = paymentData.substring(paymentData.lastIndexOf(PARAMETER_PAYMENT_ID) + PARAMETER_PAYMENT_ID.length());
                EventBus.getDefault().postSticky(new DeepLink(qrCodeId));

            } else {
                showErrorAndDoAction(P2B_QRCODE_IS_NOT_VALID, (dialog, which) -> finish());
                return false;
            }

        }
        return true;
    }

    private void manageInit() {
        Task<?> t = BancomatPayApiInterface.Factory.getInstance().doInit(result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    manageBanksFile(true);
                } else {
                    showErrorRetryInit(v -> manageInit());
                }
            }
        });
        addTask(t);
    }

    private void manageBanksFile(boolean fromInit) {

        MutableLiveCompletable getBankConfigurationResponse = new MutableLiveCompletable();

        getBankConfigurationResponse.setListener(this, new CompletableListener() {
            @Override
            protected void onComplete() {
                if(fromInit) {
                    ActivationFlowManager.goToIntro(SplashActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });


        LiveUtil.fromCallable(new GetBanksConfigurationBank())
                .onErrorReturnItem(VoidResponse.VALUE)
                .subscribe(LiveUtil.getWrapper(getBankConfigurationResponse));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void goToNextPage() {
        CustomLogger.d(TAG, "goToNextPage " + pushEvent);

        if (pushEvent != null) {
            EventBus.getDefault().postSticky(pushEvent);
        }
        manageBanksFile(false);
        if (BancomatPayApiInterface.Factory.getInstance().isLoginRequired() && !BancomatPayApiInterface.Factory.getInstance().isBiometricMigrationNeed()) {
            BancomatPayApiInterface.Factory.getInstance().setShowCjConsentsinHome(true);
            LoginFlowManager.goToLogin(this);
        } else {
            if (BancomatPayApiInterface.Factory.getInstance().isBancomatSdkInitialized()) {
                if (pushEvent != null) {
                    BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                            .startBancomatPayFlowClearTask(this, null, true, AppBancomatDataManager.getInstance().getTokens().getOauth());
                } else {
                    BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                            .startBancomatPayFlow(this, null, AppBancomatDataManager.getInstance().getTokens().getOauth());
                }
                BancomatPayApiInterface.Factory.getInstance().setShowCjConsentsinHome(true);
                finish();
            }
        }
    }

    @Override
    public void showError(StatusCodeInterface statusCodeInterface) {
        int idString = AppErrorMapper.getStringFromStatusCode(statusCodeInterface);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title);
        builder.setMessage(idString)
                .setCancelable(false);

        if (statusCodeInterface == StatusCode.Mobile.ROOTED) {
            builder.setPositiveButton(R.string.ok, (dialog, id) -> finish());
        } else {
            builder.setMessage(R.string.init_error_message);
            builder.setPositiveButton(R.string.retry, (dialog, id) -> manageInit());
        }

        if (!isFinishing() && !isDestroyed()) {
            builder.show();
        }
    }

}
