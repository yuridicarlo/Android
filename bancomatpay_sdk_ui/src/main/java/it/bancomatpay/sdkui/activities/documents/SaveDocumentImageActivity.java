package it.bancomatpay.sdkui.activities.documents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.RelativeLayout;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmSaveDocumentImageBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_BACK_BOOL;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_BACK_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FRONT_BOOL;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FRONT_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_TYPE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_UUID_EXTRA;

public class SaveDocumentImageActivity extends GenericErrorActivity {

    private boolean isDocumentFront;
    private boolean isDocumentBack;

    private String documentFrontImageBase64;
    private String documentBackImageBase64;
    private String documentUuid;

    private DtoDocument.DocumentTypeEnum documentType;
    private ActivityBcmSaveDocumentImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(SaveDocumentImageActivity.class.getSimpleName());
        binding = ActivityBcmSaveDocumentImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isDocumentFront = getIntent().getBooleanExtra(DOCUMENT_PHOTO_FRONT_BOOL, false);
        isDocumentBack = getIntent().getBooleanExtra(DOCUMENT_PHOTO_BACK_BOOL, false);
        documentFrontImageBase64 = getIntent().getStringExtra(DOCUMENT_PHOTO_FRONT_EXTRA);
        documentBackImageBase64 = getIntent().getStringExtra(DOCUMENT_PHOTO_BACK_EXTRA);

        documentUuid = getIntent().getStringExtra(DOCUMENT_UUID_EXTRA);

        documentType = (DtoDocument.DocumentTypeEnum) getIntent().getSerializableExtra(DOCUMENT_TYPE_EXTRA);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        initView();

        binding.buttonSavePicture.setOnClickListener(new CustomOnClickListener(v -> onSavePicture()));

    }


    private void initView() {

        boolean isCardDocument = true;
        if (documentType != null) {
            isCardDocument = documentType == DtoDocument.DocumentTypeEnum.ELECTRONIC_IDENTITY_CARD
                    || documentType == DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD
                    || documentType == DtoDocument.DocumentTypeEnum.DRIVING_LICENSE;
        }

        if (isDocumentFront) {
            Result<Bitmap> resultImageFront = Conversion.doGetBitmapFromBase64(documentFrontImageBase64);
            if (resultImageFront.isSuccess()) {
                int roundPixels = resultImageFront.getResult().getWidth() / 100 * 3;
                if (isCardDocument) {
                    Bitmap roundedBitmap = BancomatSdk.getInstance().getRoundedCornerBitmap(resultImageFront.getResult(), roundPixels);
                    binding.imageAcquiredBox.setImageBitmap(roundedBitmap);
                } else {
                    Matrix rotationMatrixPaper = new Matrix();
                    rotationMatrixPaper.postRotate(90); // for orientation
                    Bitmap rotatedBitmapPaper = Bitmap.createBitmap(resultImageFront.getResult(), 0, 0,
                            resultImageFront.getResult().getWidth(), resultImageFront.getResult().getHeight(), rotationMatrixPaper, true);
                    Bitmap rotatedRoundedBitmap = BancomatSdk.getInstance().getRoundedCornerBitmap(rotatedBitmapPaper, roundPixels);
                    binding.imageAcquiredBox.setImageBitmap(rotatedRoundedBitmap);
                }
            }
        } else if (isDocumentBack) {
            Result<Bitmap> resultImageBack = Conversion.doGetBitmapFromBase64(documentBackImageBase64);
            if (resultImageBack.isSuccess()) {
                int roundPixels = resultImageBack.getResult().getWidth() / 100 * 3;
                if (isCardDocument) {
                    Bitmap roundedBitmap = BancomatSdk.getInstance().getRoundedCornerBitmap(resultImageBack.getResult(), roundPixels);
                    binding.imageAcquiredBox.setImageBitmap(roundedBitmap);
                } else {
                    Matrix rotationMatrixPaper = new Matrix();
                    rotationMatrixPaper.postRotate(90); // for orientation
                    Bitmap rotatedBitmapPaper = Bitmap.createBitmap(resultImageBack.getResult(), 0, 0,
                            resultImageBack.getResult().getWidth(), resultImageBack.getResult().getHeight(), rotationMatrixPaper, true);
                    Bitmap rotatedRoundedBitmap = BancomatSdk.getInstance().getRoundedCornerBitmap(rotatedBitmapPaper, roundPixels);
                    binding.imageAcquiredBox.setImageBitmap(rotatedRoundedBitmap);
                }
            }
        }
    }

    private void onSavePicture() {
        LoaderHelper.showLoader(this);
        BancomatSdkInterface.Factory.getInstance()
                .doSetDocumentImages(this, result -> {
                            if (result != null) {
                                if (result.isSuccess()) {

                                    Intent intentResult = new Intent();
                                    if (isDocumentFront) {
                                        intentResult.putExtra(DOCUMENT_PHOTO_FRONT_EXTRA, documentFrontImageBase64);
                                    } else if (isDocumentBack) {
                                        intentResult.putExtra(DOCUMENT_PHOTO_BACK_EXTRA, documentBackImageBase64);
                                    }
                                    setResult(RESULT_OK, intentResult);
                                    finish();

                                } else if (result.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                    //finishAffinity();
                                } else {
                                    showError(result.getStatusCode());
                                }
                            }
                        },
                        documentUuid,
                        documentFrontImageBase64,
                        documentBackImageBase64,
                        SessionManager.getInstance().getSessionToken());
    }

}
