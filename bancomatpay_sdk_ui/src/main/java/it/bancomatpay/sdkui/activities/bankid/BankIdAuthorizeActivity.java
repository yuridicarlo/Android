package it.bancomatpay.sdkui.activities.bankid;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmBankIdAuthorizeBinding;
import it.bancomatpay.sdkui.flowmanager.BankIdFlowManager;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.IS_FROM_NOTIFICATION;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.PROVIDER_MERCHANT_DATA;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.REQUEST_ID;

public class BankIdAuthorizeActivity extends GenericErrorActivity {

    private String requestId;
    private BankIdMerchantData merchantData;
    private boolean isFromNotification;
    private ActivityBcmBankIdAuthorizeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(BankIdAuthorizeActivity.class.getSimpleName());
        binding = ActivityBcmBankIdAuthorizeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestId = getIntent().getStringExtra(REQUEST_ID);
        merchantData = (BankIdMerchantData) getIntent().getSerializableExtra(PROVIDER_MERCHANT_DATA);
        isFromNotification = getIntent().getBooleanExtra(IS_FROM_NOTIFICATION, false);

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setOnClickRightImageListener(v -> {
            if (isFromNotification) {
                onBackPressed();
            } else {
                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                        .goToHome(this, false, false, false);
            }
        });

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> onConfirmClicked()));
        binding.refuseButton.setOnClickListener(new CustomOnClickListener(v -> BankIdFlowManager.goToBankIdAuthorizeResult(BankIdAuthorizeActivity.this, true, null, requestId, merchantData)));
        binding.textProviderName.setText(merchantData.getMerchantName());
        binding.textAuthorizationLabel.setText(getString(R.string.bank_id_authorize_access_label, merchantData.getMerchantName()));
        binding.textAuthorizationSublabel.setText(getString(R.string.bank_id_authorize_access_sublabel, merchantData.getMerchantName()));

        BancomatDataManager.getInstance().putRequestId(requestId);

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

    }


    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void onConfirmClicked() {
        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForProviderAccess(this, (authenticated, authToken) -> {
                    if (authenticated) {
                        BankIdFlowManager.goToBankIdAuthorizeResult(this, false, authToken, requestId, merchantData);
                    }
                }, requestId, merchantData.getMerchantTag());
    }

}
