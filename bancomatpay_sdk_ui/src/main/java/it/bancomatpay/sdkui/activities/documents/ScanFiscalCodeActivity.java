package it.bancomatpay.sdkui.activities.documents;

import static com.google.zxing.client.android.Intents.Scan.MIXED_SCAN;
import static com.google.zxing.client.android.Intents.Scan.SCAN_TYPE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
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
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.ActivityBcmScanFiscalCodeBinding;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CAMERA_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager.RESULT_SCAN_FISCAL_CODE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class ScanFiscalCodeActivity extends GenericErrorActivity implements BarcodeCallback {

    private static final int REQUEST_PERMISSION_CAMERA = 250;

    private CaptureManager capture;
    private DecoratedBarcodeView qrView;
    private ActivityBcmScanFiscalCodeBinding binding;


    ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(ScanFiscalCodeActivity.class.getSimpleName());
        binding = ActivityBcmScanFiscalCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        qrView = initializeContent();


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

        binding.buttonAddManually.setOnClickListener(new CustomOnClickListener(v -> onBackPressed()));

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
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
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
        Intent intentResult = new Intent();
        intentResult.putExtra(RESULT_SCAN_FISCAL_CODE, result.toString());
        setResult(RESULT_OK, intentResult);
        finish();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        //Non usato
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
