package it.bancomatpay.sdkui.activities.petrol;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.Till;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.PumpsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmPetrolChoosePumpBinding;
import it.bancomatpay.sdkui.flowmanager.PetrolFlowManager;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsPetrolLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogPetrolLocation;
import it.bancomatpay.sdkui.R;


import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_MERCHANT_DATA;

public class PetrolChoosePumpActivity extends GenericErrorActivity
        implements PumpsAdapter.PumpInteractionListener, PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmPetrolChoosePumpBinding binding;

    private PumpsAdapter adapter;
    private ShopItem shopItem;
    private ShopsDataMerchant merchantItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(PetrolChoosePumpActivity.class.getSimpleName());
        binding = ActivityBcmPetrolChoosePumpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToPetrol(this));
        } else {
            binding.toolbarSimple.setRightImageVisibility(false);
        }

        shopItem = (ShopItem) getIntent().getSerializableExtra(PETROL_MERCHANT_DATA);
        initLayout(shopItem);
        List<Till> pumpsList = shopItem.getTillList();

        adapter = new PumpsAdapter(this, pumpsList, this);

        binding.recyclerPumps.setLayoutManager(new LinearLayoutManager(this, HORIZONTAL, false));
        binding.recyclerPumps.setAdapter(adapter);
        if (binding.recyclerPumps.getItemAnimator() != null) {
            ((SimpleItemAnimator) binding.recyclerPumps.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        binding.buttonNext.setOnClickListener(new CustomOnClickListener(v -> {
            Till selectedPump = adapter.getSelectedPump();
            PetrolFlowManager.goToChooseAmount(this, shopItem, selectedPump);
        }));
        binding.fabPetrolLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabPetrolLocation()));

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void initLayout(ShopItem shopItem) {
        if (shopItem != null) {
            binding.textPetrolName.setText(shopItem.getTitle());
            binding.textPetrolAddress.setText(shopItem.getDescription());

            String constraint = shopItem.getTitle() + " " + shopItem.getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this,
                    shopItem.getLatitude(), shopItem.getLongitude(), constraint);
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
    public void onPumpSelected(Till pumpData) {
        adapter.setSelection(pumpData);
        if (!binding.buttonNext.isEnabled()) {
            binding.buttonNext.setEnabled(true);
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
