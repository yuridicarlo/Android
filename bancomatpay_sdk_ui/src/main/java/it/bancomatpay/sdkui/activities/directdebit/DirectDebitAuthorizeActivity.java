package it.bancomatpay.sdkui.activities.directdebit;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmDirectDebitAuthorizeBinding;
import it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager.DIRECT_DEBIT_REQUEST_DATA;
import static it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager.IS_FROM_NOTIFICATION;

public class DirectDebitAuthorizeActivity extends GenericErrorActivity {

    private DirectDebitRequest directDebitRequest;
    private boolean isFromNotification;
    private ActivityBcmDirectDebitAuthorizeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBcmDirectDebitAuthorizeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setActivityName(DirectDebitAuthorizeActivity.class.getSimpleName());

        directDebitRequest = (DirectDebitRequest) getIntent().getSerializableExtra(DIRECT_DEBIT_REQUEST_DATA);
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

        binding.textMerchantName.setText(directDebitRequest.getMerchantName());
        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> onConfirmClicked()));
        binding.refuseButton.setOnClickListener(new CustomOnClickListener(v -> DirectDebitFlowManager.goToDirectDebitAuthorizeResult(this, true, null, directDebitRequest, isFromNotification)));

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void onConfirmClicked() {

        String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();

        BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
        bcmPaymentAuthorization.setAmount(null);
        bcmPaymentAuthorization.setOperation(AuthenticationOperationType.DIRECT_DEBITS);
        bcmPaymentAuthorization.setReceiver(directDebitRequest.getTag());
        bcmPaymentAuthorization.setSender(userMsisdn);

        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForDirectDebits(this,
                        bcmPaymentAuthorization, (authenticated, authToken) -> {
                            if (authenticated) {
                                DirectDebitFlowManager.goToDirectDebitAuthorizeResult(this, false, authToken, directDebitRequest, isFromNotification);
                            }
                        });
    }


}
