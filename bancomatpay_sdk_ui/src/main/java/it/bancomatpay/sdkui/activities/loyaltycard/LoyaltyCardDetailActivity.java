package it.bancomatpay.sdkui.activities.loyaltycard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.activities.documents.DocumentDetailActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmLoyaltyCardDetailBinding;
import it.bancomatpay.sdkui.events.ReturnLoyaltyCardListEvent;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Mobile.INVALID_FILE_PROVIDER_AUTHORITY;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.MULTIMEDIA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.EDIT_LOYALTY_CARD_REQUEST_CODE;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.IS_ADD_LOYALTY_CARD;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.LOYALTY_CARD_UPDATED_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_LOYALTY_SHARE;

public class LoyaltyCardDetailActivity extends GenericErrorActivity {

    private static final String TAG = LoyaltyCardDetailActivity.class.getSimpleName();

    private ActivityBcmLoyaltyCardDetailBinding binding;

    private static final int REQUEST_PERMISSION_STORAGE = 6023;


    private PopupMenu popupMenu;

    private LoyaltyCard loyaltyCard;
    private boolean isShowDetailAfterAddCard;

    ActivityResultLauncher<Intent> activityResultLauncherEditLoyalityCard = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(EDIT_LOYALTY_CARD_REQUEST_CODE,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherMultimedia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherShareDocumentPhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            });


    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(LoyaltyCardDetailActivity.class.getSimpleName());
        binding = ActivityBcmLoyaltyCardDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loyaltyCard = (LoyaltyCard) getIntent().getSerializableExtra(LOYALTY_CARD_EXTRA);

        isShowDetailAfterAddCard = getIntent().getBooleanExtra(IS_ADD_LOYALTY_CARD, false);

        if (loyaltyCard != null) {
            if (!isShowDetailAfterAddCard) {
                BancomatSdk.getInstance().updateLoyaltyCardFrequent(loyaltyCard);
            }

            Bitmap barcodeBitmap = createBarcode(loyaltyCard.getBarCodeNumber());
            if (barcodeBitmap != null) {
                binding.barcodeImg.setImageBitmap(barcodeBitmap);
            }
            binding.barcodeNumber.setText(loyaltyCard.getBarCodeNumber());

            if (loyaltyCard.getBrand() != null) {
                binding.imageCardBackgroundColor.setBackgroundColor(loyaltyCard.getBrand().getCardColor());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(loyaltyCard.getBrand().getCardColor());
                }
                if (loyaltyCard.getBrand().isLight()) {
                    binding.toolbarSimple.setLeftImage(R.drawable.back_blue);
                    binding.toolbarSimple.setCenterImage(R.drawable.logo_bancomat_blue);
                    binding.toolbarSimple.setRightImage(R.drawable.menu_more_azur);
                }
                if (loyaltyCard.getBrand().getCardType() == DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND) {
                    AnimationFadeUtil.startFadeInAnimationV1(binding.textLogoKnownCard, AnimationFadeUtil.DEFAULT_DURATION);
                    binding.textLogoKnownCard.setText(loyaltyCard.getBrand().getBrandName());
                    Picasso.get().load(loyaltyCard.getBrand().getCardLogoUrl())
                            .noPlaceholder()
                            .into(binding.imageBarcodeLogo, new Callback() {
                                @Override
                                public void onSuccess() {
                                    AnimationFadeUtil.startFadeOutAnimationV1(binding.textLogoKnownCard, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                                    AnimationFadeUtil.startFadeInAnimationV1(binding.imageBarcodeLogo, AnimationFadeUtil.DEFAULT_DURATION);
                                }

                                @Override
                                public void onError(Exception e) {
                                    AnimationFadeUtil.startFadeOutAnimationV1(binding.imageBarcodeLogo, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                                    AnimationFadeUtil.startFadeInAnimationV1(binding.textLogoKnownCard, AnimationFadeUtil.DEFAULT_DURATION);
                                    binding.textLogoKnownCard.setText(loyaltyCard.getBrand().getBrandName());
                                }
                            });
                } else {
                    Result<Bitmap> resultBitmap = Conversion.doGetBitmapFromBase64(loyaltyCard.getBrand().getCardImage());
                    if (resultBitmap.getResult() != null) {
                        binding.imageCardCircle.setImageBitmap(resultBitmap.getResult());
                    } else {
                        binding.imageCardCircle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_merchant_white));
                    }
                    binding.textCardBrandName.setText(loyaltyCard.getBrand().getBrandName());
                }
            }
        }

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this, R.style.CardDetailPopupMenu);
        popupMenu = new PopupMenu(contextThemeWrapper, binding.toolbarSimple.getRightImageReference());
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_card_detail, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.edit_card_voice) {
                LoyaltyCardFlowManager.goToEditCard(this, loyaltyCard, activityResultLauncherEditLoyalityCard);
            } else if (itemId == R.id.share_card_voice) {
                if (hasPermissionStorage()) {
                    shareLoyaltyCard();
                } else {
                    requestPermissionStorage();
                }
            } else if (itemId == R.id.delete_card_voice) {
                showWarningDialog();
            }
            return true;
        });

        popupMenu.setGravity(Gravity.RIGHT);

        binding.toolbarSimple.setOnClickRightImageListener(v -> popupMenu.show());

        //clearLightStatusBar(binding.mainLayout, R.color.generic_coloured_background);

    }


    @Override
    public void onBackPressed() {
        if (isShowDetailAfterAddCard) {
            EventBus.getDefault().postSticky(new ReturnLoyaltyCardListEvent());
        }
        super.onBackPressed();
    }

    @Nullable
    private Bitmap createBarcode(String data) {

        Bitmap barcodeBitmap = null;

        if (data.matches(".*\\d.*")) {
            int size = 650;
            MultiFormatWriter barcodeWriter = new MultiFormatWriter();

            BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
            if (!TextUtils.isEmpty(loyaltyCard.getBarCodeType())) {
                try {
                    barcodeFormat = BarcodeFormat.valueOf(loyaltyCard.getBarCodeType());
                } catch (IllegalArgumentException e) {
                    CustomLogger.e(TAG, "Error in parsing BarcodeType of card, actual barcode type is: " + loyaltyCard.getBarCodeType());
                }
            }

            BitMatrix barcodeBitMatrix = null;
            try {
                barcodeBitMatrix = barcodeWriter.encode(data, barcodeFormat, size, size);
            } catch (Exception e) {
                CustomLogger.e(TAG, "checkBarcodeFormatValidity error: " + e.getMessage());
                try {
                    barcodeBitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_128, size, size);
                } catch (WriterException ex) {
                    CustomLogger.e(TAG, "checkBarcodeFormatValidity error: " + e.getMessage());
                    ex.printStackTrace();
                }
            }

            barcodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    barcodeBitmap.setPixel(x, y, barcodeBitMatrix.get(x, y) ?
                            Color.BLACK : Color.TRANSPARENT);
                }
            }
        }

        return barcodeBitmap;
    }

    private void shareLoyaltyCard() {
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_LOYALTY_SHARE, null, false);

        Bitmap bitmap = getBitmapFromView(binding.layoutToScreenshot,
                binding.layoutToScreenshot.getChildAt(0).getHeight(),
                binding.layoutToScreenshot.getChildAt(0).getWidth());
        String fileName = "BCM_loyalty_card_screenshot" + ".jpg";

        File screenshot = null;

        Uri contentUri = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            screenshot = new File(getCacheDir(), fileName);
            try (FileOutputStream out = new FileOutputStream(screenshot)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            } catch (IOException e) {
                CustomLogger.e(TAG, e.getMessage());
            }

        } else {

            contentUri = saveImageToInternalMemoryAPI29(fileName, bitmap);
        }

        String authority = BancomatDataManager.getInstance().getFileProviderAuthority();

        if (!TextUtils.isEmpty(authority)) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                contentUri = FileProvider.getUriForFile(this, authority, screenshot);
            }

            sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            sharingIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                sharingIntent.setClipData(ClipData.newRawUri("", contentUri));
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }


            sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(sharingIntent, getString(R.string.share));

            List<ResolveInfo> resInfoList = getPackageManager()
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;

                    grantUriPermission(packageName, contentUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            activityResultLauncherShareDocumentPhoto.launch(chooser);

        } else {
            showError(INVALID_FILE_PROVIDER_AUTHORITY);
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.delete_card_message)
                .setPositiveButton(R.string.yes, (dialog, id) -> doDeleteCard())
                .setNegativeButton(R.string.no, null)
                .setCancelable(false);
        builder.show();
    }

    private void doDeleteCard() {

        LoaderHelper.showLoader(this);

        BancomatSdkInterface.Factory.getInstance().doDeleteLoyaltyCard(this, result -> {
                    LoaderHelper.dismissLoader();
                    if (result != null) {
                        if (result.isSuccess()) {
                            finish();
                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            showError(result.getStatusCode());
                        }
                    }
                },
                loyaltyCard.getBarCodeNumber(),
                loyaltyCard.getLoyaltyCardId(),
                SessionManager.getInstance().getSessionToken());
    }

    private void updateLayout(LoyaltyBrand brand) {
        if (brand != null) {
            binding.imageCardBackgroundColor.setBackgroundColor(brand.getCardColor());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(loyaltyCard.getBrand().getCardColor());
            }
            if (loyaltyCard.getBrand().isLight()) {
                binding.toolbarSimple.setLeftImage(R.drawable.back_blue);
                binding.toolbarSimple.setCenterImage(R.drawable.logo_bancomat_blue);
                binding.toolbarSimple.setRightImage(R.drawable.menu_more_azur);
            }
            if (brand.getCardType() == DtoBrand.LoyaltyCardTypeEnum.KNOWN_BRAND) {
                Picasso.get().load(brand.getCardLogoUrl())
                        .noPlaceholder()
                        .into(binding.imageBarcodeLogo);
            } else {
                if (!TextUtils.isEmpty(brand.getCardImage())) {
                    Result<Bitmap> resultBitmap = Conversion
                            .doGetBitmapFromBase64(brand.getCardImage());
                    binding.imageCardCircle.setImageBitmap(resultBitmap.getResult());
                } else {
                    binding.imageCardCircle.setImageResource(R.drawable.placeholder_merchant_white);
                }
                binding.textCardBrandName.setText(brand.getBrandName());
            }
        }
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EDIT_LOYALTY_CARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                LoyaltyCard cardUpdated = (LoyaltyCard) data.getSerializableExtra(LOYALTY_CARD_UPDATED_EXTRA);
                binding.barcodeNumber.setText(cardUpdated.getBarCodeNumber());
                Bitmap barcodeBitmap = createBarcode(cardUpdated.getBarCodeNumber());
                if (barcodeBitmap != null) {
                    binding.barcodeImg.setImageBitmap(barcodeBitmap);
                }
                if (loyaltyCard != null) {
                    loyaltyCard.setBarCodeNumber(cardUpdated.getBarCodeNumber());
                    loyaltyCard.setBrand(cardUpdated.getBrand());
                    loyaltyCard.setBarCodeType(cardUpdated.getBarCodeType());
                }
                updateLayout(cardUpdated.getBrand());
            }
        } else if (requestCode == REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT) {
            if (resultCode == RESULT_OK) {
                shareLoyaltyCard();
            }
        }
    }

    private boolean hasPermissionStorage() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    private void requestPermissionStorage() {
        if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(MULTIMEDIA_DISCLOSURE)){
            PermissionFlowManager.goToMultimediaDisclosure(this, activityResultLauncherMultimedia);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareLoyaltyCard();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    private Uri saveImageToInternalMemoryAPI29(String fileName, Bitmap bitmap) {

        OutputStream outputStream;
        try {

            String filePath = Environment.DIRECTORY_PICTURES + File.separator + "BancomatPaySharedImages";

            ContentResolver resolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();

            return imageUri;

        } catch (Exception e) {
            CustomLogger.e(TAG, "Error saving bitmap " + e.getMessage());
            return null;
        }
    }


}
