package it.bancomatpay.sdkui.activities.documents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.File;
import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmDocumentDetailBinding;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.fragment.documents.DocumentDataFragment;
import it.bancomatpay.sdkui.fragment.documents.DocumentPhotoFragment;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_BACK_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FILE_NAME_BACK;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FILE_NAME_FRONT;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FRONT_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_TYPE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.REQUEST_CODE_ADD_DOCUMENT_IMAGE;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.REQUEST_CODE_SCAN_FISCAL_CODE;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.REQUEST_CODE_SHARE_DOCUMENT_PHOTO;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.RESULT_SCAN_FISCAL_CODE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_DOCUMENT_ADDED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_DOCUMENT_FLOW;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_DOCUMENT_TYPE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_VIEW_ID;

public class DocumentDetailActivity extends GenericErrorActivity
        implements DocumentDataFragment.InteractionListener, DocumentPhotoFragment.InteractionListener {

    private static final String TAG = DocumentDetailActivity.class.getSimpleName();

    private static final int NUM_TABS = 2;

    private BcmDocument document;
    private DtoDocument.DocumentTypeEnum documentType;
    private String documentName;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ActivityBcmDocumentDetailBinding binding;


    ActivityResultLauncher<Intent> activityResultLauncherCaptureCardImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_ADD_DOCUMENT_IMAGE,result.getResultCode(),data);
            });


    ActivityResultLauncher<Intent> activityResultLauncherFiscalCode = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SCAN_FISCAL_CODE,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherShareDocumentPhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHARE_DOCUMENT_PHOTO,result.getResultCode(),data);
            });

    public  ActivityResultLauncher<Intent> getActivityResultLauncherFiscalCode() {
        return activityResultLauncherFiscalCode;
    }

    public ActivityResultLauncher<Intent> getActivityResultLauncherShareDocumentPhoto() {
        return activityResultLauncherShareDocumentPhoto;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(DocumentDetailActivity.class.getSimpleName());

        binding = ActivityBcmDocumentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setBackgroundDrawableResource(R.drawable.document_data_background);

        document = (BcmDocument) getIntent().getSerializableExtra(DOCUMENT_EXTRA);

        binding.toolbarSimple.setRightImageVisibility(document != null);

        //Valorizzato solo per aggiunta documento
        documentType = (DtoDocument.DocumentTypeEnum) getIntent().getSerializableExtra(DOCUMENT_TYPE_EXTRA);

        initLayoutPlaceholder();

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        binding.toolbarSimple.setOnClickRightImageListener(v -> showDeleteWarningDialog());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        binding.documentViewpager.setAdapter(mSectionsPagerAdapter);

        binding.documentViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tabSelected = binding.documentTabs.getTabAt(position);
                if (tabSelected != null) {
                    tabSelected.select();

                    HashMap<String, String> mapEventParams = new HashMap<>();

                    if (position == 1) {
                        mapEventParams.put(PARAM_VIEW_ID, "Document data");
                        hideSoftKeyboard();
                    } else if (position == 0) {
                        mapEventParams.put(PARAM_VIEW_ID, "Document photos");
                    }

                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                            DocumentDetailActivity.this, KEY_DOCUMENT_FLOW, mapEventParams, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        binding.documentViewpager.setOffscreenPageLimit(NUM_TABS);

        binding.documentTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.documentViewpager));

        KeyboardVisibilityEvent.setEventListener(
                this, this, isOpen -> {
                    DocumentDataFragment fragment = ((DocumentDataFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 0));
                    fragment.updateButtonLayout(isOpen);
                    binding.mainLayout.postDelayed(() -> {
                        if (getCurrentFocus() != null) {
                            binding.mainLayout.scrollTo(0, getCurrentFocus().getBottom());
                        }
                    }, 100);
                });

        loadDocumentImages();

      //  deleteCacheImageFront();
      //  deleteCacheImageBack();

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }


    @Override
    public void onBackPressed() {
        if (document == null) {
            showDocumentNotSavedWarningDialog();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    @Override
    public void hideKeyboard() {
        hideSoftKeyboard();
    }

    @Override
    public void saveDocument(@NonNull BcmDocument document) {

        LoaderHelper.showLoader(this);

        if (this.document == null) {

            BancomatSdkInterface.Factory.getInstance()
                    .doSetDocument(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

                                        HashMap<String, String> mapEventParams = new HashMap<>();
                                        mapEventParams.put(PARAM_DOCUMENT_TYPE, this.documentType.toString());
                                        CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                                this, KEY_DOCUMENT_ADDED, mapEventParams, false);

                                        DocumentDataFragment fragment1 = ((DocumentDataFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 0));
                                        fragment1.updateLayout();
                                        DocumentPhotoFragment fragment2 = ((DocumentPhotoFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 1));
                                        fragment2.updateSavedDocument(result.getResult());

                                        SnackbarUtil.showSnackbarMessageCustom(this, getString(R.string.document_data_saved));
                                        documentName = document.getDocumentName();

                                        this.document = document;

                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                        //finishAffinity();
                                    } else {
                                        showError(result.getStatusCode());
                                    }
                                }
                            },
                            this.documentType,
                            document.getDocumentNumber(),
                            document.getSurname(),
                            document.getName(),
                            document.getFiscalCode(),
                            document.getIssuingInstitution(),
                            document.getIssuingDate(),
                            document.getExpirationDate(),
                            document.getNote(),
                            document.getDocumentName(),
                            SessionManager.getInstance().getSessionToken());

        } else {

            BancomatSdkInterface.Factory.getInstance()
                    .doModifyDocument(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

                                        DocumentDataFragment fragment = ((DocumentDataFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 0));
                                        fragment.updateLayout();
                                        SnackbarUtil.showSnackbarMessageCustom(this, getString(R.string.document_data_saved));
                                        documentName = document.getDocumentName();

                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                        //finishAffinity();
                                    } else {
                                        showError(result.getStatusCode());
                                    }
                                }
                            },
                            document.getDocumentUuid(),
                            document.getDocumentType(),
                            document.getDocumentNumber(),
                            document.getSurname(),
                            document.getName(),
                            document.getFiscalCode(),
                            document.getIssuingInstitution(),
                            document.getIssuingDate(),
                            document.getExpirationDate(),
                            document.getNote(),
                            document.getDocumentName(),
                            SessionManager.getInstance().getSessionToken());

        }
    }

    @Override
    public void goToAddDocumentImageFront(String documentUuid, DtoDocument.DocumentTypeEnum documentType) {
        DocumentsFlowManager.goToAddDocumentImageFront(this, documentUuid, documentType, documentName, activityResultLauncherCaptureCardImage);
    }

    @Override
    public void goToAddDocumentImageBack(String documentUuid, DtoDocument.DocumentTypeEnum documentType) {
        DocumentsFlowManager.goToAddDocumentImageBack(this, documentUuid, documentType, documentName, activityResultLauncherCaptureCardImage);
    }

    @Override
    public void onSetDocumentPhotoWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.document_set_photo_warning_message))
                .setPositiveButton(R.string.document_save, (dialog, id) -> {
                    TabLayout.Tab tab = binding.documentTabs.getTabAt(0);
                    if (tab != null) {
                        tab.select();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            switch (position) {
                case 0:
                    boolean isAddOtherDocument = (document != null && document.getDocumentType() == DtoDocument.DocumentTypeEnum.OTHER)
                            || documentType == DtoDocument.DocumentTypeEnum.OTHER;
                    boolean hasFiscalCode = (document != null && document.getDocumentType() == DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD)
                            || documentType == DtoDocument.DocumentTypeEnum.HEALTH_INSURANCE_CARD;

                    fragment = DocumentDataFragment.newInstance(document, DocumentDetailActivity.this, isAddOtherDocument, hasFiscalCode, documentType);

                    break;

                case 1:
                    if (document != null && document.getDocumentType() != null) {
                        fragment = DocumentPhotoFragment.newInstance(document.getDocumentUuid(), document.getDocumentType());
                    } else {
                        fragment = DocumentPhotoFragment.newInstance(null, documentType);
                    }
                    ((DocumentPhotoFragment) fragment).setInteractionListener(DocumentDetailActivity.this);

                    break;

                default:
                    fragment = null;
                    break;
            }

            return fragment;

        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public Parcelable saveState() {
            Bundle bundle = (Bundle) super.saveState();
            if (bundle != null) {
                bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
            }
            return bundle;
        }

    }

    private void initLayoutPlaceholder() {
        DtoDocument.DocumentTypeEnum type = DtoDocument.DocumentTypeEnum.OTHER;
        if (document != null) {
            type = document.getDocumentType();
            documentName = document.getDocumentName();
        } else if (documentType != null) {
            type = documentType;
        }
        switch (type) {
            case PAPER_IDENTITY_CARD:
                binding.imageDocumentPlaceholder.setImageResource(R.drawable.carta_identita);
                break;
            case ELECTRONIC_IDENTITY_CARD:
                binding.imageDocumentPlaceholder.setImageResource(R.drawable.carta_identita_elettronica);
                break;
            case DRIVING_LICENSE:
                binding.imageDocumentPlaceholder.setImageResource(R.drawable.patente);
                break;
            case HEALTH_INSURANCE_CARD:
                binding.imageDocumentPlaceholder.setImageResource(R.drawable.tessera_sanitaria);
                break;
            case PASSPORT:
                binding.imageDocumentPlaceholder.setImageResource(R.drawable.passaporto);
                break;
            case OTHER:
            default:
                binding.imageDocumentPlaceholder.setImageResource(R.drawable.altro_documento_placeholder);
                if (document != null && !TextUtils.isEmpty(document.getDocumentName())) {
                    binding.textDocumentName.setText(document.getDocumentName());
                } else {
                    binding.textDocumentName.setText(getString(R.string.other_document_default_title));
                }
                binding.textDocumentName.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadDocumentImages() {
        if (document != null) {
            BancomatSdkInterface.Factory.getInstance()
                    .doGetDocumentImages(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

                                        Result<Bitmap> resultImageFront = Conversion.doGetBitmapFromBase64(result.getResult().getFrontImage());
                                        if (resultImageFront.isSuccess()) {
                                            DocumentPhotoFragment fragment = ((DocumentPhotoFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 1));
                                            fragment.setFrontImageAnimation(resultImageFront.getResult());
                                        }

                                        Result<Bitmap> resultImageBack = Conversion.doGetBitmapFromBase64(result.getResult().getBackImage());
                                        if (resultImageBack.isSuccess()) {
                                            DocumentPhotoFragment fragment = ((DocumentPhotoFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 1));
                                            fragment.setBackImageAnimation(resultImageBack.getResult());
                                        }

                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                        //finishAffinity();
                                    } else {
                                        showError(result.getStatusCode());
                                    }
                                }
                            },
                            document.getDocumentUuid(),
                            SessionManager.getInstance().getSessionToken());
        }
    }

    private void showDeleteWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.delete_document_message)
                .setPositiveButton(R.string.yes, (dialog, id) -> doDeleteDocument())
                .setNegativeButton(R.string.no, null)
                .setCancelable(false);
        builder.show();
    }

    private void showDocumentNotSavedWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.back_on_document_message)
                .setPositiveButton(R.string.yes, (dialog, id) -> finish())
                .setNegativeButton(R.string.no, null)
                .setCancelable(false);
        builder.show();
    }

    private void doDeleteDocument() {
        if (document != null) {

            LoaderHelper.showLoader(this);
            BancomatSdkInterface.Factory.getInstance()
                    .doDeleteDocument(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

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
                            document.getDocumentUuid(),
                            SessionManager.getInstance().getSessionToken());

        }
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_ADD_DOCUMENT_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {

                if (!TextUtils.isEmpty(data.getStringExtra(DOCUMENT_PHOTO_FRONT_EXTRA))) {
                    String base64image = data.getStringExtra(DOCUMENT_PHOTO_FRONT_EXTRA);
                    Result<Bitmap> resultBitmap = Conversion.doGetBitmapFromBase64(base64image);
                    if (resultBitmap.isSuccess()) {
                        DocumentPhotoFragment fragment = ((DocumentPhotoFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 1));
                        fragment.setFrontImage(resultBitmap.getResult());
                    } else {
                        CustomLogger.e(TAG, "Get bitmap from document front photo error: " + resultBitmap.getStatusCodeDetail());
                    }
                } else if (!TextUtils.isEmpty(data.getStringExtra(DOCUMENT_PHOTO_BACK_EXTRA))) {
                    String base64image = data.getStringExtra(DOCUMENT_PHOTO_BACK_EXTRA);
                    Result<Bitmap> resultBitmap = Conversion.doGetBitmapFromBase64(base64image);
                    if (resultBitmap.isSuccess()) {
                        DocumentPhotoFragment fragment = ((DocumentPhotoFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 1));
                        fragment.setBackImage(resultBitmap.getResult());
                    } else {
                        CustomLogger.e(TAG, "Get bitmap from document back photo error: " + resultBitmap.getStatusCodeDetail());
                    }
                }

            }
        } else if (requestCode == REQUEST_CODE_SCAN_FISCAL_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && !TextUtils.isEmpty(data.getStringExtra(RESULT_SCAN_FISCAL_CODE))) {
                    DocumentDataFragment fragment = ((DocumentDataFragment) mSectionsPagerAdapter.instantiateItem(binding.documentViewpager, 0));
                    fragment.setFiscalCodeValue(data.getStringExtra(RESULT_SCAN_FISCAL_CODE));
                }
            }
        } else if (requestCode == REQUEST_CODE_SHARE_DOCUMENT_PHOTO) {
            deleteCacheImageFront();
            deleteCacheImageBack();
            finish();
        }
    }

    private void deleteCacheImageFront() {
        File fileFront = new File(getCacheDir(), DOCUMENT_PHOTO_FILE_NAME_FRONT);
        boolean deletedFront = false;
        if (fileFront.exists()) {
            deletedFront = fileFront.delete();
        }
        CustomLogger.d(TAG, DOCUMENT_PHOTO_FILE_NAME_FRONT + " deleted = " + deletedFront);
    }

    private void deleteCacheImageBack() {
        File fileBack = new File(getCacheDir(), DOCUMENT_PHOTO_FILE_NAME_BACK);
        boolean deletedBack = false;
        if (fileBack.exists()) {
            deletedBack = fileBack.delete();
        }
        CustomLogger.d(TAG, DOCUMENT_PHOTO_FILE_NAME_BACK + " deleted = " + deletedBack);
    }

}
