package it.bancomatpay.sdkui.activities.cashaback;

import android.os.Bundle;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityCashbackConfirmActivationBinding;
import it.bancomatpay.sdkui.flowmanager.CashbackDigitalPaymentFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.CashbackTermsAndConditionsBottomSheet;

import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.CASHBACK_STATUS_DATA_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_CONFIRM_ENROLLMENT;

public class CashbackConfirmActivationActivity extends GenericErrorActivity
        implements CashBackBPayTermsAndCoFragment.OnButtonInteractionListener, CashBackPagoPaTermsAndCoFragment.OnButtonInteractionListener {

    private CashbackStatusData mCashbackStatusData;
    private CashbackTermsAndConditionsBottomSheet bottomCashbackTermsAndConditions;
    private ActivityCashbackConfirmActivationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(CashbackConfirmActivationActivity.class.getSimpleName());
        binding = ActivityCashbackConfirmActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mCashbackStatusData = (CashbackStatusData) getIntent().getSerializableExtra(CASHBACK_STATUS_DATA_EXTRA);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

        if (mCashbackStatusData != null) {
            if (mCashbackStatusData.isbPayTermsAndConditionsAccepted() && mCashbackStatusData.isPagoPaCashbackEnabled()) {
                binding.buttonConfirm.setEnabled(true);
            } else {
                binding.buttonConfirm.setEnabled(false);
                bottomCashbackTermsAndConditions = CashbackTermsAndConditionsBottomSheet.newInstance(mCashbackStatusData);
                bottomCashbackTermsAndConditions.show(getSupportFragmentManager(), CashbackTermsAndConditionsBottomSheet.TAG);
            }
        }

        binding.buttonConfirm.setOnClickListener(new CustomOnClickListener(v -> {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(
                    CashbackConfirmActivationActivity.this, KEY_CASHBACK_CONFIRM_ENROLLMENT, null, false);
            onBcmPaySubscription();
        }));

    }

    public void onBcmPaySubscription() {
        BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
        bcmPaymentAuthorization.setOperation(AuthenticationOperationType.CASHBACK);
        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForCashback(this, bcmPaymentAuthorization,
                        ECashbackAuthorizationTypeRequest.SUBSCRIBE_BCM_CASHBACK_TOOL,
                        ((authenticated, result) -> {
                            if (authenticated) {
                                CashbackDigitalPaymentFlowManager.goToCashbackResult(this, result);
                            }
                        })
                );
    }

    @Override
    public void onPagoPaYesClicked() {
        bottomCashbackTermsAndConditions.dismiss();
        binding.buttonConfirm.setEnabled(true);
    }

    @Override
    public void onBpayYesClicked() {

        BancomatSdkInterface.Factory.getInstance().doAcceptCashbackBpayTermsAndConditions(this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            if (bottomCashbackTermsAndConditions.isVisible() && !mCashbackStatusData.isPagoPaCashbackEnabled()) {
                                bottomCashbackTermsAndConditions.onFragmentChanged();
                            } else {
                                bottomCashbackTermsAndConditions.dismiss();
                                binding.buttonConfirm.setEnabled(true);
                            }
                        }else{
                            showError(result.getStatusCode());
                        }
                    }
                }
                , "", SessionManager.getInstance().getSessionToken());
    }


    @Override
    public void onBackPressed() {
        finish();
    }

}
