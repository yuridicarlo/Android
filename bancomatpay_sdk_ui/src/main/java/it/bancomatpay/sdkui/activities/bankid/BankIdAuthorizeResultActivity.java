package it.bancomatpay.sdkui.activities.bankid;

import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmBankIdAuthorizeResultBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.SDK_BANKID_REQUEST_NOT_VALID;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.AUTHORIZATION_TOKEN;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.IS_DENY_REQUEST;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.PROVIDER_MERCHANT_DATA;
import static it.bancomatpay.sdkui.flowmanager.BankIdFlowManager.REQUEST_ID;

public class BankIdAuthorizeResultActivity extends GenericErrorActivity {

    private static final String TAG = BankIdAuthorizeResultActivity.class.getSimpleName();

    private boolean isDenyBankIdRequest;
    private String requestId;
    private BankIdMerchantData merchantData;

    private ActivityBcmBankIdAuthorizeResultBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBcmBankIdAuthorizeResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivityName(BankIdAuthorizeResultActivity.class.getSimpleName());

        isDenyBankIdRequest = getIntent().getBooleanExtra(IS_DENY_REQUEST, false);
        requestId = getIntent().getStringExtra(REQUEST_ID);
        merchantData = (BankIdMerchantData) getIntent().getSerializableExtra(PROVIDER_MERCHANT_DATA);
        binding.textProviderName.setText(merchantData.getMerchantName());
        binding.textAccessLabel.setText(getString(R.string.bank_id_access_to_label, merchantData.getMerchantName()));
        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setOnClickRightImageListener(v -> {
            if (isDenyBankIdRequest) {
                LoaderHelper.showLoader(this);
                BancomatSdkInterface.Factory.getInstance().doDenyBankIdRequest(this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {

                                    CustomLogger.d(TAG, "doDenyBankIdRequest success!");
                                    BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                            .goToHome(this, false, false, false);
                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else {
                                    showError(result.getStatusCode());
                                    new Handler().postDelayed(() -> BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                            .goToHome(this, false, false, false), 1500);
                                }
                            }
                        },
                        requestId,
                        false,
                        SessionManager.getInstance().getSessionToken());
            } else {
                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                        .goToHome(this, false, false, false);
            }
        });
        initTextSwitcher();
        initLayout(isDenyBankIdRequest);

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

            binding.resultLoader.playAnimation();
            binding.toolbarSimple.setRightImageVisibility(false);

            BancomatSdkInterface.Factory.getInstance().doConfirmBankIdRequest(this, result -> {
                        binding.toolbarSimple.setRightImageVisibility(true);
                        if (result != null) {
                            if (result.isSuccess()) {
                                binding.resultTextSwitcher.setText(getString(R.string.authorization_permitted_description, merchantData.getMerchantName()));
                                binding.resultAnimation.setAnimation(getString(R.string.check_ok));
                                AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
                                binding.resultAnimation.playAnimation();

                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(BankIdAuthorizeResultActivity.this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else if (result.getStatusCode() == SDK_BANKID_REQUEST_NOT_VALID) {
                                showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                        .goToHome(BankIdAuthorizeResultActivity.this, false, false, false));
                            } else {
                                showErrorLayout();
                            }
                        } else {
                            showErrorLayout();
                        }
                    },
                    requestId,
                    merchantData.getMerchantTag(),
                    getIntent().getStringExtra(AUTHORIZATION_TOKEN),
                    SessionManager.getInstance().getSessionToken());

        } else {

            binding.resultTextSwitcher.setText(getText(R.string.authorization_denied_description));
            binding.resultAnimation.setAnimation(getString(R.string.check_ko));
            AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);

            binding.blockButton.setVisibility(View.VISIBLE);
            binding.blockButton.setOnClickListener(new CustomOnClickListener(v -> {

                LoaderHelper.showLoader(BankIdAuthorizeResultActivity.this);
                BancomatSdkInterface.Factory.getInstance().doDenyBankIdRequest(
                        BankIdAuthorizeResultActivity.this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    showBlockedMerchantLayout();
                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(BankIdAuthorizeResultActivity.this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else if (result.getStatusCode() == SDK_BANKID_REQUEST_NOT_VALID) {
                                    showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                            .goToHome(BankIdAuthorizeResultActivity.this, false, false, false));
                                } else {
                                    showError(result.getStatusCode());
                                }
                            }
                        },
                        requestId,
                        true,
                        SessionManager.getInstance().getSessionToken());
            }));

        }
    }

    private void showBlockedMerchantLayout() {
        binding.blockedMerchantText.setText(getString(R.string.blocked_merchant_description, merchantData.getMerchantName()));
        AnimationFadeUtil.startFadeOutAnimationV1(binding.blockButton, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE,
                () -> AnimationFadeUtil.startFadeInAnimationV1(binding.blockedMerchantText, AnimationFadeUtil.DEFAULT_DURATION));
        binding.resultAnimation.setAnimation(getString(R.string.esito_esercente_locked));
        binding.resultAnimation.playAnimation();
        binding.resultTextSwitcher.setText(getString(R.string.blocked_merchant_label));
        binding.toolbarSimple.setOnClickRightImageListener(v -> BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                .goToHome(BankIdAuthorizeResultActivity.this, false, false, false));
    }

    private void showErrorLayout() {
        binding.resultTextSwitcher.setText(getText(R.string.authorization_denied_description));
        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
    }

}
