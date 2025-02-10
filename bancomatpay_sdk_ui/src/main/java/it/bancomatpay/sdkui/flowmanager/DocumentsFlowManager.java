package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdkui.activities.documents.AddDocumentImageActivity;
import it.bancomatpay.sdkui.activities.documents.DocumentDetailActivity;
import it.bancomatpay.sdkui.activities.documents.SaveDocumentImageActivity;
import it.bancomatpay.sdkui.activities.documents.ScanFiscalCodeActivity;

public class DocumentsFlowManager {

	public static final String DOCUMENT_EXTRA = "DOCUMENT_EXTRA";
	public static final String DOCUMENT_UUID_EXTRA = "DOCUMENT_UUID_EXTRA";
	public static final String DOCUMENT_TYPE_EXTRA = "DOCUMENT_TYPE_EXTRA";
	public static final String DOCUMENT_NAME_EXTRA = "DOCUMENT_NAME_EXTRA";
	public static final String DOCUMENT_PHOTO_FRONT_BOOL = "DOCUMENT_PHOTO_FRONT_BOOL";
	public static final String DOCUMENT_PHOTO_BACK_BOOL = "DOCUMENT_PHOTO_BACK_BOOL";
	public static final String DOCUMENT_PHOTO_FRONT_EXTRA = "DOCUMENT_PHOTO_FRONT_EXTRA";
	public static final String DOCUMENT_PHOTO_BACK_EXTRA = "DOCUMENT_PHOTO_BACK_EXTRA";
    public static final String IS_ADD_OTHER_DOCUMENT = "IS_ADD_OTHER_DOCUMENT";
    public static final String HAS_FISCAL_CODE = "HAS_FISCAL_CODE";

    public static final String RESULT_SCAN_FISCAL_CODE = "RESULT_SCAN_FISCAL_CODE";

	public static final int REQUEST_CODE_ADD_DOCUMENT_IMAGE = 4000;
	public static final int REQUEST_CODE_SAVE_DOCUMENT_IMAGE = 5000;
	public static final int REQUEST_CODE_SCAN_FISCAL_CODE = 6000;
	public static final int REQUEST_CODE_SHARE_DOCUMENT_PHOTO = 7000;

	public static final String DOCUMENT_PHOTO_FILE_NAME_FRONT = "BCM_document_front" + ".jpeg";
	public static final String DOCUMENT_PHOTO_FILE_NAME_BACK = "BCM_document_back" + ".jpeg";

	public static void goToDocumentDetail(Activity activity, BcmDocument document) {
		Intent intent = new Intent(activity, DocumentDetailActivity.class);
		intent.putExtra(DOCUMENT_EXTRA, document);
		activity.startActivity(intent);
	}

	public static void goToAddDocument(Activity activity, DtoDocument.DocumentTypeEnum documentType) {
		Intent intent = new Intent(activity, DocumentDetailActivity.class);
		intent.putExtra(DOCUMENT_TYPE_EXTRA, documentType);
		activity.startActivity(intent);
	}

	public static void goToAddDocumentImageFront(Activity activity, String documentUuid, DtoDocument.DocumentTypeEnum documentType, String documentName
	, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, AddDocumentImageActivity.class);
		intent.putExtra(DOCUMENT_PHOTO_FRONT_BOOL, true);
		intent.putExtra(DOCUMENT_UUID_EXTRA, documentUuid);
		intent.putExtra(DOCUMENT_TYPE_EXTRA, documentType);
		intent.putExtra(DOCUMENT_NAME_EXTRA, documentName);
		activityResultLauncher.launch(intent);
	}

	public static void goToAddDocumentImageBack(Activity activity, String documentUuid, DtoDocument.DocumentTypeEnum documentType, String documentName
			, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, AddDocumentImageActivity.class);
		intent.putExtra(DOCUMENT_PHOTO_BACK_BOOL, true);
		intent.putExtra(DOCUMENT_UUID_EXTRA, documentUuid);
		intent.putExtra(DOCUMENT_TYPE_EXTRA, documentType);
		intent.putExtra(DOCUMENT_NAME_EXTRA, documentName);
		activityResultLauncher.launch(intent);
	}

	public static void goToSaveDocumentFrontImage(Activity activity, String documentImageFront, String documentUuid, DtoDocument.DocumentTypeEnum documentType, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, SaveDocumentImageActivity.class);
		intent.putExtra(DOCUMENT_PHOTO_FRONT_BOOL, true);
		intent.putExtra(DOCUMENT_UUID_EXTRA, documentUuid);
		intent.putExtra(DOCUMENT_PHOTO_FRONT_EXTRA, documentImageFront);
		intent.putExtra(DOCUMENT_TYPE_EXTRA, documentType);
		activityResultLauncher.launch(intent);
	}

	public static void goToSaveDocumentBackImage(Activity activity, String documentImageFront, String documentUuid, DtoDocument.DocumentTypeEnum documentType, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, SaveDocumentImageActivity.class);
		intent.putExtra(DOCUMENT_PHOTO_BACK_BOOL, true);
		intent.putExtra(DOCUMENT_UUID_EXTRA, documentUuid);
		intent.putExtra(DOCUMENT_PHOTO_BACK_EXTRA, documentImageFront);
		intent.putExtra(DOCUMENT_TYPE_EXTRA, documentType);
		activityResultLauncher.launch(intent);
	}

	public static void goToScanFiscalCode(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, ScanFiscalCodeActivity.class);
		activityResultLauncher.launch(intent);
	}

}
