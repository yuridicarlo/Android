package it.bancomatpay.sdkui.activities.cashaback;

import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityCashbackActivationResultBinding;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.flowmanager.CashbackDigitalPaymentFlowManager;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdk.manager.model.ECashbackActivationResult.CASHBACK_DISABLING_FAILED;
import static it.bancomatpay.sdk.manager.model.ECashbackActivationResult.CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS;
import static it.bancomatpay.sdkui.flowmanager.CashbackDigitalPaymentFlowManager.CASHBACK_AUTHENTICATION_RESULT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_DISABLE_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_ENROLLMENT_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_ENROLLMENT_COMPLETED_APPIO;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_ENROLLMENT_COMPLETED_CLOSE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_ENROLLMENT_COMPLETED_RETRY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_CASHBACK_UNSUBSCRIBE_BPAY_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_STATUS;

public class CashbackActivationResultActivity extends GenericErrorActivity {

    private ActivityCashbackActivationResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(CashbackActivationResultActivity.class.getSimpleName());
        binding = ActivityCashbackActivationResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (FullStackSdkDataManager.getInstance().getTimeToShowCashbackDialog() != 0) {
            FullStackSdkDataManager.getInstance().putTimesToShowCashbackDialog(0);
        }
        ECashbackActivationResult result = (ECashbackActivationResult) getIntent().getSerializableExtra(CASHBACK_AUTHENTICATION_RESULT);
        if (result != null) {
            switch (result) {
                case SUBSCRIBED_SUCCESS:
                    HashMap<String, String> mapEventParams1 = new HashMap<>();
                    mapEventParams1.put(PARAM_STATUS, "ok");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_ENROLLMENT_COMPLETED, mapEventParams1, false);
                    setStatusSuccess();
                    break;
                case SUBSCRIBED_FAILURE:
                    HashMap<String, String> mapEventParams2 = new HashMap<>();
                    mapEventParams2.put(PARAM_STATUS, "ko");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_ENROLLMENT_COMPLETED, mapEventParams2, false);
                    setStatusFail();
                    break;
                case SUBSCRIBED_UNDEFINED:
                    setStatusTimeout();
                    break;
                case BPAY_UNSUBSCRIBED_SUCCESS:
                    HashMap<String, String> mapEventParams3 = new HashMap<>();
                    mapEventParams3.put(PARAM_STATUS, "ok");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_UNSUBSCRIBE_BPAY_COMPLETED, mapEventParams3, false);
                    setBPayUnsubscriptionSuccess();
                    break;
                case BPAY_UNSUBSCRIBED_FAILURE:
                    HashMap<String, String> mapEventParams4 = new HashMap<>();
                    mapEventParams4.put(PARAM_STATUS, "ko");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_UNSUBSCRIBE_BPAY_COMPLETED, mapEventParams4, false);
                    setBPayUnsubscriptionFail();
                    break;
                case CASHBACK_DISABLING_SUCCESS:
                    HashMap<String, String> mapEventParams5 = new HashMap<>();
                    mapEventParams5.put(PARAM_STATUS, "ok");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_DISABLE_COMPLETED, mapEventParams5, false);
                    setDisableCashbackSuccess();
                    break;
                case CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS:
                case CASHBACK_DISABLING_FAILED:
                    HashMap<String, String> mapEventParams6 = new HashMap<>();
                    mapEventParams6.put(PARAM_STATUS, "ko");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_DISABLE_COMPLETED, mapEventParams6, false);
                    if (result == CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS) {
                        setDisableCashbackFailedDueToMoreTool();
                    } else if (result == CASHBACK_DISABLING_FAILED) {
                        setDisableCashbackFailed();
                    }
                    break;
            }
        }
    }

    private void setStatusSuccess() {
        binding.title.setText(getString(R.string.cashback_activation_completed_title));
        binding.description.setText(getString(R.string.cashback_activation_completed_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_attivato_ok);
        AnimationFadeUtil.startFadeInAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION);
        binding.closeButton.setOnClickListener(new CustomOnClickListener(v -> goToHomeFragment()));
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(new CustomOnClickListener(v -> goToPlayStore()));
    }

    private void setStatusFail() {
        binding.title.setText(getString(R.string.cashback_activation_failed_title));
        binding.description.setText(getString(R.string.cashback_activation_failed_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_nonattivato_ko);
        AnimationFadeUtil.startFadeInAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION);
        binding.closeButton.setOnClickListener(new CustomOnClickListener(v -> goToHomeFragment()));
        binding.buttonGoToAppIO.setText(getString(R.string.cashback_activation_failed_retry_button));
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(new CustomOnClickListener(v -> {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_ENROLLMENT_COMPLETED_RETRY, null, false);
            getCashbackStatus();
        }));
    }

    private void setStatusTimeout() {
        binding.title.setText(getString(R.string.cashback_title));
        binding.description.setText(getString(R.string.cashback_activation_timeout_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_elaborazione);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        binding.buttonGoToAppIO.setText(getString(R.string.close_button));
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(new CustomOnClickListener(v -> goToHomeFragment()));
    }

    private void setBPayUnsubscriptionSuccess() {
        binding.title.setText(getString(R.string.cashback_bcm_pay_tool_unregistered_title));
        binding.description.setText(getString(R.string.cashback_unsubscribed_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_disattivato);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        binding.buttonGoToAppIO.setText(getString(R.string.close_button));
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(view -> goToHomeFragment());
    }

    private void setBPayUnsubscriptionFail() {
        binding.title.setText(getString(R.string.cashback_bcm_pay_tool_unsbscription_ko_title));
        binding.description.setText(getString(R.string.cashback_activation_failed_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_nonattivato_ko);
        binding.buttonGoToAppIO.setText(R.string.close_button);
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(view -> goToHomeFragment());
    }

    private void setDisableCashbackSuccess() {
        binding.title.setText(getString(R.string.cashback_unsubscribed_title));
        binding.description.setText(getString(R.string.cashback_deregistration_success_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_disattivato);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        binding.buttonGoToAppIO.setText(getString(R.string.close_button));
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(view -> goToHomeFragment());
    }

    private void setDisableCashbackFailedDueToMoreTool() {
        binding.title.setText(getString(R.string.cashback_unsubscribed_ko_title));
        binding.description.setText(getString(R.string.cashback_deregistration_active_tool_ko_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_nonattivato_ko);
        AnimationFadeUtil.startFadeInAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION);
        binding.closeButton.setOnClickListener(view -> goToHomeFragment());
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(new CustomOnClickListener(v -> goToPlayStore()));
    }

    private void setDisableCashbackFailed() {
        binding.title.setText(getString(R.string.cashback_unsubscribed_ko_title));
        binding.description.setText(getString(R.string.cashback_activation_failed_description));
        binding.imageResult.setImageResource(R.drawable.illustration_cashback_nonattivato_ko);
        binding.buttonGoToAppIO.setText(R.string.close_button);
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonGoToAppIO, AnimationFadeUtil.DEFAULT_DURATION);
        binding.buttonGoToAppIO.setOnClickListener(view -> goToHomeFragment());
    }

    private void goToPlayStore() {
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_ENROLLMENT_COMPLETED_APPIO, null, false);
        CashbackDigitalPaymentFlowManager.goToStore(this);
    }

    private void goToHomeFragment() {
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_CASHBACK_ENROLLMENT_COMPLETED_CLOSE, null, false);
        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                .goToHome(this, false, false, false);
    }

    private void getCashbackStatus() {
        LoaderHelper.showLoader(this);
        BancomatSdkInterface.Factory.getInstance().doGetCashbackStatus(
                this, result -> {
                    LoaderHelper.dismissLoader();
                    if (result != null) {
                        if (result.isSuccess()) {
                            if ((result.getResult().isbPaySubscribed() && result.getResult().isPagoPaCashbackEnabled()) ||
                                    (!result.getResult().isbPaySubscribed() && result.getResult().isPagoPaCashbackEnabled()
                                            && result.getResult().getbPayUnsubscribedTimestamp() != null)
                            ) {
                                HomeFlowManager.goToCashbackDigitalPayments(this, result.getResult());
                            } else {
                                HomeFlowManager.goToCashbackTermsAndConditions(this, result.getResult());
                            }
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                }, SessionManager.getInstance().getSessionToken());
    }


}
