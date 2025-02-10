package it.bancomatpay.sdkui.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ProfileData;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.flowmanager.ProfileFlowManager;
import it.bancomatpay.sdkui.utilities.BitmapCache;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.BottomDialogDisableProfile;
import it.bancomatpay.sdkui.widgets.ToolbarSimple;

public class ProfileActivity extends GenericErrorActivity {

    View mainLayout;
    ToolbarSimple toolbarSimple;
    TextView profileName;
    TextView profilePhone;
    CircleImageView profileImageCircle;
    TextView profileLetter;
    View profileAccountsManagement;
    View profileSpendingLimits;
    View profileDisableProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(ProfileActivity.class.getSimpleName());
        setContentView(R.layout.activity_bcm_profile);

        mainLayout = findViewById(R.id.main_layout);
        toolbarSimple = findViewById(R.id.toolbar_simple);
        profileName = findViewById(R.id.profile_name);
        profilePhone = findViewById(R.id.profile_phone);
        profileImageCircle = findViewById(R.id.profile_image_circle);
        profileLetter = findViewById(R.id.profile_letter);
        profileAccountsManagement = findViewById(R.id.profile_accounts_management);
        profileSpendingLimits = findViewById(R.id.profile_spending_limits);
        profileDisableProfile = findViewById(R.id.profile_disable_profile);

        toolbarSimple.setOnClickLeftImageListener(v -> finish());

        ProfileData profileData = BancomatSdk.getInstance().getProfileData();
        if (profileData.getName() != null) {
            profileName.setText(profileData.getName());
        }
        if (profileData.getMsisdn() != null) {
            profilePhone.setText(profileData.getMsisdn());
        }
        Bitmap bitmap;
        try {
            Uri uri = Uri.parse(profileData.getImage());
            bitmap = BitmapCache.getInstance().getThumbnail(uri, this);
        } catch (Exception e) {
            bitmap = null;
        }
        if (bitmap != null) {
            profileImageCircle.setImageBitmap(bitmap);
            profileLetter.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(profileData.getLetter())) {
                profileImageCircle.setImageResource(R.drawable.profile_letter_circle_background);
                profileLetter.setVisibility(View.VISIBLE);
                profileLetter.setText(profileData.getLetter());
            } else {
                profileLetter.setVisibility(View.GONE);
                profileImageCircle.setImageResource(R.drawable.placeholder_contact);
            }
        }

        if (BancomatDataManager.getInstance().isHideSpendingLimits()) {
            profileSpendingLimits.setVisibility(View.GONE);
        } else {
            profileSpendingLimits.setVisibility(View.VISIBLE);
        }

        profileAccountsManagement.setOnClickListener(new CustomOnClickListener(v -> ProfileFlowManager.goToAccountsManagement(this)));
        profileSpendingLimits.setOnClickListener(new CustomOnClickListener(v -> ProfileFlowManager.goToSpendingLimits(this)));
        profileDisableProfile.setOnClickListener(new CustomOnClickListener(v -> showDisableProfileDialog()));

        clearLightStatusBar(mainLayout, R.color.blue_statusbar_color);


    }

    private void showDisableProfileDialog() {
        BottomDialogDisableProfile bottomDialog = new BottomDialogDisableProfile(
                this, v -> {
            BCMAbortCallback.getInstance().getAuthenticationListener()
                    .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_USER_DELETED);
            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                    .goToHome(this, true, false, true);
        });
        bottomDialog.showDialog();
    }


}
