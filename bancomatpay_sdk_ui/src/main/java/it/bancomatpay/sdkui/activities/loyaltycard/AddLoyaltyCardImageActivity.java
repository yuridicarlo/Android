package it.bancomatpay.sdkui.activities.loyaltycard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.ActivityBcmAddCardImageBinding;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.MULTIMEDIA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CAMERA_CONSENT;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager.SQUARE_CARD_IMAGE_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class AddLoyaltyCardImageActivity extends GenericErrorActivity {

    private ActivityBcmAddCardImageBinding binding;

    private static final String TAG = AddLoyaltyCardImageActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 2000;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3000;

    private boolean showAlert = false;



    ActivityResultLauncher<Intent> activityResultLauncherGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_GALLERY,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherMultimedia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherCameraConsent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_CAMERA_CONSENT,result.getResultCode(),data);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(AddLoyaltyCardImageActivity.class.getSimpleName());
        binding = ActivityBcmAddCardImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.textChoosePhoto.setPaintFlags(binding.textChoosePhoto.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.cameraView.setLifecycleOwner(this);
        binding.cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult pictureResult) {
                super.onPictureTaken(pictureResult);

                String fileName = "bcmpay_loyalty_card_image";

                BancomatSdk.getInstance().getSquareImageFromPhoto(AddLoyaltyCardImageActivity.this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            Intent intent = new Intent();
                            intent.putExtra(SQUARE_CARD_IMAGE_EXTRA, result.getResult().getBase64Image());
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            CustomLogger.e(TAG, "getSquareImageFromPhoto error: " + result.toString());
                        }
                    }
                }, pictureResult.getData(), fileName);
            }
        });

        binding.buttonTakePicture.setOnClickListener(new CustomOnClickListener(v -> {
            LoaderHelper.showLoader(this);
            binding.cameraView.takePicture();
        }));
        binding.textChoosePhoto.setOnClickListener(new CustomOnClickListener(v -> {
            if (!hasPermissionReadExternalStorage()) {
                requestPermissionWriteExternalStorage();
            }else {
                clickTextChoosePhoto();
            }
        }));

        setLightStatusBar(binding.mainLayout, R.color.white_background);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermissionCamera()) {
            binding.cameraView.open();
        } else {
            requestPermissionCamera();
        }
    }

    @Override
    protected void onPause() {
        if (hasPermissionCamera()) {
            binding.cameraView.close();
        }
        super.onPause();
    }

    private void clickTextChoosePhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activityResultLauncherGallery.launch(intent);
        LoaderHelper.showLoader(this);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException ioException){
            return null;
        }
    }

    private boolean hasPermissionCamera() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionCamera() {
        if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)){
            PermissionFlowManager.goToCameraDisclosure(this, activityResultLauncherCameraConsent);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
    }

    private boolean hasPermissionReadExternalStorage() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionWriteExternalStorage() {
        if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(MULTIMEDIA_DISCLOSURE)){
            PermissionFlowManager.goToMultimediaDisclosure(this , activityResultLauncherMultimedia);
        }
        else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);

            if(!hasPermissionReadExternalStorage() && showAlert){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_request_contact_permission_title)
                        .setMessage(R.string.dialog_request_contact_permission_desc)
                        .setPositiveButton(R.string.settings_title, (dialog, id) -> startActivity(new Intent(Settings.ACTION_SETTINGS)))
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(false);
                builder.show();
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.cameraView.open();
            } else {
                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_PERMISSION, "Camera");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
            }
        } else if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(permissions[0]) ) {
                    showAlert = true;
                }
                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_PERMISSION, "Memory");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
            }else {
                clickTextChoosePhoto();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY: {
                if (resultCode == RESULT_OK && data != null) {

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = false;
                    opts.inPreferredConfig = Bitmap.Config.RGB_565;
                    opts.inDither = true;

                    Uri selectedImage = data.getData();

                    Bitmap imageBitmap = getBitmapFromUri(selectedImage);

                    if (imageBitmap != null) {

                        Matrix rotationMatrix = new Matrix();
                        rotationMatrix.postRotate(270); // for orientation

                        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), rotationMatrix, true);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                        byte[] imageData = stream.toByteArray();
                        rotatedBitmap.recycle();

                        String fileName = "bcmpay_loyalty_card_image";

                        BancomatSdk.getInstance().getSquareImageFromPhoto(AddLoyaltyCardImageActivity.this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {

                                    Intent intent = new Intent();
                                    intent.putExtra(SQUARE_CARD_IMAGE_EXTRA, result.getResult().getBase64Image());
                                    setResult(RESULT_OK, intent);
                                    finish();

                                } else {
                                    CustomLogger.e(TAG, "getSquareImageFromPhoto error: " + result.toString());
                                }
                            }
                        }, imageData, fileName);

                    }

                } else {
                    LoaderHelper.dismissLoader();
                }

            }
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE: {
                if (resultCode == RESULT_OK) {
                    clickTextChoosePhoto();
                }
                //caso ko già considerato dalla PermissionDisclosureActivity
            }
            case REQUEST_CODE_SHOW_CAMERA_CONSENT: {
                if (resultCode == RESULT_OK) {
                    binding.cameraView.open();
                }
            }
        }

    }
}
