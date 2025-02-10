package it.bancomatpay.sdkui.activities.pos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.math.BigDecimal;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmPosWithdrawalDataLoadingBinding;
import it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsMerchantLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogMerchantLocation;

import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.QR_DATA;

public class PosWithdrawalDataLoadingActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmPosWithdrawalDataLoadingBinding binding;
    private ShopsDataMerchant merchantItem;
    private AbstractPaymentData paymentData;
    private QrCodeDetailsData detailsData;
    private boolean isFromServiceFragment = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityName(PosWithdrawalDataLoadingActivity.class.getSimpleName());

        binding = ActivityBcmPosWithdrawalDataLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isFromServiceFragment = getIntent().getBooleanExtra(PosWithdrawalFLowManager.IS_FROM_SERVICE_FRAGMENT, false);
        detailsData = (QrCodeDetailsData) getIntent().getSerializableExtra(QR_DATA);

        manageToolbarComponent();

        binding.loaderPaymentQr.setVisibility(View.INVISIBLE);

        if (detailsData != null) {
            initLayout(detailsData);
        }

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v ->
                clickConfirm()));

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    private void manageToolbarComponent() {
        binding.toolbarSimple.setRightImageVisibility(true);
        binding.toolbarSimple.setRightImage(R.drawable.info_ico_white);
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToAtmCardless(this));
        } else {
            binding.toolbarSimple.setRightCenterImageVisibility(false);
        }
        binding.toolbarSimple.setLeftImageVisibility(true);
        binding.toolbarSimple.setOnClickLeftImageListener(new CustomOnClickListener(v -> {
            if (isFromServiceFragment) {
                onBackPressed();
            } else {
                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                        .goToHome(this, false, false, false);
            }

        }));
    }


    private void initLayout(QrCodeDetailsData detailsData) {
        String address = detailsData.getPaymentItem().getAddress().getStreet()
                + ", "
                + detailsData.getPaymentItem().getAddress().getCity();
        binding.textMerchantName.setText(detailsData.getPaymentItem().getShopName());
        binding.textMerchantAddress.setText(address);
        binding.recoveryPaymentDataLabel.setVisibility(View.INVISIBLE);

        if (detailsData.getPaymentItem() != null) {
            paymentData = new MerchantQrPaymentData(detailsData.getPaymentItem(), true);

            binding.textAmount.setText(StringUtils.getFormattedValue(paymentData.getAmount()));
            AnimationFadeUtil.startFadeInAnimationV1(binding.layoutPaymentInfo, AnimationFadeUtil.DEFAULT_DURATION);

        } else {
            showErrorAndDoAction(StatusCode.Mobile.GENERIC_ERROR, (dialog, which) -> finish());
        }
        BigDecimal fee = paymentData.getFee() != null ? paymentData.getFee() : new BigDecimal(0);
        binding.feeLabel.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(fee)));
        binding.feeLabel.setVisibility(View.VISIBLE);
        if (detailsData.getPaymentItem().getLatitude() != null && detailsData.getPaymentItem().getLongitude() != null) {
            binding.fabMerchantLocation.setVisibility(View.VISIBLE);
            binding.fabMerchantLocation.setOnClickListener(new CustomOnClickListener(c -> clickFabMerchantLocation()));
        }
        binding.paymentDescription.setText(getString(R.string.withdraw_recap_label));

        String constraint =
                detailsData.getPaymentItem().getShopName()
                        + detailsData.getPaymentItem().getAddress().getStreet()
                        + ", "
                        + detailsData.getPaymentItem().getAddress().getCity();
        PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, constraint);
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

    private void clickConfirm() {
        if (paymentData != null) {
            MerchantQrPaymentData merchantQrPaymentData = new MerchantQrPaymentData(detailsData.getPaymentItem(), true);
            PosWithdrawalFLowManager.goToPosWithdrawalResult(this, merchantQrPaymentData, isFromServiceFragment);
        } else {
            showErrorAndDoAction(StatusCode.Mobile.GENERIC_ERROR, (dialog, which) -> finish());
        }
    }


    private void clickFabMerchantLocation() {
        if (merchantItem == null) {

            LoaderHelper.showLoader(this);

            String merchantTag = "";

            if (paymentData instanceof MerchantPaymentData) {
                merchantTag = ((MerchantPaymentData) paymentData).getTag();
            }

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
                    merchantTag,
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

}

