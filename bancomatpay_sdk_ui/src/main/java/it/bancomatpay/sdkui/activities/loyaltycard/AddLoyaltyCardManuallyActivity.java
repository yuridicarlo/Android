package it.bancomatpay.sdkui.activities.loyaltycard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.zxing.BarcodeFormat;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmAddLoyaltyCardManuallyBinding;
import it.bancomatpay.sdkui.events.ReturnLoyaltyCardListEvent;
import it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.LOYALTY_CARD_ALREADY_REGISTERED;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.CAPTURE_CARD_BARCODE_REQUEST_CODE;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.CAPTURE_CARD_IMAGE_REQUEST_CODE;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.IS_ADD_LOYALTY_CARD_KNOWN_BRAND;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.IS_ADD_LOYALTY_CARD_UNKNOWN_BRAND;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.KNOWN_BRAND_UUID_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_BARCODE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_BARCODE_TYPE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_UPDATED_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.SQUARE_CARD_IMAGE_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_LOYALTY_CARD_ADDED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_CARD_TYPE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_MODE;

public class AddLoyaltyCardManuallyActivity extends GenericErrorActivity {

    private static final String TAG = AddLoyaltyCardManuallyActivity.class.getSimpleName();

    private ActivityBcmAddLoyaltyCardManuallyBinding binding;

    private LoyaltyCard loyaltyCard;

    private boolean isAddCardKnownBrand;
    private boolean isAddCardUnknownBrand;
    private boolean isCardImageSet;

    private String knownBrandUuid;
    private String hexColorUnknownBrand;
    private String brandImageUnknownBrand;



