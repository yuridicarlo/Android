package it.bancomatpay.sdkui.activities.documents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;

import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.ActivityBcmAddDocumentImageBinding;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CAMERA_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_NAME_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_BACK_BOOL;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_BACK_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FRONT_BOOL;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FRONT_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_TYPE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_UUID_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.REQUEST_CODE_SAVE_DOCUMENT_IMAGE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

import org.greenrobot.eventbus.EventBus;

public class AddDocumentImageActivity extends GenericErrorActivity {

    private static final String TAG = AddDocumentImageActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION_CAMERA = 1000;

    private String documentUuid;
    private DtoDocument.DocumentTypeEnum documentType;
    private String documentName;
    private boolean isDocumentFront;
    private boolean isDocumentBack;

    private ActivityBcmAddDocumentImageBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncherDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SAVE_DOCUMENT_IMAGE,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_CAMERA_CONSENT,result.getResultCode(),data);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(AddDocumentImageActivity.class.getSimpleName());
        binding = ActivityBcmAddDocumentImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isDocumentFront = getIntent().getBooleanExtra(DOCUMENT_PHOTO_FRONT_BOOL, false);
        isDocumentBack = getIntent().getBooleanExtra(DOCUMENT_PHOTO_BACK_BOOL, false);

        documentName = getIntent().getStringExtra(DOCUMENT_NAME_EXTRA);

        documentUuid = getIntent().getStringExtra(DOCUMENT_UUID_EXTRA);
        documentType = (DtoDocument.DocumentTypeEnum) getIntent().getSerializableExtra(DOCUMENT_TYPE_EXTRA);
        initTextTitle();

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        setMask();
        binding.buttonTakePicture.setOnClickListener(new CustomOnClickListener(v -> {
            LoaderHelper.showLoader(this);
            binding.cameraView.takePicture();
        }));

        binding.cameraView.setLifecycleOwner(this);
        binding.cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult pictureResult) {
                super.onPictureTaken(pictureResult);

                String fileName = "bcmpay_document_image";
                if (isDocumentFront) {
                    fileName = "bcmpay_document_image_front";
                } else if (isDocumentBack) {
                    fileName = "bcmpay_document_image_back";
                }

                boolean isCardDocument = documentType == DtoDocument.DocumentTypeEnum.ELECTRONIC_IDENTITY_CARD
                        || documentType == DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD
                        || documentType == DtoDocument.DocumentTypeEnum.DRIVING_LICENSE;

                BancomatSdk.getInstance().getRectImageFromPhoto(AddDocumentImageActivity.this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            if (isDocumentFront) {
                                DocumentsFlowManager.goToSaveDocumentFrontImage(
                                        AddDocumentImageActivity.this, result.getResult().getBase64Image(), documentUuid, documentType,activityResultLauncherDocument);
                            } else if (isDocumentBack) {
                                DocumentsFlowManager.goToSaveDocumentBackImage(
                                        AddDocumentImageActivity.this, result.getResult().getBase64Image(), documentUuid, documentType,activityResultLauncherDocument);
                            }

                        } else {
                            CustomLogger.e(TAG, "getRectImageFromPhoto error: " + result.toString());
                        }
                    }
                }, pictureResult.getData(), fileName, isCardDocument);
            }
        });


    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void initTextTitle() {
        switch (documentType) {
            case PAPER_IDENTITY_CARD:
                binding.textTitle.setText(getString(R.string.add_document_front_image_title, getString(R.string.popup_menu_voice_1)));
                break;
            case ELECTRONIC_IDENTITY_CARD:
                binding.textTitle.setText(getString(R.string.add_document_front_image_title, getString(R.string.popup_menu_voice_2)));
                break;
            case HEALTH_INSURANCE_CARD:
                binding.textTitle.setText(getString(R.string.add_document_front_image_title, getString(R.string.popup_menu_voice_3)));
                break;
            case DRIVING_LICENSE:
                binding.textTitle.setText(getString(R.string.add_document_front_image_title, getString(R.string.popup_menu_voice_4)));
                break;
            case PASSPORT:
                binding.textTitle.setText(getString(R.string.add_document_front_image_title, getString(R.string.popup_menu_voice_5)));
                break;
            case OTHER:
            default:
                if (!TextUtils.isEmpty(documentName)) {
                    binding.textTitle.setText(getString(R.string.add_document_front_image_title, documentName));
                } else {
                    binding.textTitle.setText(getString(R.string.add_document_front_image_title, getString(R.string.popup_menu_voice_6)));
                }
                break;
        }
    }

    private void setMask() {
        switch (documentType) {
            case PAPER_IDENTITY_CARD:
            case PASSPORT:
            case OTHER:
                binding.documentMask.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_camera_other_documents));
                break;
            case ELECTRONIC_IDENTITY_CARD:
            case HEALTH_INSURANCE_CARD:
            case DRIVING_LICENSE:
                binding.documentMask.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_camera_documents));
                break;
            default:
                binding.documentMask.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_camera_other_documents));
                break;
        }
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
        binding.cameraView.close();
        super.onPause();
    }

    private boolean hasPermissionCamera() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionCamera() {
        if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CAMERA_DISCLOSURE)){
            PermissionFlowManager.goToCameraDisclosure(this, activityResultLauncherCamera);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
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
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //nuova gestione al posto di onActivityResult
    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SAVE_DOCUMENT_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                String documentFrontImageBase64 = data.getStringExtra(DOCUMENT_PHOTO_FRONT_EXTRA);
                String documentBackImageBase64 = data.getStringExtra(DOCUMENT_PHOTO_BACK_EXTRA);
                if (!TextUtils.isEmpty(documentFrontImageBase64)) {
                    Intent intentResult = new Intent();
                    intentResult.putExtra(DOCUMENT_PHOTO_FRONT_EXTRA, documentFrontImageBase64);
                    setResult(RESULT_OK, intentResult);
                    finish();
                } else if (!TextUtils.isEmpty(documentBackImageBase64)) {
                    Intent intentResult = new Intent();
                    intentResult.putExtra(DOCUMENT_PHOTO_BACK_EXTRA, documentBackImageBase64);
                    setResult(RESULT_OK, intentResult);
                    finish();
                }
            }
        } else if (requestCode == REQUEST_CODE_SHOW_CAMERA_CONSENT) {
            if (resultCode == RESULT_OK) {
                binding.cameraView.open();
            }
        }
    }

}
