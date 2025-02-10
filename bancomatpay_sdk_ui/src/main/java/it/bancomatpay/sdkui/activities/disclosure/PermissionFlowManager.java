package it.bancomatpay.sdkui.activities.disclosure;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

public class PermissionFlowManager {

    public static final String DISCLOSURE_TYPE = "DISCLOSURE_TYPE";
    public static final String CAMERA_DISCLOSURE = "CAMERA_DISCLOSURE";
    public static final String CONTACT_DISCLOSURE = "CONTACT_DISCLOSURE";
    public static final String POSITION_DISCLOSURE = "POSITION_DISCLOSURE";
    public static final String MULTIMEDIA_DISCLOSURE = "MULTIMEDIA_DISCLOSURE";

    public static final String PUSH_DISCLOSURE = "PUSH_DISCLOSURE";

    public static final int REQUEST_CODE_SHOW_CAMERA_CONSENT = 1000;
    public static final int REQUEST_CODE_SHOW_CONTACTS_CONSENT = 1001;
    public static final int REQUEST_CODE_SHOW_POSITION_CONSENT = 1002;
    public static final int REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT = 3000;

    public static final int REQUEST_CODE_SHOW_PUSH_CONSENT = 4000;

    public static void goToCameraDisclosure(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher){
        Intent intent = new Intent(activity, PermissionDisclosureActivity.class);
        intent.putExtra(DISCLOSURE_TYPE, CAMERA_DISCLOSURE);
        activityResultLauncher.launch(intent);
    }

    public static void goToPushDisclosure(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher){
        Intent intent = new Intent(activity, PermissionDisclosureActivity.class);
        intent.putExtra(DISCLOSURE_TYPE, PUSH_DISCLOSURE);
        activityResultLauncher.launch(intent);
    }

    public static void goToContactDisclosure(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher){
        Intent intent = new Intent(activity, PermissionDisclosureActivity.class);
        intent.putExtra(DISCLOSURE_TYPE, CONTACT_DISCLOSURE);
        activityResultLauncher.launch(intent);
    }

    public static void goToPositionDisclosure(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher){
        Intent intent = new Intent(activity, PermissionDisclosureActivity.class);
        intent.putExtra(DISCLOSURE_TYPE, POSITION_DISCLOSURE);
        activityResultLauncher.launch(intent);
    }

    public static void goToMultimediaDisclosure(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher){
        Intent intent = new Intent(activity, PermissionDisclosureActivity.class);
        intent.putExtra(DISCLOSURE_TYPE, MULTIMEDIA_DISCLOSURE);
        activityResultLauncher.launch(intent);
    }

    public static void goToMultimediaDisclosure(Fragment fragment, ActivityResultLauncher<Intent> activityResultLauncher){
        Intent intent = new Intent(fragment.getContext(), PermissionDisclosureActivity.class);
        intent.putExtra(DISCLOSURE_TYPE, MULTIMEDIA_DISCLOSURE);
        activityResultLauncher.launch(intent);
    }
}
