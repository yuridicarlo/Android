package it.bancomatpay.sdkui.activities.atmcardless;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.AtmCardlessAmountsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmAtmCardlessChooseAmountBinding;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;
import it.bancomatpay.sdkui.widgets.BottomDialogBankLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsBankLocation;

import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.ATM_PAYMENT_ITEM_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager.IS_FROM_SERVICE_FRAGMENT;

public class AtmCardlessChooseAmountActivity extends GenericErrorActivity
        implements AtmCardlessAmountsAdapter.AtmAmountInteractionListener, PlacesClientUtil.MerchantImageLoadingListener {

    private Animation animRotationStep1;
    private Animation animRotationStep2;

    private MerchantQrPaymentData paymentData;
    private ShopsDataMerchant merchantItem;
    private AtmCardlessAmountsAdapter adapter;

    private ActivityBcmAtmCardlessChooseAmountBinding binding;
    private boolean isFromServiceFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBcmAtmCardlessChooseAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setActivityName(AtmCardlessChooseAmountActivity.class.getSimpleName());
        paymentData = (MerchantQrPaymentData) getIntent().getSerializableExtra(ATM_PAYMENT_ITEM_EXTRA);
        isFromServiceFragment = getIntent().getBooleanExtra(IS_FROM_SERVICE_FRAGMENT, false);

        initLayout(paymentData.getDisplayData());

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToAtmCardless(this));
        } else {
            binding.toolbarSimple.setRightCenterImageVisibility(false);
        }

        binding.layoutOpenSpinner.setOnClickListener(new CustomOnClickListener(v -> clickDrawerChooseAmount()));
        binding.fabBankLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabBankLocation()));
        binding.buttonNext.setOnClickListener(new CustomOnClickListener(v -> {
            String centsAmount = String.valueOf(adapter.getSelectedItemAmount());
            AtmCardlessFlowManager.goToAtmCardlessResult(AtmCardlessChooseAmountActivity.this, paymentData, merchantItem, centsAmount, isFromServiceFragment);
        }));

        initTextSwitcher();

        List<Integer> items = new ArrayList<>();
        items.add(5000);
        items.add(10000);
        items.add(15000);
        items.add(20000);
        items.add(25000);

        //Altro importo
        items.add(0);

        adapter = new AtmCardlessAmountsAdapter(items, items.get(0), this);
        binding.recyclerViewAmount.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAmount.setAdapter(adapter);

        animRotationStep1 = AnimationUtils.loadAnimation(this, R.anim.rotation_half_step_1);
        animRotationStep2 = AnimationUtils.loadAnimation(this, R.anim.rotation_half_step_2);

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    @Override
    public void onBackPressed() {
        if (binding.expandableLayout.isExpanded()) {
            binding.expandableLayout.collapse();
        } else {
            super.onBackPressed();
        }
    }


    private void initTextSwitcher() {

        binding.textSwitcherSelectedAmount.setFactory(() -> new TextView(
                new ContextThemeWrapper(this, R.style.TextAtmSelectedAmount), null, 0));

        Animation inAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        inAnim.setDuration(200);
        outAnim.setDuration(200);

        binding.textSwitcherSelectedAmount.setInAnimation(inAnim);
        binding.textSwitcherSelectedAmount.setOutAnimation(outAnim);

        binding.textSwitcherSelectedAmount.setText("50,00 €");

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

    private void clickDrawerChooseAmount() {
        if (binding.expandableLayout.isExpanded()) {
            binding.expandableLayout.collapse();
            binding.imageArrowDown.startAnimation(animRotationStep2);
        } else {
            binding.expandableLayout.expand();
            binding.imageArrowDown.startAnimation(animRotationStep1);
        }
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
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
    public void onAmountSelected(int amount) {
        binding.textSwitcherSelectedAmount.setText(StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(amount)));
        binding.expandableLayout.collapse();
        binding.imageArrowDown.startAnimation(animRotationStep2);
    }

    @Override
    public void onOtherAmountSelected() {
        binding.expandableLayout.collapse();
        binding.imageArrowDown.startAnimation(animRotationStep2);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> AtmCardlessFlowManager.goToChooseAmountManually(this, paymentData, merchantItem, isFromServiceFragment), 300);
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
