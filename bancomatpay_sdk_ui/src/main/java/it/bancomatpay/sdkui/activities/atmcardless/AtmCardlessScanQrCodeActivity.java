package it.bancomatpay.sdkui.activities.atmcardless;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.activities.loyaltycard.AddLoyaltyCardImageActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmAtmCardlessScanQrcodeBinding;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;

import static it.bancomatpay.sdk.manager.utilities.Constants.QR_CODE_BASE_URL;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2B_POS_WITHDRAWAL_NOT_ACTIVATED_BY_BANK;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.QRCODE_GENERIC;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CAMERA_CONSENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class AtmCardlessScanQrCodeActivity extends GenericErrorActivity implements BarcodeCallback {

    private static final int REQUEST_PERMISSION_CAMERA = 250;

    private CaptureManager capture;
    private DecoratedBarcodeView qrView;
    private ActivityBcmAtmCardlessScanQrcodeBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_CAMERA_CONSENT,result.getResultCode(),data);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(AddLoyaltyCardImageActivity.class.getSimpleName());
        binding = ActivityBcmAtmCardlessScanQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        qrView = initializeContent();

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToAtmCardless(this));
        } else {
            binding.toolbarSimple.setRightCenterImageVisibility(false);
        }

        if (hasPermissionCamera()) {
            capture = new CaptureManager(this, qrView);
            Intent intent = getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
            intent.setAction(Intents.Scan.ACTION);
            capture.initializeFromIntent(intent, savedInstanceState);
            capture.decode();
        } else {
            requestPermissionCamera();
        }
        setLightStatusBar(binding.mainLayout, R.color.white_background);

    }

    DecoratedBarcodeView initializeContent() {
        qrView = findViewById(R.id.zxing_barcode_scanner);
        qrView.setStatusText("");
        qrView.getViewFinder().setVisibility(View.GONE);
        return qrView;
    }


    private void pauseCamera() {
        if (capture != null) {
            capture.onPause();
        }
        if (qrView != null) {
            qrView.pause();
        }
    }

    @Override
    protected void onPause() {
        pauseCamera();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (capture != null) {
            capture.onResume();
        }
        if (qrView != null) {
            qrView.resume();
            qrView.decodeSingle(this);
        }
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    @Override
    public void barcodeResult(BarcodeResult result) {

        LoaderHelper.showLoader(this);

        String qrCodeString = result.toString();
        if (BancomatFullStackSdk.getInstance().isQrCodeLinkPayment(qrCodeString)) {
            qrCodeString = qrCodeString.substring(qrCodeString.lastIndexOf(QR_CODE_BASE_URL) + QR_CODE_BASE_URL.length());
        }

        String finalQrCodeString = qrCodeString;
        BancomatSdkInterface.Factory.getInstance().doGetQrCodeDetails(this, result1 -> {
            if (result1 != null) {
                if (result1.isSuccess()) {

                    QrCodeDetailsData detailsData = result1.getResult();
                    if (BancomatFullStackSdk.getInstance().isQrCodeStatic(finalQrCodeString)) {
                        if (BancomatFullStackSdk.getInstance().isQrCodeAtmCardless(detailsData)) {
                            AtmCardlessFlowManager.goToChooseAmount(this,
                                    new MerchantQrPaymentData(detailsData.getPaymentItem(), false), true);
                        } else {
                            showError(QRCODE_GENERIC);
                            if (qrView != null) {
                                qrView.decodeSingle(this);
                            }
                        }
                    } else if (result1.getResult().getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.atmWithdrawal)) {
                        AtmCardlessFlowManager.goToChooseAmount(this,
                                new MerchantQrPaymentData(detailsData.getPaymentItem(), true), true);
                    } else if (result1.getResult().getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.posWithdrawal)) {
                        PosWithdrawalFLowManager.goToPosWithdrawalPaymentData(this, detailsData, true);
                    } else {
                        showError(QRCODE_GENERIC);
                        if (qrView != null) {
                            qrView.decodeSingle(this);
                        }
                    }

                } else if (result1.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else if (result1.getStatusCode() == P2B_POS_WITHDRAWAL_NOT_ACTIVATED_BY_BANK) {
                    showError(result1.getStatusCode());
                } else {
                    showError(result1.getStatusCode());
                    if (qrView != null) {
                        qrView.decodeSingle(this);
                    }
                }
            } else {
                if (qrView != null) {
                    qrView.decodeSingle(this);
                }
            }
        }, qrCodeString, SessionManager.getInstance().getSessionToken());
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //Non usato
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
                capture = new CaptureManager(this, qrView);
                Intent intent = getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
                intent.setAction(Intents.Scan.ACTION);
                capture.initializeFromIntent(intent, null);
                capture.decode();
            } else {
                pauseCamera();

                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_PERMISSION, "Camera");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_SHOW_CAMERA_CONSENT){
            if(resultCode == RESULT_OK){
                capture = new CaptureManager(this, qrView);
                Intent intent = getIntent().putExtra(SCAN_TYPE, MIXED_SCAN);
                intent.setAction(Intents.Scan.ACTION);
                capture.initializeFromIntent(intent, null);
                capture.decode();
            }
            else{
                pauseCamera();
            }
        }
    }
}
