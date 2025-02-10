package it.bancomatpay.sdkui.activities.pos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityPosWithdrawalResultBinding;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsMerchantLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogMerchantLocation;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.POS_DAILY_THRESHOLD_REACHED;
import static it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager.IS_FROM_SERVICE_FRAGMENT;
import static it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager.WITHDRAWAL_PAYMENT_ITEM_EXTRA;

public class PosWithdrawalResultActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private MerchantQrPaymentData paymentData;
    private ShopsDataMerchant merchantItem;
    private ActivityPosWithdrawalResultBinding binding;
    private boolean isFromServiceFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosWithdrawalResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivityName(PosWithdrawalResultActivity.class.getSimpleName());

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

        binding.toolbarSimple.setCenterImageVisibility(true);

        paymentData = (MerchantQrPaymentData) getIntent().getSerializableExtra(WITHDRAWAL_PAYMENT_ITEM_EXTRA);
        isFromServiceFragment = getIntent().getBooleanExtra(IS_FROM_SERVICE_FRAGMENT, false);

        initTextSwitcher();
        initLayout(paymentData.getDisplayData());

        callConfirmPosWithdrawal(paymentData);

        if (isFromServiceFragment) {
            EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.SERVICES));
        }
        binding.buttonClose.setOnClickListener(new CustomOnClickListener(v -> BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                .goToHome(PosWithdrawalResultActivity.this, false, false, false)));
        binding.fabMerchantLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabBankLocation()));
    }


    private void initLayout(DisplayData displayData) {
        if (displayData != null && displayData.getItemInterface() instanceof ShopItem) {

            if (TextUtils.isEmpty(displayData.getTitle())) {
                binding.textMerchantName.setVisibility(View.GONE);
            } else {
                binding.textMerchantName.setText(displayData.getTitle());
            }
            if (TextUtils.isEmpty(displayData.getDescription())) {
                binding.textMerchantAddress.setVisibility(View.GONE);
            } else {
                binding.textMerchantAddress.setText(displayData.getDescription());
            }

            if (paymentData.getAmount() != null) {
                binding.layoutAmount.setVisibility(View.VISIBLE);
                binding.textAmount.setText(StringUtils.getFormattedValue(paymentData.getAmount()));

                BigDecimal fee = paymentData.getFee() != null ? paymentData.getFee() : new BigDecimal(0);
                binding.textFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(fee)));
                binding.textFee.setVisibility(View.VISIBLE);
            }

            String constraint = displayData.getTitle() + " " + displayData.getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, constraint);
        }
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

        binding.resultTextSwitcher.setText(getString(R.string.service_result_loading_label));

    }

    private void callConfirmPosWithdrawal(MerchantQrPaymentData paymentData) {

        String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();

        BCMOperationAuthorization bcmPayment = new BCMOperationAuthorization();
        bcmPayment.setOperation(AuthenticationOperationType.POS);
        bcmPayment.setAmount(String.valueOf(paymentData.getCentsAmount()));
        bcmPayment.setPaymentId(paymentData.getQrCodeId());
        bcmPayment.setReceiver(paymentData.getTag());
        bcmPayment.setSender(userMsisdn);

        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForWithdrawalOperation(this,
                        bcmPayment, (authenticated, authToken) -> {
                            if (authenticated) {
                                BancomatSdkInterface.Factory.getInstance().doConfirmPosWithdrawal(this, result -> {
                                    binding.layoutAmount.setVisibility(View.GONE);
                                    binding.buttonClose.setVisibility(View.VISIBLE);
                                    if (result != null) {
                                        if (result.isSuccess()) {
                                            showSuccessLayout();
                                        } else if (result.isSessionExpired()) {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                        } else if (result.getStatusCode().equals(POS_DAILY_THRESHOLD_REACHED)) {
                                            showErrorLayout(true);
                                        } else {
                                            showError(result.getStatusCode());
                                            showErrorLayout(false);
                                        }
                                    } else {
                                        showErrorLayout(false);
                                    }
                                }, bcmPayment.getReceiver(), paymentData.getPaymentItem().getShopId(), paymentData.getPaymentItem().getTillId(), String.valueOf(paymentData.getCentsAmount()), String.valueOf(paymentData.getCentsTotalAmount()), authToken, paymentData.getPaymentItem().getQrCodeId(), paymentData.getCausal(), SessionManager.getInstance().getSessionToken());
                            }
                        });
    }

    private void clickFabBankLocation() {
        if (merchantItem == null) {

            LoaderHelper.showLoader(this);

            BancomatSdkInterface.Factory.getInstance().doGetMerchantDetail(this, result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                merchantItem = new ShopsDataMerchant(result.getResult());
                                showBottomDialog(merchantItem);
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showError(result.getStatusCode());
                            }
                        }
                    },
                    paymentData.getTag(),
                    String.valueOf(paymentData.getDisplayData().getItemInterface().getId()),
                    SessionManager.getInstance().getSessionToken());

        } else {
            showBottomDialog(merchantItem);

        }
    }

    private void showBottomDialog(ShopsDataMerchant merchantItem) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            BottomDialogHmsMerchantLocation hmsBottomDialog = new BottomDialogHmsMerchantLocation(this, merchantItem);
            if (!hmsBottomDialog.isVisible()) {
                hmsBottomDialog.showDialog();
            }
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            BottomDialogMerchantLocation bottomDialog = new BottomDialogMerchantLocation(this, merchantItem);
            if (!bottomDialog.isVisible()) {
                bottomDialog.showDialog();
            }
        }
    }

    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        if (animate) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
            AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
        } else {
            binding.imageBackgroundMerchant.setVisibility(View.VISIBLE);
            binding.imageBackgroundMask.setVisibility(View.VISIBLE);
        }
    }


    private void showSuccessLayout() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultTextSwitcher.setText(getString(R.string.atm_cardless_result_operation_success));
        binding.resultAnimation.setAnimation(getString(R.string.check_ok));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        AnimationFadeUtil.startFadeInAnimationV1(binding.textSubdescription, AnimationFadeUtil.DEFAULT_DURATION);
        binding.resultAnimation.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonClose, AnimationFadeUtil.DEFAULT_DURATION);
    }

    private void showErrorLayout(boolean isThresholdReached) {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        if (isThresholdReached) {
            binding.resultTextSwitcher.setText(getText(R.string.withdraw_result_daily_limit_overcome));
        } else {
            binding.resultTextSwitcher.setText(getText(R.string.withdraw_result_operation_ko));
        }
        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        binding.resultAnimation.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonClose, AnimationFadeUtil.DEFAULT_DURATION);
    }
}