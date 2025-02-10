package it.bancomatpay.sdkui.widgets;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_CROSSFADE_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PROFILE_DEACTIVATE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_MOTIVATION;

public class BottomDialogDisableProfile extends GenericErrorActivity {

    private static final String TAG = BottomDialogDisableProfile.class.getSimpleName();

    private final ArrayList<String> spinnerData;

    private final Activity activity;
    private final View.OnClickListener closeListener;

    private BottomSheetDialog dialog;
    private BottomSheetBehavior<?> behavior;

    private View layoutStep1;
    private View layoutStep2;
    private View layoutStep3;
    private View titleStep1_2;
    private View titleStep3;
    private AppCompatButton buttonConfirm;
    private AppCompatButton buttonDisableApp;
    private AppCompatButton buttonCloseApp;

    public BottomDialogDisableProfile(@NonNull Activity activity, @NonNull View.OnClickListener closeListener) {
        this.activity = activity;
        this.closeListener = closeListener;
        this.spinnerData = new ArrayList<>();
        spinnerData.add(this.activity.getString(R.string.reason_label));
        spinnerData.add(this.activity.getString(R.string.reason_1));
        spinnerData.add(this.activity.getString(R.string.reason_2));
        spinnerData.add(this.activity.getString(R.string.reason_3));
    }

    public void showDialog() {

        dialog = new BottomSheetDialog(activity, R.style.BottomSheetCustom);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(activity);

        View bottomSheetView = inflater.inflate(R.layout.bottom_dialog_disable_profile, null);
        dialog.setContentView(bottomSheetView);

        layoutStep1 = bottomSheetView.findViewById(R.id.layout_disable_profile_step_1);
        layoutStep2 = bottomSheetView.findViewById(R.id.layout_disable_profile_step_2);
        layoutStep3 = bottomSheetView.findViewById(R.id.layout_disable_profile_step_3);

        titleStep1_2 = bottomSheetView.findViewById(R.id.title_disable_profile_step_1_2);
        titleStep3 = bottomSheetView.findViewById(R.id.title_disable_profile_step_3);

        buttonConfirm = bottomSheetView.findViewById(R.id.button_confirm_disable_profile);
        buttonConfirm.setOnClickListener(new CustomOnClickListener(v -> goToStep2()));

        buttonDisableApp = bottomSheetView.findViewById(R.id.button_disable_app);
        buttonDisableApp.setOnClickListener(new CustomOnClickListener(v -> {
            dialog.setCancelable(false);

            BancomatSdkInterface.Factory.getInstance().doDisableUser(activity, result ->
                    {
                        if (result != null) {
                            if (result.isSuccess()) {
                                buttonDisableApp.setEnabled(false);
                                FullStackSdkDataManager.getInstance().putHomePanelExpanded(true);
                                goToStep3();
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showError(result.getStatusCode());
                                buttonDisableApp.setEnabled(true);
                            }
                        }
                    },
                    SessionManager.getInstance().getSessionToken());


        }));
        //  buttonDisableApp.setEnabled(false);

        SpinnerAdapterCustom adapter = new SpinnerAdapterCustom(activity, R.layout.spinner_item_selected, spinnerData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Spinner spinnerDisableProfileReason = bottomSheetView.findViewById(R.id.spinner_disable_profile_reason);
        spinnerDisableProfileReason.setAdapter(adapter);
        spinnerDisableProfileReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomLogger.d(TAG, "Selected item " + parent.getItemAtPosition(position));
                if (position > 0) {
                    buttonDisableApp.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonCloseApp = bottomSheetView.findViewById(R.id.button_close_app);
        buttonCloseApp.setOnClickListener(new CustomOnClickListener(v -> {
            String motivation = null;
            switch (spinnerDisableProfileReason.getSelectedItemPosition()) {
                case 1:
                    motivation = "Not using Bancomat Pay";
                    break;
                case 2:
                    motivation = "Not clear how it works";
                    break;
                case 3:
                    motivation = "I want to change my number";
                    break;
            }
            HashMap<String, String> mapEventParams = new HashMap<>();
            mapEventParams.put(PARAM_MOTIVATION, motivation);
            CjUtils.getInstance().sendCustomerJourneyTagEvent(
                    activity, KEY_PROFILE_DEACTIVATE, mapEventParams, false);
            closeListener.onClick(v);
        }));

        dialog.setOnDismissListener(dialog -> behavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
            }
            return true;
        });

        behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();
    }

    private void goToStep2() {
        AnimationFadeUtil.startCrossfadeAnimation(layoutStep1, layoutStep2, DEFAULT_CROSSFADE_DURATION, DEFAULT_CROSSFADE_DURATION);
        AnimationFadeUtil.startCrossfadeAnimation(buttonConfirm, buttonDisableApp, DEFAULT_CROSSFADE_DURATION, DEFAULT_CROSSFADE_DURATION);
    }

    private void goToStep3() {
        AnimationFadeUtil.startCrossfadeAnimation(titleStep1_2, titleStep3, DEFAULT_CROSSFADE_DURATION, DEFAULT_CROSSFADE_DURATION);
        AnimationFadeUtil.startCrossfadeAnimation(layoutStep2, layoutStep3, DEFAULT_CROSSFADE_DURATION, DEFAULT_CROSSFADE_DURATION);
        AnimationFadeUtil.startCrossfadeAnimation(buttonDisableApp, buttonCloseApp, DEFAULT_CROSSFADE_DURATION, DEFAULT_CROSSFADE_DURATION);
    }

    public boolean isVisible() {
        return dialog != null && dialog.isShowing();
    }

}
