package it.bancomatpay.sdkui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.task.model.AllowPaymentRequest;
import it.bancomatpay.sdk.manager.task.model.EBankIdStatus;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.flowmanager.SettingsFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.widgets.SettingsSeparatorHeader;
import it.bancomatpay.sdkui.widgets.ToolbarSimple;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_SETTINGS_WEB_NOTIFICATION_CHANGE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_SET_TO;

public class SettingsActivity extends GenericErrorActivity {

    ToolbarSimple toolbarSimple;
    SettingsSeparatorHeader separatorHeaderAccessBcmpay;
    SwitchCompat switchManageRequestsConsumer;
    SwitchCompat switchManageRequestsMerchant;
    View layoutBlockedContacts;
    View layoutBlockedMerchants;
    View layoutBcmpayAccesses;
    ExpandableLayout expandableLayoutBcmpayAccess;
    ScrollView scrollViewSettings;
    SwitchCompat switchEnableBcmpayAccess;
    View layoutBankId;
    ProgressBar progressBarBankId;

    CompoundButton.OnCheckedChangeListener consumerListener = (buttonView, isChecked) -> manageAllowPaymentRequestP2P(isChecked);

    CompoundButton.OnCheckedChangeListener merchantListener = (buttonView, isChecked) -> manageAllowPaymentRequestP2B(isChecked);

    CompoundButton.OnCheckedChangeListener bcmPayAccessListener = (buttonView, isChecked) -> manageAllowBancomatPayAccesses(isChecked);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(SettingsActivity.class.getSimpleName());
        setContentView(R.layout.activity_bcm_settings);

        toolbarSimple = findViewById(R.id.toolbar_simple);
        separatorHeaderAccessBcmpay = findViewById(R.id.separator_header_access_bcmpay);
        switchManageRequestsConsumer = findViewById(R.id.switch_manage_requests_consumer);
        switchManageRequestsMerchant = findViewById(R.id.switch_manage_requests_merchant);
        layoutBlockedContacts = findViewById(R.id.layout_blocked_contacts);
        layoutBlockedMerchants = findViewById(R.id.layout_blocked_merchants);
        layoutBcmpayAccesses = findViewById(R.id.layout_bcmpay_accesses);
        expandableLayoutBcmpayAccess = findViewById(R.id.expandable_layout_bcmpay_access);
        scrollViewSettings = findViewById(R.id.scroll_view_settings);
        switchEnableBcmpayAccess = findViewById(R.id.switch_enable_bcmpay_access);
        layoutBankId = findViewById(R.id.layout_bank_id);
        progressBarBankId = findViewById(R.id.progress_bar_bank_id);

