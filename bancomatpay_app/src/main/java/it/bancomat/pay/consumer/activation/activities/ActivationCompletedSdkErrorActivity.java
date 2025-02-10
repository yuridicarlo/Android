package it.bancomat.pay.consumer.activation.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.HashMap;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.ActivationData;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomatpay.consumer.databinding.ActivityActivationCompletedSdkErrorBinding;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomat.pay.consumer.activation.ActivationFlowManager.ACTIVATION_DATA;
import static it.bancomat.pay.consumer.activation.ActivationFlowManager.BANK_UUID;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ELAPSED;

public class ActivationCompletedSdkErrorActivity extends AppGenericErrorActivity {

    private ActivationData activationData;
    private String bankUUID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityActivationCompletedSdkErrorBinding binding = ActivityActivationCompletedSdkErrorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activationData = (ActivationData) getIntent().getSerializableExtra(ACTIVATION_DATA);
        bankUUID = getIntent().getStringExtra(BANK_UUID);

        binding.toolbarSimple.setOnClickRightImageListener(v -> onBackPressed());
        binding.buttonRetrySdkActivation.setOnClickListener(new CustomOnClickListener(v -> clickButtonRetrySdkActivation()));

    }

    private void clickButtonRetrySdkActivation() {
        LoaderHelper.showLoader(this);
        BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                .initBancomatSDKWithCheckRoot(this,
                        activationData.getAbiCode(), activationData.getGroupCode(), activationData.getPhoneNumber(),
                        activationData.getPhonePrefix(), activationData.getIban(),
                        AppBancomatDataManager.getInstance().getTokens().getOauth(), result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    HashMap<String, String> mapEventParams = new HashMap<>();
                                    mapEventParams.put(PARAM_ELAPSED, CjUtils.getInstance().getActivationTimeElapsed());
                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_ACTIVATION_COMPLETED, mapEventParams, true);
                                    ActivationFlowManager.goToActivationCompleted(this, activationData, bankUUID);
                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else {
                                    showError(result.getStatusCode());
                                }
                            }
                        });
    }

    @Override
    public void onBackPressed() {
        BancomatPayApiInterface.Factory.getInstance().deleteUserData();
        ActivationFlowManager.goToIntro(this);
    }

    @Override
    protected void onDestroy() {
        BancomatPayApiInterface.Factory.getInstance().deleteUserData();
        super.onDestroy();
    }

}
