package it.bancomat.pay.consumer.activation.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.bancomat.pay.consumer.extended.BancomatFullStackSdkInterfaceExtended;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.consumer.databinding.ActivityFingerprintInsertedSuccessBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomat.pay.consumer.activation.ActivationFlowManager.FORCE_REOPEN_BANCOMAT_FLOW;

public class SetFingerprintSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFingerprintInsertedSuccessBinding binding = ActivityFingerprintInsertedSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonGoToHome.setOnClickListener(new CustomOnClickListener(v -> clickHomeButton()));
    }

    private void clickHomeButton() {
        FullStackSdkDataManager.getInstance().putHomePanelExpanded(false);
        if (getIntent().getBooleanExtra(FORCE_REOPEN_BANCOMAT_FLOW, false)) {
            BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                    .startBancomatPayFlowClearTask(this, null, false, AppBancomatDataManager.getInstance().getTokens().getOauth());
        } else {
            BancomatFullStackSdkInterfaceExtended.Factory.getInstance()
                    .startBancomatPayFlow(this, null, AppBancomatDataManager.getInstance().getTokens().getOauth());
        }
        finish();
    }

}
