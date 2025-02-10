package it.bancomatpay.sdkui.activities.atmcardless;

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

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

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
import it.bancomatpay.sdkui.databinding.ActivityBcmAtmCardlessResultBinding;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.widgets.BottomDialogBankLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsBankLocation;

import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.ATM_PAYMENT_CENTS_AMOUNT;
import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.ATM_PAYMENT_ITEM_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.ATM_PAYMENT_SHOP_DATA;
import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.IS_FROM_SERVICE_FRAGMENT;

public class AtmCardlessResultActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private MerchantQrPaymentData paymentData;
    private ShopsDataMerchant merchantItem;
    private String centsAmount;

    private ActivityBcmAtmCardlessResultBinding binding;
    private boolean isFromServiceFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBcmAtmCardlessResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setActivityName(AtmCardlessResultActivity.class.getSimpleName());
        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

        initTextSwitcher();

        paymentData = (MerchantQrPaymentData) getIntent().getSerializableExtra(ATM_PAYMENT_ITEM_EXTRA);
        merchantItem = (ShopsDataMerchant) getIntent().getSerializableExtra(ATM_PAYMENT_SHOP_DATA);
        centsAmount = getIntent().getStringExtra(ATM_PAYMENT_CENTS_AMOUNT);
        isFromServiceFragment = getIntent().getBooleanExtra(IS_FROM_SERVICE_FRAGMENT, false);

        binding.toolbarSimple.setCenterImageVisibility(true);
        initLayout(paymentData.getDisplayData());

        callConfirmAtmWithdrawal(paymentData);

        if (isFromServiceFragment) {
            EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.SERVICES));
        }
        binding.buttonClose.setOnClickListener(new CustomOnClickListener(v -> BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                .goToHome(AtmCardlessResultActivity.this, false, false, false)));

        binding.fabBankLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabBankLocation()));

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

        binding.resultTextSwitcher.setText(getString(R.string.atm_cardless_result_loading_label));

    }


    private void initLayout(DisplayData displayData) {
        if (displayData != null && displayData.getItemInterface() instanceof ShopItem) {

            if (TextUtils.isEmpty(displayData.getTitle())) {
                binding.textBankName.setVisibility(View.GONE);
            } else {
                binding.textBankName.setText(displayData.getTitle());
            }
            if (TextUtils.isEmpty(displayData.getDescription())) {
                binding.textBankAddress.setVisibility(View.GONE);
            } else {
                binding.textBankAddress.setText(displayData.getDescription());
            }

            String constraint = displayData.getTitle() + " " + displayData.getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, constraint);
        }
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
            BottomDialogHmsBankLocation hmsBottomDialog = new BottomDialogHmsBankLocation(this, merchantItem);
            if (!hmsBottomDialog.isVisible()) {
                hmsBottomDialog.showDialog();
            }
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            BottomDialogBankLocation bottomDialog = new BottomDialogBankLocation(this, merchantItem);
            if (!bottomDialog.isVisible()) {
                bottomDialog.showDialog();
            }
        }
    }

    private void callConfirmAtmWithdrawal(MerchantQrPaymentData paymentData) {

        String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();

        BCMOperationAuthorization bcmPayment = new BCMOperationAuthorization();
        bcmPayment.setOperation(AuthenticationOperationType.ATM);
        bcmPayment.setAmount(centsAmount);
        bcmPayment.setPaymentId(paymentData.getQrCodeId());
        bcmPayment.setReceiver(paymentData.getTag());
        bcmPayment.setSender(userMsisdn);

        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForWithdrawalOperation(this,
                        bcmPayment, (authenticated, authToken) -> {
                            if (authenticated) {
                                BancomatSdkInterface.Factory.getInstance().doConfirmAtmWithdrawal(this, result -> {
                                            if (result != null) {
                                                if (result.isSuccess()) {
                                                    showSuccessLayout();
                                                } else if (result.isSessionExpired()) {
                                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                                } else {
                                                    showError(result.getStatusCode());
                                                    showErrorLayout();
                                                }
                                            } else {
                                                showErrorLayout();
                                            }
                                        },
                                        bcmPayment.getReceiver(),
                                        paymentData.getTag(),
                                        paymentData.getShopId(),
                                        centsAmount,
                                        authToken,
                                        paymentData.getQrCodeId(),
                                        SessionManager.getInstance().getSessionToken());
                            } else {
                                showErrorLayout();
                            }
                        });
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

    private void showErrorLayout() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultTextSwitcher.setText(getText(R.string.atm_cardless_result_operation_error));
        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        binding.resultAnimation.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.buttonClose, AnimationFadeUtil.DEFAULT_DURATION);
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

}
