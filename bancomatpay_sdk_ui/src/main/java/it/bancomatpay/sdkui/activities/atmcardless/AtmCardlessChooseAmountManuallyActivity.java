package it.bancomatpay.sdkui.activities.atmcardless;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmAtmCardlessChooseAmountManuallyBinding;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;
import it.bancomatpay.sdkui.widgets.BottomDialogBankLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsBankLocation;
import it.bancomatpay.sdkui.widgets.LabelPaymentAmount;
import it.bancomatpay.sdkui.R;


import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.ATM_PAYMENT_ITEM_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.ATM_PAYMENT_SHOP_DATA;
import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.IS_FROM_SERVICE_FRAGMENT;

public class AtmCardlessChooseAmountManuallyActivity extends GenericErrorActivity
        implements LabelPaymentAmount.LabelListener, PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmAtmCardlessChooseAmountManuallyBinding binding;

    private int money;
    private MerchantQrPaymentData paymentData;
    private ShopsDataMerchant merchantItem;
    private boolean isFromServiceFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(AtmCardlessChooseAmountManuallyActivity.class.getSimpleName());
        binding = ActivityBcmAtmCardlessChooseAmountManuallyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        paymentData = (MerchantQrPaymentData) getIntent().getSerializableExtra(ATM_PAYMENT_ITEM_EXTRA);
        merchantItem = (ShopsDataMerchant) getIntent().getSerializableExtra(ATM_PAYMENT_SHOP_DATA);
        isFromServiceFragment = getIntent().getBooleanExtra(IS_FROM_SERVICE_FRAGMENT, false);

        initLayout(paymentData.getDisplayData());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToAtmCardless(this));
        } else {
            binding.toolbarSimple.setRightCenterImageVisibility(false);
        }

        binding.atmAmountKeyboard.setKeyboardListener(binding.atmAmountLabel);
        binding.atmAmountKeyboard.setKeyboardType(KeyboardType.ATM_AMOUNT_KEYBOARD);
        binding.atmAmountLabel.setLabelListener(this);

        binding.atmAmountKeyboard.getButtonContinueOn().setOnClickListener(new CustomOnClickListener(v ->
                AtmCardlessFlowManager.goToAtmCardlessResult(this, paymentData, merchantItem, String.valueOf(this.money), isFromServiceFragment)));

        binding.fabBankLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabBankLocation()));
        binding.cancelButtonInsertNumber.setOnClickListener(new CustomOnClickListener(v -> binding.atmAmountLabel.onDeleteCharacter()));
        binding.cancelButtonInsertNumber.setOnLongClickListener(v -> {
            binding.atmAmountLabel.onDeleteAllText();
            return true;
        });
        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
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
            if (hmsBottomDialog.isVisible()) {
                hmsBottomDialog.showDialog();
            }
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            BottomDialogBankLocation bottomDialog = new BottomDialogBankLocation(this, merchantItem);
            if (!bottomDialog.isVisible()) {
                bottomDialog.showDialog();
            }
        }
    }

    @Override
    public void onMoneyInserted(int money, boolean isDeletingCharacter) {
        this.money = money;
        if (money >= 2000 && money % 1000 == 0) {
            binding.atmAmountKeyboard.getButtonContinueOn().setVisibility(View.VISIBLE);
            binding.atmAmountKeyboard.getButtonContinueOff().setVisibility(View.INVISIBLE);
        } else {
            binding.atmAmountKeyboard.getButtonContinueOn().setVisibility(View.INVISIBLE);
            binding.atmAmountKeyboard.getButtonContinueOff().setVisibility(View.VISIBLE);
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

}
