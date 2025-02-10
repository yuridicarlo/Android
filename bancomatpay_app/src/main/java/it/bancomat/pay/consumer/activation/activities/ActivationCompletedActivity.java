package it.bancomat.pay.consumer.activation.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.events.ForceReopenBancomatFlow;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.notification.MyFirebaseMessagingService;
import it.bancomat.pay.consumer.notification.MyHmsMessageService;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomatpay.consumer.databinding.ActivityActivationCompletedBinding;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

public class ActivationCompletedActivity extends AppGenericErrorActivity {

    public static final String BANK_UUID = "BANK_UUID";
    private static final String TAG = ActivationCompletedActivity.class.getSimpleName();

    private boolean forceReopenBancomatFlow = false;
    private String bankUuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityActivationCompletedBinding binding = ActivityActivationCompletedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bankUuid = getIntent().getStringExtra(BANK_UUID);
        if (!BancomatPayApiInterface.Factory.getInstance().isFingerprintEnrollable()) {
            binding.textActivateFingerprintLabel.setVisibility(View.INVISIBLE);
            binding.textActivateFingerprintAction.setVisibility(View.INVISIBLE);
        }

        binding.textActivateFingerprintAction.setOnClickListener(new CustomOnClickListener(v ->
                ActivationFlowManager.goToSetFingerprint(this, forceReopenBancomatFlow, bankUuid)));
        binding.buttonGoToHome.setOnClickListener(new CustomOnClickListener(v -> clickButtonHome()));

        if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            MyFirebaseMessagingService.registerCurrentToken(this);
        } else if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            MyHmsMessageService.registerCurrentToken(this);
        }

    }

    private void clickButtonHome() {

        if (forceReopenBancomatFlow) {
            BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                    .startBancomatPayFlowClearTask(this, null, false,
                            AppBancomatDataManager.getInstance().getTokens().getOauth());
        } else {
            BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                    .startBancomatPayFlow(this, null,
                            AppBancomatDataManager.getInstance().getTokens().getOauth());
        }
        FullStackSdkDataManager.getInstance().putHomePanelExpanded(false);
        finish();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ForceReopenBancomatFlow event) {
        EventBus.getDefault().removeStickyEvent(event);
        this.forceReopenBancomatFlow = true;
    }

}
