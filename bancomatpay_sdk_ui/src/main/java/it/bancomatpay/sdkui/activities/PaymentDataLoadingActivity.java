package it.bancomatpay.sdkui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.databinding.ActivityBcmPaymentDataLoadingBinding;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2B_POS_WITHDRAWAL_NOT_ACTIVATED_BY_BANK;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.QR_DATA;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.QR_DATA_ID;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_QR_CODE_SCAN;

public class PaymentDataLoadingActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmPaymentDataLoadingBinding binding;

    private AbstractPaymentData paymentData;

    private boolean isFromDeepLink = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(PaymentDataLoadingActivity.class.getSimpleName());
        binding = ActivityBcmPaymentDataLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isFromDeepLink = getIntent().getBooleanExtra(PaymentFlowManager.PAYMENT_FROM_DEEP_LINK, false);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

        QrCodeDetailsData detailsData = (QrCodeDetailsData) getIntent().getSerializableExtra(QR_DATA);

        String qrDataId = getIntent().getStringExtra(QR_DATA_ID);

        if (detailsData != null) {
            initLayout(detailsData);
        }else {
            getQrCodeDetails(qrDataId);
        }

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }


    private void getQrCodeDetails(String qrDataId) {
        binding.confirmButton.setEnabled(false);
        binding.textAmount.setText(". . .");
        binding.textLocalAmountAndCurrency.setText("");

        BancomatSdkInterface.Factory.getInstance().doGetQrCodeDetails(this, result -> {

            if (result != null) {
                if (result.isSuccess()) {

                    CjUtils.getInstance().startP2BPaymentFlow();
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                            this, KEY_QR_CODE_SCAN, null, true);

                    QrCodeDetailsData detailsData = result.getResult();

                    if(BancomatFullStackSdk.getInstance().isQrCodeStatic(qrDataId)){

                        if (detailsData.getShopItem() != null) {
                            PaymentFlowManager.goToInsertAmount(this,
                                    new ShopsDataMerchant(detailsData.getShopItem(), detailsData.getPaymentItem()), false, PaymentContactFlowType.SEND, false);
                            finish();
                        } else if (detailsData.getPaymentItem() != null) {
                            if (result.getResult().getPaymentItem().getCategory() != null && result.getResult().getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.atmWithdrawal)) {
                                AtmCardlessFlowManager.goToChooseAmount(this,
                                        new MerchantQrPaymentData(detailsData.getPaymentItem(), false), false);
                                finish();
                            } else {
                                PaymentFlowManager.goToConfirm(this,
                                        new MerchantQrPaymentData(detailsData.getPaymentItem(), false), true, false);
                                finish();
                            }
                        } else {
                            showErrorAndDoAction(StatusCode.Mobile.GENERIC_ERROR, (dialog, which) -> finish());
                        }


                    }else {

                        if (detailsData.getPaymentItem() != null && detailsData.getPaymentItem().getCategory() != null) {
                            if (detailsData.getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.atmWithdrawal)) {
                                AtmCardlessFlowManager.goToChooseAmount(this,
                                        new MerchantQrPaymentData(detailsData.getPaymentItem(), true), false);
                                finish();
                            } else if (result.getResult().getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.posWithdrawal)) {
                                PosWithdrawalFLowManager.goToPosWithdrawalPaymentData(this, detailsData, false);
                                finish();
                            } else {
                                initLayout(detailsData);
                            }
                        } else {
                            initLayout(detailsData);
                        }
                    }

                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> finish());
                }
            } else {
                showErrorAndDoAction(StatusCode.Mobile.GENERIC_ERROR, (dialog, which) -> finish());
            }
        }, qrDataId, SessionManager.getInstance().getSessionToken());
    }


    private void initLayout(QrCodeDetailsData detailsData) {
        binding.confirmButton.setEnabled(true);
        binding.recoveryPaymentDataLabel.setVisibility(View.INVISIBLE);
        binding.loaderPaymentQr.setVisibility(View.INVISIBLE);

        if (detailsData.getPaymentItem() != null) {
            binding.textMerchantName.setText(detailsData.getPaymentItem().getShopName());

            paymentData = new MerchantQrPaymentData(detailsData.getPaymentItem(), true);

            if (detailsData.getPaymentItem().getAddress() != null) {
                String address = detailsData.getPaymentItem().getAddress().getStreet()
                        + ", "
                        + detailsData.getPaymentItem().getAddress().getCity();
                binding.textMerchantAddress.setText(address);

                PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, address);
            }

            binding.textAmount.setText(StringUtils.getFormattedValue(paymentData.getAmount()));

            if (paymentData.isQrCodeEmpsa() && (paymentData.getLocalAmount() != null)) {
                binding.textLocalAmountAndCurrency.setText("(" + StringUtils.getFormattedValue(paymentData.getLocalAmount(), paymentData.getLocalCurrency()) + ")");
            }
            else {
                binding.textLocalAmountAndCurrency.setVisibility(View.GONE);
            }
        } else {
            showErrorAndDoAction(StatusCode.Mobile.GENERIC_ERROR, (dialog, which) -> finish());
        }

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> {
            if (paymentData != null) {
                PaymentFlowManager.goToResultPayment(this, paymentData, "", true, isFromDeepLink, false);
            } else {
                showErrorAndDoAction(StatusCode.Mobile.GENERIC_ERROR, (dialog, which) -> finish());
            }
        }));
    }

    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
    }

}
