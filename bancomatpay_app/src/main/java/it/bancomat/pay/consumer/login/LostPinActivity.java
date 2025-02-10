package it.bancomat.pay.consumer.login;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import it.bancomat.pay.consumer.events.ForceReopenBancomatFlow;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityLostPinBinding;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class LostPinActivity extends AppGenericErrorActivity {

    private boolean isBlocked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLostPinBinding binding = ActivityLostPinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isBlocked = getIntent().getBooleanExtra(LoginFlowManager.IS_BLOCKED, false);
        if (isBlocked) {
            binding.textLostPinTitle.setText(R.string.login_block_pin_title);
            binding.textLostPinLabel.setText(R.string.login_block_pin_label);
        }

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        binding.buttonActivateBcmpay.setOnClickListener(new CustomOnClickListener(v -> clickButtonActivateBcmPay()));

    }

    private void clickButtonActivateBcmPay() {
        BancomatPayApiInterface.Factory.getInstance().deleteUserData();
        EventBus.getDefault().postSticky(new ForceReopenBancomatFlow());
        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                .goToHome(this, true, true, true);
    }

    @Override
    public void onBackPressed() {
        if (isBlocked) {
            clickButtonActivateBcmPay();
        } else {
            super.onBackPressed();
        }
    }

}
