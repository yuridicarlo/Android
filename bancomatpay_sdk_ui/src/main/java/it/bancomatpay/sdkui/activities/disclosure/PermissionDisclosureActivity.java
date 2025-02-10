package it.bancomatpay.sdkui.activities.disclosure;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityCameraDisclosureBinding;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CAMERA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CONTACT_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.DISCLOSURE_TYPE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.MULTIMEDIA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.POSITION_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.PUSH_DISCLOSURE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;

public class PermissionDisclosureActivity extends GenericErrorActivity {

    private ActivityCameraDisclosureBinding binding;
    private String disclosureType;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 122;
    public static final int REQUEST_CODE_CONTACTS_PERMISSION = 1000;
    public static final int REQUEST_CODE_PERMISSION_DISCLOSURE = 2000;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraDisclosureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

        disclosureType = getIntent().getStringExtra(DISCLOSURE_TYPE);
        initLayout();

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> clickContinue()));
        binding.refuseButton.setOnClickListener(new CustomOnClickListener(v -> sendResult()));
    }


    private void initLayout() {
        switch (disclosureType) {
            case CAMERA_DISCLOSURE: {
                binding.imgTypeDisclosure.setImageResource(it.bancomatpay.sdkui.R.drawable.fotocamera);
                binding.textTypeDisclosure.setText(R.string.camera_disclosure_title);
                binding.textDisclosureTitle.setText(R.string.camera_disclosure_subtitle);
                binding.textDisclosureTypeDescription.setText(R.string.camera_disclosure_text);
                break;
            }
            case CONTACT_DISCLOSURE: {
                binding.imgTypeDisclosure.setImageResource(it.bancomatpay.sdkui.R.drawable.contatti);
                binding.textTypeDisclosure.setText(R.string.contact_disclosure_title);
                binding.textDisclosureTitle.setText(R.string.contact_disclosure_subtitle);
                binding.textDisclosureTypeDescription.setText(R.string.contact_disclosure_text);
                break;
            }
            case POSITION_DISCLOSURE: {
                binding.imgTypeDisclosure.setImageResource(it.bancomatpay.sdkui.R.drawable.posizione);
                binding.textTypeDisclosure.setText(R.string.position_disclosure_title);
                binding.textDisclosureTitle.setText(R.string.position_disclosure_subtitle);
                binding.textDisclosureTypeDescription.setText(R.string.position_disclosure_text);
                break;
            }
            case MULTIMEDIA_DISCLOSURE: {
                binding.imgTypeDisclosure.setImageResource(it.bancomatpay.sdkui.R.drawable.multimedia);
                binding.textDisclosureTitle.setText(R.string.multimedia_disclosure_subtitle);
                binding.textTypeDisclosure.setText(R.string.multimedia_disclosure_title);
                binding.textDisclosureTypeDescription.setText(R.string.multimedia_disclosure_text);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void clickContinue() {
        switch (disclosureType) {
            case CAMERA_DISCLOSURE:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_DISCLOSURE);
                break;
            case CONTACT_DISCLOSURE:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_PERMISSION_DISCLOSURE);
                break;
            case POSITION_DISCLOSURE:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_PERMISSION_DISCLOSURE);
                break;
            case MULTIMEDIA_DISCLOSURE:
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION_DISCLOSURE);
                else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION_DISCLOSURE);
                break;
        }
    }

    private void sendResult() {
        switch (disclosureType) {
            case CAMERA_DISCLOSURE:
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    setResult(RESULT_OK);
                }
                else{
                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_PERMISSION, "Camera");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                    setResult(RESULT_CANCELED);
                }
                break;
            case CONTACT_DISCLOSURE:
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    setResult(RESULT_OK);
                }
                else{
                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_PERMISSION, "Contacts");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                    setResult(RESULT_CANCELED);
                }
                break;
            case POSITION_DISCLOSURE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    setResult(RESULT_OK);
                } else {

                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_PERMISSION, "Location");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                    setResult(RESULT_CANCELED);
                }
            case MULTIMEDIA_DISCLOSURE:
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        setResult(RESULT_OK);
                    } else {
                        HashMap<String, String> mapEventParams = new HashMap<>();
                        mapEventParams.put(PARAM_PERMISSION, "Memory");
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                        setResult(RESULT_CANCELED);
                    }
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        setResult(RESULT_OK);
                    } else {
                        HashMap<String, String> mapEventParams = new HashMap<>();
                        mapEventParams.put(PARAM_PERMISSION, "Memory");
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                        setResult(RESULT_CANCELED);
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        setResult(RESULT_OK);
                    } else {
                        HashMap<String, String> mapEventParams = new HashMap<>();
                        mapEventParams.put(PARAM_PERMISSION, "Memory");
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
                        setResult(RESULT_CANCELED);
                    }
                }
                break;
        }
        FullStackSdkDataManager.getInstance().putInAppDisclosureAlreadyShown(true,disclosureType);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sendResult();

    }
}