package it.bancomatpay.sdkui.activities.petrol;

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

import java.math.BigInteger;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.Till;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmPetrolResultBinding;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsPetrolLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogPetrolLocation;

import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_CENTS_AMOUNT;
import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_MERCHANT_DATA;
import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_PUMP_DATA_EXTRA;

public class PetrolResultActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmPetrolResultBinding binding;

    private ShopsDataMerchant merchantItem;
    private ShopItem shopItem;
    private Till till;
    private int centsAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(PetrolResultActivity.class.getSimpleName());
        binding = ActivityBcmPetrolResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shopItem = (ShopItem) getIntent().getSerializableExtra(PETROL_MERCHANT_DATA);
        till = (Till) getIntent().getSerializableExtra(PETROL_PUMP_DATA_EXTRA);
        centsAmount = getIntent().getIntExtra(PETROL_CENTS_AMOUNT, 0);

        initLayout(shopItem);
        initTextSwitcher();

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.fabPetrolLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabPetrolLocation()));
        binding.closeButton.setOnClickListener(new CustomOnClickListener(v ->
                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                        .goToHome(this, false, false, false)));

        callConfirmPetrolPayment();

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

        binding.resultTextSwitcher.setText(getString(R.string.service_result_loading_label));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, 200);

    }

    private void initLayout(ShopItem shopItem) {
        if (shopItem != null) {

            if (TextUtils.isEmpty(shopItem.getTitle())) {
                binding.textPetrolName.setVisibility(View.GONE);
            } else {
                binding.textPetrolName.setText(shopItem.getTitle());
            }
            if (TextUtils.isEmpty(shopItem.getDescription())) {
                binding.textPetrolAddress.setVisibility(View.GONE);
            } else {
                binding.textPetrolAddress.setText(shopItem.getDescription());
            }

            String constraint = shopItem.getTitle() + " " + shopItem.getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(
                    this, this, shopItem.getLatitude(), shopItem.getLongitude(), constraint);
        }
    }

    private void clickFabPetrolLocation() {
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
                    shopItem.getTag(),
                    String.valueOf(shopItem.getId()),
                    SessionManager.getInstance().getSessionToken());

        } else {
            showBottomDialog(merchantItem);
        }
    }


    private void showBottomDialog(ShopsDataMerchant merchantItem) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            BottomDialogHmsPetrolLocation hmsBottomDialog = new BottomDialogHmsPetrolLocation(this, merchantItem);
            if (!hmsBottomDialog.isVisible()) {
                hmsBottomDialog.showDialog();
            }
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            BottomDialogPetrolLocation bottomDialog = new BottomDialogPetrolLocation(this, merchantItem);
            if (!bottomDialog.isVisible()) {
                bottomDialog.showDialog();
            }
        }
    }

    private void callConfirmPetrolPayment() {

        String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();

        BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
        bcmPaymentAuthorization.setAmount(String.valueOf(centsAmount));
        bcmPaymentAuthorization.setOperation(AuthenticationOperationType.PAYMENT);
        bcmPaymentAuthorization.setReceiver(shopItem.getTag());
        bcmPaymentAuthorization.setSender(userMsisdn);

        BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                .authenticationNeededForDispositiveOperation(this,
                        bcmPaymentAuthorization, (authenticated, authToken, loyaltyToken, paymentId) -> {
                            if (authenticated) {

                                LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);

                                BancomatSdkInterface.Factory.getInstance()
                                        .doConfirmPetrolPayment(this, result -> {
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
                                                shopItem.getTag(),
                                                String.valueOf(shopItem.getShopId()),
                                                new BigInteger(till.getTillId()),
                                                String.valueOf(centsAmount),
                                                authToken,
                                                SessionManager.getInstance().getSessionToken());
                            } else {
                                showErrorLayout();
                            }
                        });
    }

    private void showSuccessLayout() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultTextSwitcher.setText(getString(R.string.petrol_result_operation_success, till.getName()));
        binding.textPetrolPreauthAmount.setText(
                getString(R.string.petrol_preauthorized_amount_label,
                        StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(centsAmount))));
        binding.resultAnimation.setAnimation(getString(R.string.check_ok));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        AnimationFadeUtil.startFadeInAnimationV1(binding.textPetrolPreauthAmount, AnimationFadeUtil.DEFAULT_DURATION);
        binding.resultAnimation.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION);
    }

    private void showErrorLayout() {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultTextSwitcher.setText(getText(R.string.petrol_result_operation_error));
        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        binding.resultAnimation.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.closeButton, AnimationFadeUtil.DEFAULT_DURATION);
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