    ActivityResultLauncher<Intent> activityResultLauncherCaptureCardImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(CAPTURE_CARD_IMAGE_REQUEST_CODE,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherCaptureCardBarcode = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(CAPTURE_CARD_BARCODE_REQUEST_CODE,result.getResultCode(),data);
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(AddLoyaltyCardManuallyActivity.class.getSimpleName());
        binding = ActivityBcmAddLoyaltyCardManuallyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Prevent keyboard open
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        hexColorUnknownBrand = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(this, R.color.colorAccentBancomat)));

        Result<String> resultBase64 = Conversion.doGetBase64FromDrawable(R.drawable.placeholder_merchant_white);
        if (resultBase64.isSuccess()) {
            brandImageUnknownBrand = resultBase64.getResult();
        }

        isAddCardKnownBrand = getIntent().getBooleanExtra(IS_ADD_LOYALTY_CARD_KNOWN_BRAND, false);
        isAddCardUnknownBrand = getIntent().getBooleanExtra(IS_ADD_LOYALTY_CARD_UNKNOWN_BRAND, false);

        knownBrandUuid = getIntent().getStringExtra(KNOWN_BRAND_UUID_EXTRA);

        isCardImageSet = false;

        loyaltyCard = (LoyaltyCard) getIntent().getSerializableExtra(LOYALTY_CARD_EXTRA);
        if (loyaltyCard != null && loyaltyCard.getBrand() != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND) {
                    getWindow().setStatusBarColor(loyaltyCard.getBrand().getCardColor());
                } else {
                    if (loyaltyCard.getBrand().getCardColor() != 0) {
                        getWindow().setStatusBarColor(loyaltyCard.getBrand().getCardColor());
                    } else {
                        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.generic_coloured_background));
                    }
                }
            }

            if (!TextUtils.isEmpty(loyaltyCard.getBarCodeNumber())) {
                binding.editTextCardBarcode.setText(loyaltyCard.getBarCodeNumber());
            }
            if (!TextUtils.isEmpty(loyaltyCard.getBrand().getBrandName())) {
                binding.editTextCardName.setText(loyaltyCard.getBrand().getBrandName());
            }

            boolean isBarcodeValid = binding.editTextCardBarcode.getText().toString().isEmpty()
                    || checkBarcodeValidity(binding.editTextCardBarcode.getText().toString());
            if (!isBarcodeValid) {
                if (binding.textErrorBarcode.getVisibility() != View.VISIBLE) {
                    AnimationFadeUtil.startFadeInAnimationV1(binding.textErrorBarcode, AnimationFadeUtil.DEFAULT_DURATION);
                    AnimationFadeUtil.startFadeInAnimationV1(binding.imageErrorBackground, AnimationFadeUtil.DEFAULT_DURATION);
                }
            } else {
                if (binding.textErrorBarcode.getVisibility() == View.VISIBLE) {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.textErrorBarcode, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.imageErrorBackground, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                }
            }

            binding.layoutCardImage.setVisibility(View.VISIBLE);
            if (loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND) {
                binding.fabAddImage.hide();
                AnimationFadeUtil.startFadeOutAnimationV1(binding.textCardBrandName, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                AnimationFadeUtil.startFadeInAnimationV1(binding.textLogoKnownCard, AnimationFadeUtil.DEFAULT_DURATION);
                binding.imageCardCircle.setVisibility(View.GONE);
                binding.textLogoKnownCard.setText(loyaltyCard.getBrand().getBrandName());
                binding.editTextCardName.setVisibility(View.GONE);
                if (loyaltyCard.getBrand().isLight()) {
                    binding.toolbarSimple.setLeftImage(R.drawable.back_blue);
                    binding.toolbarSimple.setCenterImage(R.drawable.logo_bancomat_blue);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int flags = getWindow().getDecorView().getSystemUiVisibility();
                        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                        getWindow().getDecorView().setSystemUiVisibility(flags);
                    }
                }
                Picasso.get().load(loyaltyCard.getBrand().getCardLogoUrl())
                        .noPlaceholder()
                        .into(binding.imageCardLogo, new Callback() {
                            @Override
                            public void onSuccess() {
                                AnimationFadeUtil.startFadeOutAnimationV1(binding.textLogoKnownCard, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                                AnimationFadeUtil.startFadeInAnimationV1(binding.imageCardLogo, AnimationFadeUtil.DEFAULT_DURATION);
                            }

                            @Override
                            public void onError(Exception e) {
                                AnimationFadeUtil.startFadeOutAnimationV1(binding.imageCardLogo, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                                AnimationFadeUtil.startFadeInAnimationV1(binding.textLogoKnownCard, AnimationFadeUtil.DEFAULT_DURATION);
                                binding.textLogoKnownCard.setText(loyaltyCard.getBrand().getBrandName());
                            }
                        });

            } else if (loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {

                Result<Bitmap> resultBitmap = Conversion
                        .doGetBitmapFromBase64(loyaltyCard.getBrand().getCardImage());
                if (resultBitmap.getResult() != null) {
                    binding.imageCardCircle.setImageBitmap(resultBitmap.getResult());
                    getColorAccentFromBitmap(resultBitmap.getResult());
                }
                binding.textCardBrandName.setText(loyaltyCard.getBrand().getBrandName());

            }

            if (!isAddCardUnknownBrand) {
                binding.imageCardBackground.setImageResource(R.drawable.empty);
                if (loyaltyCard.getBrand().getCardImage() == null) {
                    binding.imageCardCircle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_merchant_white));
                }
                binding.imageCardBackground.setBackgroundColor(loyaltyCard.getBrand().getCardColor());
            } else {
                binding.imageCardBackground.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.generic_coloured_background));
            }

        }
        KeyboardVisibilityEvent.setEventListener(
                this, this, isOpen -> {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.confirmButton.getLayoutParams();
                    if (isOpen) {
                        binding.confirmButton.setBackgroundResource(R.drawable.button_square_background_state_list);
                        params.leftMargin = 0;
                        params.rightMargin = 0;
                        params.bottomMargin = 0;
                        binding.mainScrollView.postDelayed(() -> binding.mainScrollView.fullScroll(ScrollView.FOCUS_DOWN), 150);
                    } else {
                        binding.confirmButton.setBackgroundResource(R.drawable.button_round_background_state_list);
                        params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        params.bottomMargin = (int) getResources().getDimension(R.dimen.size_25);
                    }
                });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {

                    binding.textCardBrandName.setText(binding.editTextCardName.getText().toString());
                    binding.layoutCardImage.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(binding.editTextCardName.getText())) {
                        if (!isCardImageSet) {
                            if (binding.imageCardLogo.getVisibility() != View.VISIBLE) {
                                binding.imageCardLogo.setImageResource(R.drawable.placeholder_merchant_white);
                                AnimationFadeUtil.startFadeInAnimationV1(binding.imageCardLogo, AnimationFadeUtil.DEFAULT_DURATION);
                            }
                            if (binding.imageCardBackgroundColor.getVisibility() != View.VISIBLE) {
                                AnimationFadeUtil.startFadeInAnimationV1(binding.imageCardBackgroundColor, AnimationFadeUtil.DEFAULT_DURATION);
                            }
                        }
                    } else {
                        if (binding.imageCardLogo.getVisibility() == View.VISIBLE) {
                            AnimationFadeUtil.startFadeOutAnimationV1(binding.imageCardLogo, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                        }
                        if (binding.imageCardBackgroundColor.getVisibility() == View.VISIBLE) {
                            AnimationFadeUtil.startFadeOutAnimationV1(binding.imageCardBackgroundColor, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                        }
                    }
                }
                boolean isBarcodeValid = checkBarcodeValidity(binding.editTextCardBarcode.getText().toString());
                if (getCurrentFocus() != null && getCurrentFocus().getId() == binding.editTextCardBarcode.getId()) {
                    if (!isBarcodeValid) {
                        if (binding.textErrorBarcode.getVisibility() != View.VISIBLE) {
                            AnimationFadeUtil.startFadeInAnimationV1(binding.textErrorBarcode, AnimationFadeUtil.DEFAULT_DURATION);
                            AnimationFadeUtil.startFadeInAnimationV1(binding.imageErrorBackground, AnimationFadeUtil.DEFAULT_DURATION);
                        }
                    } else {
                        if (binding.textErrorBarcode.getVisibility() == View.VISIBLE) {
                            AnimationFadeUtil.startFadeOutAnimationV1(binding.textErrorBarcode, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                            AnimationFadeUtil.startFadeOutAnimationV1(binding.imageErrorBackground, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
                        }
                    }
                }
                binding.confirmButton.setEnabled(
                        !binding.editTextCardName.getText().toString().isEmpty()
                                && isBarcodeValid);
            }

            @Override
            public void afterTextChanged(Editable text) {
            }
        };
        binding.editTextCardName.addTextChangedListener(textWatcher);
        binding.editTextCardBarcode.addTextChangedListener(textWatcher);

        binding.fabAddImage.setOnClickListener(new CustomOnClickListener(v -> LoyaltyCardFlowManager.goToAddCardImage(this, activityResultLauncherCaptureCardImage)));
        binding.imageBarcodeScanner.setOnClickListener(new CustomOnClickListener(v -> clickImageBarcodeScanner()));
        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> clickButtonConfirm()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.confirmButton.setEnabled(
                !binding.editTextCardName.getText().toString().isEmpty()
                        && !binding.editTextCardBarcode.getText().toString().isEmpty());
    }

    private void clickImageBarcodeScanner() {
        LoyaltyBrand brand;
        if (loyaltyCard != null && loyaltyCard.getBrand() != null) {
            brand = loyaltyCard.getBrand();
        } else {
            brand = new LoyaltyBrand();
            brand.setCardType(DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND);
        }
        LoyaltyCardFlowManager.goToCaptureCardBarcode(this, brand, true, activityResultLauncherCaptureCardBarcode);
    }

    private void clickButtonConfirm() {

        if (loyaltyCard.getBrand() == null ||
                loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {

            if (isAddCardUnknownBrand) {

                LoaderHelper.showLoader(this);

                String barcodeNumber = binding.editTextCardBarcode.getText().toString().replaceAll("\\s","");

                BancomatSdkInterface.Factory.getInstance().doSetLoyaltyCard(this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    if (!TextUtils.isEmpty(result.getResult())) {
                                        String loyaltyCardId = result.getResult();
                                        loyaltyCard.setLoyaltyCardId(loyaltyCardId);
                                    }

                                    HashMap<String, String> mapEventParams = new HashMap<>();
                                    mapEventParams.put(PARAM_MODE, "Manual");
                                    mapEventParams.put(PARAM_CARD_TYPE, "Other card");
                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                            this, KEY_LOYALTY_CARD_ADDED, mapEventParams, false);

                                    CustomLogger.d(TAG, "doSetLoyaltyCard success!");
                                    LoyaltyBrand brand = new LoyaltyBrand();
                                    brand.setBrandName(binding.editTextCardName.getText().toString());
                                    loyaltyCard.setBrand(brand);
                                    loyaltyCard.setBarCodeNumber(barcodeNumber);
                                    LoyaltyCardFlowManager.goToCardDetailAfterAddCard(this, loyaltyCard);
                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else if (result.getStatusCode() == LOYALTY_CARD_ALREADY_REGISTERED) {
                                    showLoyaltyCardAlreadyRegisteredDialog();
                                } else {
                                    showError(result.getStatusCode());
                                }
                            }
                        },
                        null,
                        barcodeNumber,
                        loyaltyCard.getBarCodeType(),
                        DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND,
                        hexColorUnknownBrand,
                        binding.editTextCardName.getText().toString(),
                        brandImageUnknownBrand,
                        SessionManager.getInstance().getSessionToken());

            } else {

                if (loyaltyCard != null) {

                    String hexColor;
                    if (!TextUtils.isEmpty(hexColorUnknownBrand)) {
                        hexColor = hexColorUnknownBrand;
                    } else {
                        hexColor = String.format("#%06X", (0xFFFFFF & loyaltyCard.getBrand().getCardColor()));
                    }

                    LoaderHelper.showLoader(this);

                    BancomatSdkInterface.Factory.getInstance().doModifyLoyaltyCard(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {
                                        CustomLogger.d(TAG, "doModifyLoyaltyCard success!");

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
                                    }
                                }
                            },
                            binding.editTextCardBarcode.getText().toString().replaceAll("\\s", ""),
                            !TextUtils.isEmpty(loyaltyCard.getBarCodeType()) ? loyaltyCard.getBarCodeType() : BarcodeFormat.CODE_128.toString(),
                            loyaltyCard.getLoyaltyCardId(),
                            null,
                            DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND,
                            hexColor,
                            binding.editTextCardName.getText().toString().replaceAll("\\s", ""),
                            brandImageUnknownBrand,
                            SessionManager.getInstance().getSessionToken());

                }

            }

        } else if (loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND) {

            if (isAddCardKnownBrand) {

                LoaderHelper.showLoader(this);

                String hexColor = String.format("#%06X", (0xFFFFFF & loyaltyCard.getBrand().getCardColor()));
                String barcodeNumber = binding.editTextCardBarcode.getText().toString().replaceAll("\\s", "");

                BancomatSdkInterface.Factory.getInstance().doSetLoyaltyCard(this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    if (!TextUtils.isEmpty(result.getResult())) {
                                        String loyaltyCardId = result.getResult();
                                        loyaltyCard.setLoyaltyCardId(loyaltyCardId);
                                    }

                                    HashMap<String, String> mapEventParams = new HashMap<>();
                                    mapEventParams.put(PARAM_MODE, "Manual");
                                    mapEventParams.put(PARAM_CARD_TYPE, loyaltyCard.getBrand().getBrandName());
                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                            this, KEY_LOYALTY_CARD_ADDED, mapEventParams, false);

                                    CustomLogger.d(TAG, "doSetLoyaltyCard success!");
                                    loyaltyCard.setBarCodeNumber(barcodeNumber);
                                    LoyaltyCardFlowManager.goToCardDetailAfterAddCard(this, loyaltyCard);
                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else if (result.getStatusCode() == LOYALTY_CARD_ALREADY_REGISTERED) {
                                    showLoyaltyCardAlreadyRegisteredDialog();
                                } else {
                                    showError(result.getStatusCode());
                                }
                            }
                        },
                        loyaltyCard.getBrand().getBrandUuid(),
                        barcodeNumber,
                        !TextUtils.isEmpty(loyaltyCard.getBarCodeType()) ? loyaltyCard.getBarCodeType() : BarcodeFormat.CODE_128.toString(),
                        DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND,
                        hexColor,
                        binding.editTextCardName.getText().toString(),
                        loyaltyCard.getBrand().getCardLogoUrl(),
                        SessionManager.getInstance().getSessionToken());

            } else {

                LoaderHelper.showLoader(this);

                BancomatSdkInterface.Factory.getInstance().doModifyLoyaltyCard(this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {
                                    CustomLogger.d(TAG, "doModifyLoyaltyCard success!");

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
                                }
                            }
                        },
                        binding.editTextCardBarcode.getText().toString().replaceAll("\\s", ""),
                        loyaltyCard.getBarCodeType(),
                        loyaltyCard.getLoyaltyCardId(),
                        loyaltyCard.getBrand().getBrandUuid(),
                        DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND,
                        null,
                        null,
                        null,
                        SessionManager.getInstance().getSessionToken());

            }

        }

    }

    private boolean checkBarcodeValidity(String barcode) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(barcode);
        return matcher.matches();
    }

    private void showLoyaltyCardAlreadyRegisteredDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(getString(R.string.error_loyalty_card_already_registered, binding.editTextCardName.getText().toString()))
                .setPositiveButton(R.string.yes, (dialog, id) -> {

                    DtoBrand.LoyaltyCardTypeEnum brandType;
                    if (loyaltyCard.getBrand() == null ||
                            loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {
                        brandType = DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND;
                    } else {
                        brandType = DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND;
                    }

                    String hexColor = null;
                    if (brandType == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {
                        if (!TextUtils.isEmpty(hexColorUnknownBrand)) {
                            hexColor = hexColorUnknownBrand;
                        } else {
                            hexColor = String.format("#%06X", (0xFFFFFF & loyaltyCard.getBrand().getCardColor()));
                        }
                    }

                    String brandName = binding.editTextCardName.getText().toString();

                    String brandLogoImage = null;
                    if (brandType == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND) {
                        brandLogoImage = brandImageUnknownBrand;
                    }

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
                                    }
                                }
                            },
                            binding.editTextCardBarcode.getText().toString().replaceAll("\\s", ""),
                            loyaltyCard.getBarCodeType(),
                            loyaltyCard.getLoyaltyCardId(),
                            knownBrandUuid,
                            brandType,
                            hexColor,
                            brandName,
                            brandLogoImage,
                            SessionManager.getInstance().getSessionToken());

                })
                .setNegativeButton(R.string.no, null)
                .setCancelable(false);
        builder.show();
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_CARD_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String resultImageBase64 = data.getStringExtra(SQUARE_CARD_IMAGE_EXTRA);
                if (!TextUtils.isEmpty(resultImageBase64)) {

                    Result<Bitmap> resultBitmap = Conversion.doGetBitmapFromBase64(resultImageBase64);

                    if (resultBitmap.isSuccess()) {
                        Bitmap bitmap = resultBitmap.getResult();
                        binding.imageCardCircle.setImageBitmap(bitmap);
                        getColorAccentFromBitmap(bitmap);
                    }

                    brandImageUnknownBrand = resultImageBase64;
                    if (loyaltyCard != null) {
                        loyaltyCard.getBrand().setCardImage(brandImageUnknownBrand);
                    }

                    binding.textCardBrandName.setText(binding.editTextCardName.getText());
                    AnimationFadeUtil.startFadeInAnimationV1(binding.layoutCardImage, AnimationFadeUtil.DEFAULT_DURATION);
                }
            } else {
                if (!isCardImageSet && loyaltyCard.getBrand() != null && loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.UNKNOWN_BRAND
                        && !binding.editTextCardName.getText().toString().isEmpty()) {
                    binding.imageCardBackgroundColor.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentBancomat));
                    binding.imageCardBackgroundColor.setVisibility(View.VISIBLE);
                }
            }
        } else if (requestCode == CAPTURE_CARD_BARCODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                binding.editTextCardBarcode.setText(data.getStringExtra(LOYALTY_CARD_BARCODE_EXTRA));
                if (loyaltyCard != null) {
                    loyaltyCard.setBarCodeType(data.getStringExtra(LOYALTY_CARD_BARCODE_TYPE_EXTRA));
                }
            }
        }
    }


    private void getColorAccentFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int colorDominant = palette.getDominantColor(ContextCompat.getColor(this, R.color.colorAccentBancomat));
                double darkness = 1 - (0.299 * Color.red(colorDominant) + 0.587 * Color.green(colorDominant) + 0.114 * Color.blue(colorDominant)) / 255;
                if (darkness > 0.5) {
                    binding.mainLayout.setBackgroundColor(colorDominant);
                    hexColorUnknownBrand = String.format("#%06X", (0xFFFFFF & colorDominant));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(colorDominant);
                    }
                    binding.imageCardBackgroundColor.setVisibility(View.INVISIBLE);
                    isCardImageSet = true;
                } else {
                    binding.mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentBancomat));
                    isCardImageSet = false;
                }
                binding.imageCardBackground.setVisibility(View.INVISIBLE);
            }
        });
    }


}
