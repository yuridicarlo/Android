package it.bancomatpay.sdkui.activities.loyaltycard;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.ActivityBcmCaptureLoyaltyCardBinding;
import it.bancomatpay.sdkui.events.ReturnLoyaltyCardListEvent;
import it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.LOYALTY_CARD_ALREADY_REGISTERED;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.IS_MODIFY_LOYALTY_CARD;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_BRAND_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_BARCODE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_BARCODE_TYPE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_UPDATED_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_LOYALTY_CARD_ADDED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_CARD_TYPE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_MODE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class CaptureLoyaltyCardActivity extends GenericErrorActivity implements BarcodeCallback {

    private static final String TAG = CaptureLoyaltyCardActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_CAMERA = 250;

    private CaptureManager capture;
    private DecoratedBarcodeView qrView;

    private LoyaltyBrand loyaltyBrand;
    private boolean isModifyLoyaltyCard;
    private ActivityBcmCaptureLoyaltyCardBinding binding;


    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(CaptureLoyaltyCardActivity.class.getSimpleName());
        binding = ActivityBcmCaptureLoyaltyCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        qrView = initializeContent();

        isModifyLoyaltyCard = getIntent().getBooleanExtra(IS_MODIFY_LOYALTY_CARD, false);

        if (isModifyLoyaltyCard) {
            binding.buttonAddManually.setVisibility(View.INVISIBLE);
        }

        loyaltyBrand = (LoyaltyBrand) getIntent().getSerializableExtra(LOYALTY_BRAND_EXTRA);
        if (loyaltyBrand != null) {
            if (!TextUtils.isEmpty(loyaltyBrand.getBrandName())) {
                binding.textTitle.setText(loyaltyBrand.getBrandName());
            } else {
                binding.textTitle.setText(getString(R.string.new_card_label));
            }
        } else if (isModifyLoyaltyCard) {
            binding.textTitle.setText(getString(R.string.card_bar_code_label));
            binding.textSubtitle.setVisibility(View.GONE);
        }

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        if (!hasPermissionCamera()) {
            requestPermissionCamera();
        } else {
            capture = new CaptureManager(this, qrView);
            Intent intent = getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
            intent.setAction(Intents.Scan.ACTION);
            capture.initializeFromIntent(intent, savedInstanceState);
            capture.decode();
        }

        binding.buttonAddManually.setOnClickListener(new CustomOnClickListener(v -> clickAddCardManually()));
        setLightStatusBar(binding.mainLayout, R.color.white_background);
    }


    protected DecoratedBarcodeView initializeContent() {
        qrView = findViewById(R.id.merchant_zxing_barcode_scanner);
        qrView.setStatusText("");
        qrView.getViewFinder().setVisibility(View.GONE);
        return qrView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_PERMISSION, "Camera");
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
        }
        if (capture != null) {
            capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return qrView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        if (capture != null) {
            capture.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (capture != null) {
            capture.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (capture != null) {
            capture.onResume();
            qrView.decodeSingle(this);
        }

    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        switch (result.getBarcodeFormat()) {
            case CODE_39:
            case CODE_128:
            case EAN_8:
            case EAN_13:
            case UPC_A:
                if (!isModifyLoyaltyCard) {
                    LoyaltyCard cardItem = new LoyaltyCard();
                    cardItem.setBarCodeType(result.getBarcodeFormat().toString());
                    cardItem.setBarCodeNumber(result.getText().replaceAll("\\s", ""));
                    if (loyaltyBrand != null) {
                        cardItem.setBrand(loyaltyBrand);
                        doSetLoyaltyCard(cardItem);
                    } else {
                        LoyaltyBrand brandUnknown = new LoyaltyBrand();
                        brandUnknown.setCardType(DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND);
                        cardItem.setBrand(brandUnknown);
                        LoyaltyCardFlowManager.goToAddCardUnknownBrand(this, cardItem);
                    }
                } else {
                    Intent intentResult = new Intent();
                    intentResult.putExtra(LOYALTY_CARD_BARCODE_EXTRA, result.getText());
                    intentResult.putExtra(LOYALTY_CARD_BARCODE_TYPE_EXTRA, result.getBarcodeFormat().toString());
                    setResult(RESULT_OK, intentResult);
                    finish();
                }
                break;
        }
    }

    private void doSetLoyaltyCard(LoyaltyCard loyaltyCard) {

        LoaderHelper.showLoader(this);

        DtoBrand.LoyaltyCardTypeEnum cardType;
        String hexColor;
        String brandLogoImage;
        if (loyaltyCard.getBrand() != null) {
            cardType = DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND;
            hexColor = String.format("#%06X", (0xFFFFFF & loyaltyCard.getBrand().getCardColor()));
            brandLogoImage = loyaltyCard.getBrand().getCardLogoUrl();
        } else {
            cardType = DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND;
            hexColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(this, R.color.colorAccentBancomat)));
            brandLogoImage = null;
        }

        BancomatSdkInterface.Factory.getInstance().doSetLoyaltyCard(this, result -> {
                    LoaderHelper.dismissLoader();
                    if (result != null) {
                        if (result.isSuccess()) {
                            if (!TextUtils.isEmpty(result.getResult())) {
                                String loyaltyCardId = result.getResult();
                                loyaltyCard.setLoyaltyCardId(loyaltyCardId);
                            }

                            HashMap<String, String> mapEventParams = new HashMap<>();
                            mapEventParams.put(PARAM_MODE, "Barcode");
                            mapEventParams.put(PARAM_CARD_TYPE,
                                    loyaltyCard.getBrand() == null ? "Other card" : loyaltyCard.getBrand().getBrandName());
                            CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                    this, KEY_LOYALTY_CARD_ADDED, mapEventParams, false);

                            CustomLogger.d(TAG, "doSetLoyaltyCard success!");
                            LoyaltyCardFlowManager.goToCardDetailAfterAddCard(this, loyaltyCard);
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else if (result.getStatusCode() == LOYALTY_CARD_ALREADY_REGISTERED) {
                            showLoyaltyCardAlreadyRegisteredDialog(
                                    loyaltyCard.getBarCodeNumber(), loyaltyCard.getBarCodeType());
                        } else {
                            showError(result.getStatusCode());
                            qrView.decodeSingle(this);
                        }
                    } else {
                        qrView.decodeSingle(this);
                    }
                },
                loyaltyCard.getBrand().getBrandUuid(),
                loyaltyCard.getBarCodeNumber(),
                loyaltyCard.getBarCodeType(),
                cardType,
                hexColor,
                loyaltyCard.getBrand().getBrandName(),
                brandLogoImage,
                SessionManager.getInstance().getSessionToken());
    }

    private void showLoyaltyCardAlreadyRegisteredDialog(String barcode, String barcodeType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(getString(R.string.error_loyalty_card_already_registered, loyaltyBrand.getBrandName()))
                .setPositiveButton(R.string.yes, (dialog, id) -> {

                    LoaderHelper.showLoader(this);

                    BancomatSdkInterface.Factory.getInstance().doModifyLoyaltyCard(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {
                                        CustomLogger.d(TAG, "doModifyLoyaltyCard success!");

                                        EventBus.getDefault().postSticky(new ReturnLoyaltyCardListEvent());

                                        LoyaltyCard loyaltyCardUpdated = result.getResult();
                                        Intent intent = new Intent();
                                        intent.putExtra(LOYALTY_CARD_UPDATED_EXTRA, loyaltyCardUpdated);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                    } else {
                                        showError(result.getStatusCode());
                                        qrView.decodeSingle(this);
                                    }
                                } else {
                                    qrView.decodeSingle(this);
                                }
                            },
                            barcode,
                            barcodeType,
                            null,
                            loyaltyBrand.getBrandUuid(),
                            DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND,
                            null,
                            loyaltyBrand.getBrandName(),
                            null,
                            SessionManager.getInstance().getSessionToken());

                })
                .setNegativeButton(R.string.no, null)
                .setCancelable(false);
        builder.show();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //Non usato
    }

    private void clickAddCardManually() {
        boolean isAddOtherCard = loyaltyBrand == null;
        LoyaltyCard loyaltyCard = new LoyaltyCard();
        if (loyaltyBrand == null) {
            LoyaltyBrand brand = new LoyaltyBrand();
            brand.setCardType(DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND);
            loyaltyCard.setBrand(brand);
            loyaltyCard.setBarCodeType(BarcodeFormat.CODE_128.toString());
        } else {
            loyaltyCard.setBrand(loyaltyBrand);
        }
        loyaltyCard.setBarCodeType(BarcodeFormat.CODE_128.toString());
        LoyaltyCardFlowManager.goToAddCardManually(this, loyaltyCard, isAddOtherCard, loyaltyCard.getBrand().getBrandUuid());
    }

    private void requestPermissionCamera() {
        if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)){
               PermissionFlowManager.goToCameraDisclosure(this, activityResultLauncherCamera);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
    }

    private boolean hasPermissionCamera() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

}