package it.bancomatpay.sdkui.activities.directdebit;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmDirectDebitAuthorizeResultBinding;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;

import static it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager.AUTHORIZATION_TOKEN;
import static it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager.DIRECT_DEBIT_REQUEST_DATA;
import static it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager.IS_DENY_REQUEST;
import static it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager.IS_FROM_NOTIFICATION;

public class DirectDebitAuthorizeResultActivity extends GenericErrorActivity {

    private static final String TAG = DirectDebitAuthorizeActivity.class.getSimpleName();

    private DirectDebitRequest directDebitRequest;
    private String authToken;

    private ActivityBcmDirectDebitAuthorizeResultBinding binding;
    private boolean isFromNotification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(DirectDebitAuthorizeResultActivity.class.getSimpleName());
        binding = ActivityBcmDirectDebitAuthorizeResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isDenyDirectDebitRequest = getIntent().getBooleanExtra(IS_DENY_REQUEST, false);
        isFromNotification = getIntent().getBooleanExtra(IS_FROM_NOTIFICATION, false);

        directDebitRequest = (DirectDebitRequest) getIntent().getSerializableExtra(DIRECT_DEBIT_REQUEST_DATA);
        authToken = getIntent().getStringExtra(AUTHORIZATION_TOKEN);
        binding.textMerchantName.setText(directDebitRequest.getMerchantName());

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setRightImageVisibility(true);
        binding.toolbarSimple.setOnClickRightImageListener(v -> {
                    if (isFromNotification) {
                        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                    }
                    BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                            .goToHome(this, false, false, false);
                }
        );

        initTextSwitcher();
        initLayout(isDenyDirectDebitRequest);

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void initTextSwitcher() {

        binding.resultTextSwitcher.setFactory(() -> new TextView(
                new ContextThemeWrapper(this, R.style.AmountLabel), null, 0));

        Animation inAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        inAnim.setDuration(200);
        outAnim.setDuration(200);

        binding.resultTextSwitcher.setInAnimation(inAnim);
        binding.resultTextSwitcher.setOutAnimation(outAnim);

        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, 200);

    }

    private void initLayout(boolean isDenyBankIdRequest) {
        if (!isDenyBankIdRequest) {
            confirmDirectDebitRequest();
        } else {
            denyDirectDebitRequest();
        }
    }

    public void confirmDirectDebitRequest() {
        binding.resultLoader.playAnimation();
        BancomatSdkInterface.Factory.getInstance().doConfirmDirectDebitsRequest(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    binding.resultTextSwitcher.setText(getString(R.string.debits_registration_ok_description, directDebitRequest.getMerchantName()));
                    binding.resultAnimation.setAnimation(getString(R.string.check_ok));
                    AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
                    binding.resultAnimation.playAnimation();
                } else if (result.isSessionExpired()) {
                    binding.resultTextSwitcher.setText(getText(R.string.debits_registration_ko_description));
                    showOnErrorLayout();
                } else {
                    showError(result.getStatusCode());
                    showOnErrorLayout();
                }
            } else {
                showOnErrorLayout();
            }
        }, directDebitRequest.getRequestId(), directDebitRequest.getTag(), authToken, SessionManager.getInstance().getSessionToken());
    }

    public void denyDirectDebitRequest() {
        binding.resultLoader.playAnimation();
        BancomatSdkInterface.Factory.getInstance().doDenyDirectDebitsRequest(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    CustomLogger.d(TAG, "doDenyDebitRequest success");
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener().onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                }
            }
            showOnErrorLayout();
        }, directDebitRequest.getRequestId(), directDebitRequest.getTag(), SessionManager.getInstance().getSessionToken());
    }

    public void showOnErrorLayout() {
        binding.resultTextSwitcher.setText(getText(R.string.debits_registration_ko_description));
        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
    }

}