        toolbarSimple.setOnClickLeftImageListener(v -> finish());

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.settings_separator_header_access_bcmpay)).append(" ");
        builder.setSpan(new ImageSpan(this, R.drawable.logo_bancomat_blue_small),
                builder.length() - 1, builder.length(), 0);

        separatorHeaderAccessBcmpay.setText(builder);

        checkRequests();

        switchManageRequestsMerchant.setOnCheckedChangeListener(merchantListener);
        switchManageRequestsConsumer.setOnCheckedChangeListener(consumerListener);

        BankServices bankServices = BancomatDataManager.getInstance().getBankBankActiveServices();
        boolean hasBankIdService = false;

        if (bankServices != null && bankServices.getBankServiceList() != null) {
            for (BankServices.EBankService item : bankServices.getBankServiceList()) {
                if (item == BankServices.EBankService.BANKID) {
                    hasBankIdService = true;
                    break;
                }
            }
        }
        if (hasBankIdService) {
            updateBankIdStatus();
        }

        layoutBlockedContacts.setOnClickListener(new CustomOnClickListener(v -> SettingsFlowManager.goToBlockedContacts(this)));
        layoutBlockedMerchants.setOnClickListener(new CustomOnClickListener(v -> SettingsFlowManager.goToBlockedMerchants(this)));
        layoutBcmpayAccesses.setOnClickListener(new CustomOnClickListener(v -> SettingsFlowManager.goToBcmPayAccesses(this)));

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        checkRequests();
        super.onResume();
    }

    private void manageAllowPaymentRequestP2P(boolean value) {
        BancomatSdkInterface.Factory.getInstance().doAllowPaymentRequestP2P(this, result -> {

            switchManageRequestsConsumer.setOnCheckedChangeListener(null);
            if (result != null) {
                if (result.isSuccess()) {
                    AllowPaymentRequest allowPaymentRequest = result.getResult();
                    switchManageRequestsConsumer.setChecked(allowPaymentRequest.isForP2P());
                    if (value) {
                        checkAlert(true);
                    }
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                    switchManageRequestsConsumer.setChecked(!value);
                }
            } else {
                switchManageRequestsConsumer.setChecked(!value);
            }
            switchManageRequestsConsumer.setOnCheckedChangeListener(consumerListener);
        }, value, null, SessionManager.getInstance().getSessionToken());
        LoaderHelper.showLoader(this);
    }

    private void manageAllowPaymentRequestP2B(boolean value) {
        HashMap<String, String> mapEventParams = new HashMap<>();
        mapEventParams.put(PARAM_SET_TO, value ? "ON" : "OFF");
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_SETTINGS_WEB_NOTIFICATION_CHANGE, mapEventParams, false);

        BancomatSdkInterface.Factory.getInstance().doAllowPaymentRequestP2B(this, result -> {

            switchManageRequestsMerchant.setOnCheckedChangeListener(null);
            if (result != null) {
                if (result.isSuccess()) {
                    AllowPaymentRequest allowPaymentRequest = result.getResult();
                    switchManageRequestsMerchant.setChecked(allowPaymentRequest.isForP2B());
                    if (value) {
                        checkAlert(false);
                    }
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                    switchManageRequestsMerchant.setChecked(!value);
                }
            } else {
                switchManageRequestsMerchant.setChecked(!value);
            }
            switchManageRequestsMerchant.setOnCheckedChangeListener(merchantListener);
        }, value, null, SessionManager.getInstance().getSessionToken());
        LoaderHelper.showLoader(this);
    }

    private void manageAllowBancomatPayAccesses(boolean value) {
        BancomatSdkInterface.Factory.getInstance().doSetBankIdStatus(this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            if (value) {
                                expandableLayoutBcmpayAccess.expand();
                                new Handler().postDelayed(() -> scrollViewSettings.fullScroll(ScrollView.FOCUS_DOWN), 200);
                            } else {
                                expandableLayoutBcmpayAccess.collapse();
                            }

                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            showError(result.getStatusCode());
                            switchEnableBcmpayAccess.setChecked(!value);
                        }
                    } else {
                        switchEnableBcmpayAccess.setChecked(!value);
                    }
                },
                value ? EBankIdStatus.ENABLED : EBankIdStatus.DISABLED,
                SessionManager.getInstance().getSessionToken());
        LoaderHelper.showLoader(this);
    }

    private void updateBankIdStatus() {

        layoutBankId.setVisibility(View.VISIBLE);
        progressBarBankId.setVisibility(View.VISIBLE);
        switchEnableBcmpayAccess.setEnabled(false);
        BancomatSdkInterface.Factory.getInstance().doGetBankIdStatus(this, result -> {

            AnimationFadeUtil.startFadeOutAnimationV1(progressBarBankId, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);

            if (result != null) {
                if (result.isSuccess()) {

                    switchEnableBcmpayAccess.setEnabled(true);

                    switch (result.getResult()) {
                        case ENABLED:
                            switchEnableBcmpayAccess.setChecked(true);
                            break;
                        case DISABLED:
                        case UNDEFINED:
                            switchEnableBcmpayAccess.setChecked(false);
                            break;
                    }

                    if (switchEnableBcmpayAccess.isChecked()) {
                        expandableLayoutBcmpayAccess.expand();
                    } else {
                        expandableLayoutBcmpayAccess.collapse();
                    }
                    switchEnableBcmpayAccess.setOnCheckedChangeListener(bcmPayAccessListener);

                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, SessionManager.getInstance().getSessionToken());

    }

    private void checkAlert(boolean isP2P) {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.notification_management_notification_activity_dialog_title))
                    .setMessage(getResources().getString(R.string.notification_management_notification_activity_dialog_label))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // continue with delete
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton(android.R.string.no, ((dialog, which) -> {
                        if (isP2P) {
                            switchManageRequestsConsumer.setChecked(false);
                        } else {
                            switchManageRequestsMerchant.setChecked(false);
                        }
                    }))
                    .show();
        }

    }

    private void checkRequests() {

        AllowPaymentRequest allowPaymentRequest = BancomatSdk.getInstance().getAllowPaymentRequest();
        switchManageRequestsConsumer.setOnCheckedChangeListener(null);
        switchManageRequestsConsumer.setChecked(allowPaymentRequest.isForP2P());
        switchManageRequestsConsumer.setOnCheckedChangeListener(consumerListener);

        switchManageRequestsMerchant.setOnCheckedChangeListener(null);
        switchManageRequestsMerchant.setChecked(allowPaymentRequest.isForP2B());
        switchManageRequestsMerchant.setOnCheckedChangeListener(merchantListener);

    }

}
