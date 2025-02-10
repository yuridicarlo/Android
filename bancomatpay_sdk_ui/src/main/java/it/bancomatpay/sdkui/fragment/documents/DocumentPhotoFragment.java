package it.bancomatpay.sdkui.fragment.documents;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.activities.documents.DocumentDetailActivity;
import it.bancomatpay.sdkui.databinding.FragmentDocumentPhotoBinding;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static android.app.Activity.RESULT_OK;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Mobile.INVALID_FILE_PROVIDER_AUTHORITY;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.MULTIMEDIA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FILE_NAME_BACK;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_PHOTO_FILE_NAME_FRONT;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_TYPE_EXTRA;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.DOCUMENT_UUID_EXTRA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_DOCUMENT_SHARE;

public class DocumentPhotoFragment extends Fragment {

    private static final String TAG = DocumentPhotoFragment.class.getSimpleName();

    private static final int REQUEST_PERMISSION_STORAGE = 6000;

    private FragmentDocumentPhotoBinding binding;

    private String documentUuid;
    private DtoDocument.DocumentTypeEnum documentType;
    private boolean canSetDocumentPhoto;

    private Bitmap bitmapFront;
    private Bitmap bitmapBack;

    private InteractionListener listener;


    ActivityResultLauncher<Intent> activityResultLauncherMultimedia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT,result.getResultCode(),data);
            });

    ActivityResultLauncher<String> activityCameraRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            managePermissionRequestResult(REQUEST_PERMISSION_STORAGE);
        }
    });

    public static DocumentPhotoFragment newInstance(String documentUuid, DtoDocument.DocumentTypeEnum documentType) {
        DocumentPhotoFragment fragment = new DocumentPhotoFragment();
        Bundle args = new Bundle();
        args.putSerializable(DOCUMENT_TYPE_EXTRA, documentType);
        args.putString(DOCUMENT_UUID_EXTRA, documentUuid);
        fragment.setArguments(args);
        return fragment;
    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            documentType = (DtoDocument.DocumentTypeEnum) getArguments().getSerializable(DOCUMENT_TYPE_EXTRA);
            documentUuid = getArguments().getString(DOCUMENT_UUID_EXTRA);
            canSetDocumentPhoto = !TextUtils.isEmpty(documentUuid);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDocumentPhotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabAddDocumentImageFront.setOnClickListener(new CustomOnClickListener(v -> clickFabAddImageFront()));
        binding.fabAddDocumentImageBack.setOnClickListener(new CustomOnClickListener(v -> clickFabAddImageBack()));
        binding.buttonShareDocument.setOnClickListener(new CustomOnClickListener(v -> {
            if (hasPermissionStorage() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                clickShareDocument();
            } else {
                requestPermissionStorage();
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void clickFabAddImageFront() {
        if (listener != null) {
            if (canSetDocumentPhoto) {
                listener.goToAddDocumentImageFront(documentUuid, documentType);
            }else {
                listener.onSetDocumentPhotoWarning();
            }
        }
    }

    private void clickFabAddImageBack() {
        if (listener != null) {
            if (canSetDocumentPhoto) {
                listener.goToAddDocumentImageBack(documentUuid, documentType);
            } else {
                listener.onSetDocumentPhotoWarning();
            }
        }
    }

    private void clickShareDocument() {

        CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_DOCUMENT_SHARE, null, false);

        File fileImageFront = null;
        File fileImageBack = null;

        Uri uriImageFront = null;
        Uri uriImageBack = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            fileImageFront = saveImageToInternalMemoryLegacy(DOCUMENT_PHOTO_FILE_NAME_FRONT);
            fileImageBack = saveImageToInternalMemoryLegacy(DOCUMENT_PHOTO_FILE_NAME_BACK);

        } else {

            uriImageFront = saveImageToInternalMemoryAPI29(DOCUMENT_PHOTO_FILE_NAME_FRONT);
            uriImageBack = saveImageToInternalMemoryAPI29(DOCUMENT_PHOTO_FILE_NAME_BACK);

        }

        String authority = BancomatDataManager.getInstance().getFileProviderAuthority();

        if (!TextUtils.isEmpty(authority)) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            ArrayList<Uri> files = new ArrayList<>();

            Uri contentUriFront;
            Uri contentUriBack;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                contentUriFront = fileImageFront != null ? FileProvider.getUriForFile(requireContext(), authority, fileImageFront) : null;
                contentUriBack = fileImageBack != null ? FileProvider.getUriForFile(requireContext(), authority, fileImageBack) : null;
            } else {
                contentUriFront = uriImageFront;
                contentUriBack = uriImageBack;
            }

            if (contentUriFront != null) {
                files.add(contentUriFront);
            }
            if (contentUriBack != null) {
                files.add(contentUriBack);
            }

            sharingIntent.setType("image/jpeg");

            //sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            //sharingIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                boolean clipDataFrontAdded = false;
                boolean clipDataBackAdded = false;
                ClipData clipData = null;
                if (bitmapFront != null) {
                    clipData = ClipData.newRawUri("", contentUriFront);
                    clipDataFrontAdded = true;
                } else if (bitmapBack != null) {
                    clipData = ClipData.newRawUri("", contentUriBack);
                    clipDataBackAdded = true;
                }
                sharingIntent.setClipData(clipData);
                if (sharingIntent.getClipData() != null) {
                    if (clipDataFrontAdded) {
                        sharingIntent.getClipData().addItem(new ClipData.Item(contentUriBack));
                    } else if (clipDataBackAdded) {
                        sharingIntent.getClipData().addItem(new ClipData.Item(contentUriFront));
                    }
                }
            }
            sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(sharingIntent, getString(R.string.share));

            List<ResolveInfo> resInfoList = requireActivity().getPackageManager()
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                if (contentUriFront != null) {
                    requireActivity().grantUriPermission(packageName, contentUriFront,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                if (contentUriBack != null) {
                    requireActivity().grantUriPermission(packageName, contentUriBack,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            if (getActivity() instanceof DocumentDetailActivity) {
                DocumentDetailActivity activity = (DocumentDetailActivity) getActivity();
                activity.getActivityResultLauncherShareDocumentPhoto().launch(chooser);
            }


        } else {
            ((GenericErrorActivity) requireActivity()).showError(INVALID_FILE_PROVIDER_AUTHORITY);
        }
    }

    @Nullable
    private File saveImageToInternalMemoryLegacy(String fileName) {
        File file = new File(requireActivity().getCacheDir(), fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            if (fileName.equals(DOCUMENT_PHOTO_FILE_NAME_FRONT)) {
                bitmapFront.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else if (fileName.equals(DOCUMENT_PHOTO_FILE_NAME_BACK)) {
                bitmapBack.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else {
                return null;
            }
        } catch (IOException e) {
            CustomLogger.e(TAG, e.getMessage());
            return null;
        }

        return file;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    private Uri saveImageToInternalMemoryAPI29(String fileName) {

        OutputStream outputStream;
        try {

            String filePath = Environment.DIRECTORY_PICTURES + File.separator + "BancomatPaySharedImages";

            ContentResolver resolver = requireActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            if (fileName.equals(DOCUMENT_PHOTO_FILE_NAME_FRONT)) {
                bitmapFront.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } else if (fileName.equals(DOCUMENT_PHOTO_FILE_NAME_BACK)) {
                bitmapBack.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
            outputStream.flush();

            return imageUri;

        } catch (Exception e) {
            CustomLogger.e(TAG, "Error saving bitmap " + e.getMessage());
            return null;
        }
    }

    public void updateSavedDocument(String documentUuid) {
        this.documentUuid = documentUuid;
        this.canSetDocumentPhoto = !TextUtils.isEmpty(documentUuid);
    }

    public void setFrontImage(Bitmap bitmap) {
        int roundPixels = bitmap.getWidth() / 100 * 3;
        binding.imageDocumentFront.setImageBitmap(BancomatSdk.getInstance().getRoundedCornerBitmap(bitmap, roundPixels));
        binding.buttonShareDocument.setEnabled(true);
        bitmapFront = bitmap;
    }

    public void setBackImage(Bitmap bitmap) {
        int roundPixels = bitmap.getWidth() / 100 * 3;
        binding.imageDocumentBack.setImageBitmap(BancomatSdk.getInstance().getRoundedCornerBitmap(bitmap, roundPixels));
        binding.buttonShareDocument.setEnabled(true);
        bitmapBack = bitmap;
    }

    public void setFrontImageAnimation(Bitmap bitmap) {
        int roundPixels = bitmap.getWidth() / 100 * 3;
        Bitmap newImage = BancomatSdk.getInstance().getRoundedCornerBitmap(bitmap, roundPixels);
        Animation animOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        Animation animIn  = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                binding.imageDocumentFront.setImageBitmap(newImage);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                binding.imageDocumentFront.startAnimation(animIn);
            }
        });
        binding.imageDocumentFront.startAnimation(animOut);
        binding.buttonShareDocument.setEnabled(true);
        bitmapFront = bitmap;
    }

    public void setBackImageAnimation(Bitmap bitmap) {
        int roundPixels = bitmap.getWidth() / 100 * 3;
        Bitmap newImage = BancomatSdk.getInstance().getRoundedCornerBitmap(bitmap, roundPixels);
        Animation animOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        Animation animIn  = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                binding.imageDocumentBack.setImageBitmap(newImage);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                binding.imageDocumentBack.startAnimation(animIn);
            }
        });
        binding.imageDocumentBack.startAnimation(animOut);
        binding.buttonShareDocument.setEnabled(true);
        bitmapBack = bitmap;
    }

    private boolean hasPermissionStorage() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionStorage() {
        if(!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(MULTIMEDIA_DISCLOSURE)){
            Toast.makeText(requireContext(), "TEST MULTIMEDIA", Toast.LENGTH_SHORT).show();
            PermissionFlowManager.goToMultimediaDisclosure(this, activityResultLauncherMultimedia);
        }else{
            activityCameraRequestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    protected void managePermissionRequestResult(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
        //    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                clickShareDocument();
           // }
        }
    }

    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT){
            if(resultCode == RESULT_OK){
                clickShareDocument();
            }
        }
    }

    public interface InteractionListener {
        void goToAddDocumentImageFront(String documentUuid, DtoDocument.DocumentTypeEnum documentType);
        void goToAddDocumentImageBack(String documentUuid, DtoDocument.DocumentTypeEnum documentType);
        void onSetDocumentPhotoWarning();
    }

}
