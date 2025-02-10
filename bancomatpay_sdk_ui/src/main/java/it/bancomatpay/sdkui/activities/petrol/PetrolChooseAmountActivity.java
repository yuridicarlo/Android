package it.bancomatpay.sdkui.activities.petrol;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.Till;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.PetrolAmountsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmPetrolChooseAmountBinding;
import it.bancomatpay.sdkui.flowmanager.PetrolFlowManager;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsPetrolLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogPetrolLocation;

import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_MERCHANT_DATA;
import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_PUMP_DATA_EXTRA;

public class PetrolChooseAmountActivity extends GenericErrorActivity
        implements PetrolAmountsAdapter.PetrolAmountInteractionListener, PlacesClientUtil.MerchantImageLoadingListener {

    //private Animation animRotationStep1;
    //private Animation animRotationStep2;

    private ActivityBcmPetrolChooseAmountBinding binding;

    private ShopsDataMerchant merchantItem;
    private ShopItem shopItem;
    private Till pumpData;

    private PetrolAmountsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(PetrolChooseAmountActivity.class.getSimpleName());
        binding = ActivityBcmPetrolChooseAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shopItem = (ShopItem) getIntent().getSerializableExtra(PETROL_MERCHANT_DATA);
        pumpData = (Till) getIntent().getSerializableExtra(PETROL_PUMP_DATA_EXTRA);

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToPetrol(this));
        } else {
            binding.toolbarSimple.setRightImageVisibility(false);
        }

        initLayout(shopItem);
        initTextSwitcher();

        List<Integer> items = new ArrayList<>();
        items.add(10000);
        items.add(5000);
        items.add(2000);
        items.add(1000);
        items.add(500);

        adapter = new PetrolAmountsAdapter(items, this, items.get(0));
        binding.recyclerViewAmount.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAmount.setAdapter(adapter);

        //animRotationStep1 = AnimationUtils.loadAnimation(this, R.anim.rotation_half_step_1);
        //animRotationStep2 = AnimationUtils.loadAnimation(this, R.anim.rotation_half_step_2);

        binding.layoutOpenSpinner.setOnClickListener(new CustomOnClickListener(v -> clickDrawerChooseAmount()));
        binding.fabPetrolLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabPetrolLocation()));
        binding.buttonNext.setOnClickListener(new CustomOnClickListener(v -> {
            int centsAmount = adapter.getSelectedItemAmount();
            PetrolFlowManager.goToPetrolResult(this, shopItem, pumpData, centsAmount);
        }));
        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.petrol_exit_warning_message)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    if (binding.expandableLayout.isExpanded()) {
                        binding.expandableLayout.collapse();
                    } else {
                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                .goToHome(this, false, false, false);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false);
        builder.show();
    }

    private void initLayout(ShopItem shopItem) {
        if (shopItem != null) {
            binding.textPetrolName.setText(shopItem.getTitle());
            binding.textPetrolAddress.setText(shopItem.getDescription());
        }
    }

    private void initTextSwitcher() {

        binding.textSwitcherSelectedAmount.setFactory(() -> new TextView(
                new ContextThemeWrapper(this, R.style.TextPetrolSelectedAmount), null, 0));

        Animation inAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        inAnim.setDuration(200);
        outAnim.setDuration(200);

        binding.textSwitcherSelectedAmount.setInAnimation(inAnim);
        binding.textSwitcherSelectedAmount.setOutAnimation(outAnim);

        binding.textSwitcherSelectedAmount.setText("Max 100 €");

    }

    private void clickDrawerChooseAmount() {
        if (binding.expandableLayout.isExpanded()) {
            binding.expandableLayout.collapse();
        } else {
            binding.expandableLayout.expand();
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


    @Override
    public void onAmountSelected(int amount) {
        String formattedAmount = StringUtils.getFormattedValueInteger(BigDecimalUtils.getBigDecimalFromCents(amount));
        if (amount == 10000) {
            binding.textSwitcherSelectedAmount.setText(String.format("Max %s", formattedAmount));
        } else {
            binding.textSwitcherSelectedAmount.setText(formattedAmount);
        }
        binding.expandableLayout.collapse();
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
