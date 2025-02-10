package it.bancomatpay.sdkui.activities.cashaback;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityCashbackDetailBinding;
import it.bancomatpay.sdkui.flowmanager.CashbackDigitalPaymentFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.CashbackTermsAndConditionsBottomSheet;

import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.CASHBACK_STATUS_DATA_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_DISABLE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_STATUS_APPIO;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_UNSUBSCRIBE_BPAY;

public class CashbackDetailActivity extends GenericErrorActivity implements CashBackBPayTermsAndCoFragment.OnButtonInteractionListener {

    private CashbackStatusData mCashbackStatusData;
    private CashbackTermsAndConditionsBottomSheet bottomCashbackTermsAndConditions;
    private ActivityCashbackDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(CashbackDetailActivity.class.getSimpleName());
        binding = ActivityCashbackDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setLightStatusBar(binding.mainLayout, R.color.white_background);

        mCashbackStatusData = (CashbackStatusData) getIntent().getSerializableExtra(CASHBACK_STATUS_DATA_EXTRA);
        if (mCashbackStatusData != null && !mCashbackStatusData.isbPayTermsAndConditionsAccepted()) {
            if (TextUtils.isEmpty(BancomatDataManager.getInstance().getBPayTermsAndConditionsAcceptedTimestamp())) {
                showBcmPayTermsAndConditions();
            }
        }

        if (mCashbackStatusData != null) {
            if (mCashbackStatusData.getbPayUnsubscribedTimestamp() != null) {
                binding.toolbarSimple.setRightImageVisibility(false);
                binding.description.setText(getText(R.string.cashback_section_no_bcm_tool_desc));
                binding.cashbackActivationDateLabel.setText(getText(R.string.cashback_deactivation_date));
                binding.cashbackActivationDate.setText(StringUtils.getDateStringFormatted(mCashbackStatusData.getbPayUnsubscribedTimestamp(), "dd MMMM yyyy"));
                binding.unsubscribeButton.setText(getText(R.string.cashback_deregistration_btn));
                binding.unsubscribeButton.setOnClickListener(new CustomOnClickListener(v -> {
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_DISABLE, null, false);
                    showPagoPaUnsubscriptionDialog();
                }));
                binding.buttonActivateBpay.setVisibility(View.VISIBLE);
                binding.buttonActivateBpay.setOnClickListener(new CustomOnClickListener(v ->
                        CashbackDigitalPaymentFlowManager.goToCashbackConfirmActivation(this, mCashbackStatusData)
                ));
            } else {
                if (mCashbackStatusData.getbPaySubscribedTimestamp() != null) {
                    binding.cashbackActivationDate.setText((StringUtils.getDateStringFormatted(mCashbackStatusData.getbPaySubscribedTimestamp(), "dd MMMM yyyy")));
                }
                binding.unsubscribeButton.setOnClickListener(new CustomOnClickListener(v -> {
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_UNSUBSCRIBE_BPAY, null, false);
                    showBcmPayUnsubscriptionDialog();
                }));
                binding.toolbarSimple.setRightImageVisibility(true);
                binding.toolbarSimple.setOnClickRightImageListener(new CustomOnClickListener(v -> getCashbackData()));
            }
        }

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

        binding.buttonGoToAppIO.setOnClickListener(v -> goToPlayStore());
    }


    private void showBcmPayUnsubscriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.cashback_warning_message)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
                    bcmPaymentAuthorization.setOperation(AuthenticationOperationType.CASHBACK);
                    BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                            .authenticationNeededForCashback(this, bcmPaymentAuthorization, ECashbackAuthorizationTypeRequest.UNSUBSCRIBE_BCM_CASHBACK_TOOL,
                                    ((authenticated, result) -> {
                                        if (authenticated) {
                                            CashbackDigitalPaymentFlowManager.goToCashbackResult(this, result);
                                        }
                                    }));
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false);

        builder.show();
    }

    private void showPagoPaUnsubscriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.cashback_deregistration_confirmation_popup)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
                    bcmPaymentAuthorization.setOperation(AuthenticationOperationType.CASHBACK);
                    BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                            .authenticationNeededForCashback(this, bcmPaymentAuthorization, ECashbackAuthorizationTypeRequest.DISABLE_CASHBACK,
                                    ((authenticated, result) ->
                                            CashbackDigitalPaymentFlowManager.goToCashbackResult(this, result)));
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false);

        builder.show();
    }

    public void showBcmPayTermsAndConditions() {
        bottomCashbackTermsAndConditions = CashbackTermsAndConditionsBottomSheet.newInstance(mCashbackStatusData);
        bottomCashbackTermsAndConditions.show(getSupportFragmentManager(), CashbackTermsAndConditionsBottomSheet.TAG);
    }

    public void goToPlayStore() {
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_STATUS_APPIO, null, false);
        CashbackDigitalPaymentFlowManager.goToStore(this);
    }

    public void getCashbackData() {
        LoaderHelper.showLoader(this);
        BancomatSdkInterface.Factory.getInstance().doGetCashbackData(
                this, result -> {
                    LoaderHelper.dismissLoader();
                    if (result != null) {
                        if (result.isSuccess() && result.getResult() != null) {
                            CashbackDigitalPaymentFlowManager.goToCashBackRanking(this, result.getResult());
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                }, SessionManager.getInstance().getSessionToken());
    }


    @Override
    public void onBpayYesClicked() {
        BancomatSdkInterface.Factory.getInstance().doAcceptCashbackBpayTermsAndConditions(this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            bottomCashbackTermsAndConditions.dismiss();
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                }
                , "", SessionManager.getInstance().getSessionToken());
    }
}