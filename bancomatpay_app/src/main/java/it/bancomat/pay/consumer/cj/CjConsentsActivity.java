package it.bancomat.pay.consumer.cj;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.appsflyer.AppsFlyerLib;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.extended.activities.ProfileActivityExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityCjConsentsBinding;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.SnackbarUtil;

public class CjConsentsActivity extends GenericErrorActivity {

    private ActivityCjConsentsBinding binding;

    private Handler handlerUi;

    private static final String TAG = CjConsentsActivity.class.getSimpleName();
    private static final int DURATION = 4000;
    private boolean isMarketingExpanded = false;
    private boolean isProfilingExpanded = false;
    private boolean isDataToThirdPartiesExpanded = false;

    private boolean isProfilingConsentEnabled;
    private boolean isMarketingConsentEnabled;
    private boolean isDataToThirdPartiesConsentEnabled;

    private boolean isFromProfileActivity;
    private boolean isChangingProfileConsent = false;
    private boolean isChangingMarketingConsent = false;
    private boolean isChangingDataToThirdPartiesConsent = false;

    CompoundButton.OnCheckedChangeListener profilingListener = (buttonView, isChecked) -> manageAllowProfileConsent(isChecked);
    CompoundButton.OnCheckedChangeListener marketingListener = (buttonView, isChecked) -> manageAllowMarketingConsent(isChecked);
    CompoundButton.OnCheckedChangeListener dataToThirdPartiesListener = (buttonView, isChecked) -> manageAllowDataToThirdPartiesConsent(isChecked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(CjConsentsActivity.class.getSimpleName());

        binding = ActivityCjConsentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handlerUi = new Handler(Looper.getMainLooper());

		isFromProfileActivity = getIntent().getBooleanExtra(ProfileActivityExtended.CJ_OPEN_FROM_PROFILE, false);

		binding.switchCompat1.setOnCheckedChangeListener(marketingListener);
		binding.switchCompat2.setOnCheckedChangeListener(profilingListener);
		binding.switchCompat3.setOnCheckedChangeListener(dataToThirdPartiesListener);
		if (isFromProfileActivity) {
			binding.confirmButton.setEnabled(false);
			binding.switchCompat1.setChecked(BancomatPayApiInterface.Factory.getInstance().isMarketingConsentAllowed());
			binding.switchCompat2.setChecked(BancomatPayApiInterface.Factory.getInstance().isProfilingConsentAllowed());
			binding.switchCompat3.setChecked(BancomatPayApiInterface.Factory.getInstance().isDataToThirdPartiesConsentAllowed());
		}
		initializeView();
		setInfoLinkBehaviour();
		setMarketingBoldString();
		setProfilingBoldString();
		setDataToThirdPartiesBoldString();

        binding.layoutSeparatorHeader1.setOnClickListener(new CustomOnClickListener(v -> {
            if (binding.switchCompat1.isChecked()) {
                binding.switchCompat1.setChecked(false);
                manageAllowMarketingConsent(false);
            } else {
                binding.switchCompat1.setChecked(true);
                manageAllowMarketingConsent(true);
            }
        }));

        binding.layoutSeparatorHeader2.setOnClickListener(new CustomOnClickListener(v -> {
            if (binding.switchCompat2.isChecked()) {
                binding.switchCompat2.setChecked(false);
                manageAllowProfileConsent(false);
            } else {
                binding.switchCompat2.setChecked(true);
                manageAllowProfileConsent(true);
            }
        }));

        binding.layoutSeparatorHeader3.setOnClickListener(new CustomOnClickListener(v -> {
            if (binding.switchCompat3.isChecked()) {
                binding.switchCompat3.setChecked(false);
                manageAllowDataToThirdPartiesConsent(false);
            } else {
                binding.switchCompat3.setChecked(true);
                manageAllowDataToThirdPartiesConsent(true);
            }
        }));
    }

    public void initializeView() {
        if (!isFromProfileActivity) {
            binding.toolbarSimple.setLeftImageVisibility(false);
            AnimationFadeUtil.startFadeInAnimationV1(binding.cjTextProfile, AnimationFadeUtil.DEFAULT_DURATION);
        } else {
            binding.toolbarSimple.setCenterImageVisibility(false);
            binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
            AnimationFadeUtil.startFadeOutAnimationV1(binding.cjTextProfile, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            binding.confirmButton.setEnabled(false);
        }
        binding.marketingArrowDown.setOnClickListener(new CustomOnClickListener(v -> {
            if (!isMarketingExpanded) {
                isMarketingExpanded = true;
                AnimationFadeUtil.startFadeInAnimationV1(binding.marketingTextDescription, AnimationFadeUtil.DEFAULT_DURATION);
                binding.marketingArrowDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_up_web));
                binding.marketingExpandableLayoutConsent.expand();
            } else {
                isMarketingExpanded = false;
                AnimationFadeUtil.startFadeOutAnimationV1(binding.marketingTextDescription, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                binding.marketingArrowDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_down_web));
                binding.marketingExpandableLayoutConsent.collapse();
            }

        }));
        binding.profilingArrowDown.setOnClickListener(new CustomOnClickListener(v -> {
            if (!isProfilingExpanded) {
                isProfilingExpanded = true;
                AnimationFadeUtil.startFadeInAnimationV1(binding.profilingTextDescription, AnimationFadeUtil.DEFAULT_DURATION);
                binding.profilingArrowDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_up_web));
                binding.profilingExpandableLayoutConsent.expand();
            } else {
                isProfilingExpanded = false;
                AnimationFadeUtil.startFadeOutAnimationV1(binding.profilingTextDescription, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                binding.profilingArrowDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_down_web));
                binding.profilingExpandableLayoutConsent.collapse();
            }

        }));
        binding.dataToThirdPartiesgArrowDown.setOnClickListener(new CustomOnClickListener(v -> {
            if (!isDataToThirdPartiesExpanded) {
                isDataToThirdPartiesExpanded = true;
                AnimationFadeUtil.startFadeInAnimationV1(binding.dataToThirdPartiesTextDescription, AnimationFadeUtil.DEFAULT_DURATION);
                binding.dataToThirdPartiesgArrowDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_up_web));
                binding.dataToThirdPartiesExpandableLayoutConsent.expand();
            } else {
                isDataToThirdPartiesExpanded = false;
                AnimationFadeUtil.startFadeOutAnimationV1(binding.dataToThirdPartiesTextDescription, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                binding.dataToThirdPartiesgArrowDown.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_down_web));
                binding.dataToThirdPartiesExpandableLayoutConsent.collapse();
            }

        }));

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> savePermission()));
    }

    @Override
    protected void onDestroy() {
        handlerUi.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void setInfoLinkBehaviour() {

        Spannable spannableString = new SpannableString(getString(R.string.consents_privacy_sublabel_2));
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryBancomat)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                ActivationFlowManager.goToShowTermsAndConditions(
                        CjConsentsActivity.this, getString(R.string.privacy_url));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder descriptionText = new SpannableStringBuilder();
        descriptionText.append(getString(R.string.consents_privacy_sublabel_1));
        descriptionText.append(spannableString);
        descriptionText.append(" ");
        descriptionText.append(getString(R.string.consents_privacy_sublabel_3));

        binding.description.setText(descriptionText);
        binding.description.setMovementMethod(LinkMovementMethod.getInstance());
        binding.description.setHighlightColor(Color.TRANSPARENT);

    }

    public void setMarketingBoldString() {
        Spannable spannableString = new SpannableString(getString(R.string.first_consent_description_2));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 1, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder descriptionText = new SpannableStringBuilder();
        descriptionText.append(getString(R.string.first_consent_description_1));
        descriptionText.append(" ");
        descriptionText.append(spannableString);
        binding.marketingTextDescription.setText(descriptionText);
    }

    public void setProfilingBoldString() {
        Spannable spannableString = new SpannableString(getString(R.string.second_consent_description_2));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 1, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder descriptionText = new SpannableStringBuilder();
        descriptionText.append(getString(R.string.second_consent_description_1));
        descriptionText.append(" ");
        descriptionText.append(spannableString);
        binding.profilingTextDescription.setText(descriptionText);
    }

    public void setDataToThirdPartiesBoldString() {
        Spannable spannableString = new SpannableString(getString(R.string.third_consent_description_2));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 1, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder descriptionText = new SpannableStringBuilder();
        descriptionText.append(getString(R.string.third_consent_description_1));
        descriptionText.append(" ");
        descriptionText.append(spannableString);
        binding.dataToThirdPartiesTextDescription.setText(descriptionText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChangingProfileConsent = false;
        isChangingMarketingConsent = false;
        isChangingDataToThirdPartiesConsent = false;

    }

    private void savePermission() {

        LoaderHelper.showLoader(this);

        BancomatPayApiInterface.Factory.getInstance().doSetCustomerJourneyConsents(this, result -> {
            LoaderHelper.dismissLoader();
            BancomatPayApiInterface.Factory.getInstance().setShowCjConsentsinHome(false);
            if (result != null) {
                if (result.isSuccess()) {
                    AppBancomatDataManager.getInstance().putCjConsentsTimestampForKo(Long.toString(System.currentTimeMillis()));
                    BancomatPayApiInterface.Factory.getInstance().setProfilingConsent(isProfilingConsentEnabled);
                    BancomatPayApiInterface.Factory.getInstance().setMarketingConsent(isMarketingConsentEnabled);
                    BancomatPayApiInterface.Factory.getInstance().setDataToThirdPartiesConsent(isDataToThirdPartiesConsentEnabled);

                    if (isChangingProfileConsent) {
                        AppsFlyerLib.getInstance().start(this);
                        CustomLogger.d(TAG, "AppsFlyerLib started");
                    } else {
                        AppsFlyerLib.getInstance().stop(true, this);
                        CustomLogger.d(TAG, "AppsFlyerLib stopped");
                    }

                    if (isFromProfileActivity) {

                        if ((isChangingProfileConsent && !isProfilingConsentEnabled) || (isChangingMarketingConsent && !isMarketingConsentEnabled) || (isChangingDataToThirdPartiesConsent && !isDataToThirdPartiesConsentEnabled)) {
                            isChangingProfileConsent = false;
                            isChangingMarketingConsent = false;
                            isChangingDataToThirdPartiesConsent = false;
                            showRevocationDialog();
                        } else {
                            isChangingProfileConsent = false;
                            isChangingMarketingConsent = false;
                            isChangingDataToThirdPartiesConsent = false;
                            binding.confirmButton.setEnabled(false);
                            finish();
                        }
                    } else {
                        finish();
                    }
                } else {
                    if (!isFromProfileActivity) {
                        AppBancomatDataManager.getInstance().putCjConsentsTimestampForKo(Long.toString(System.currentTimeMillis()));
                        SnackbarUtil.showSnackbarMessageCustom(this, getString(R.string.set_cj_consents_home_ko));
                        handlerUi.postDelayed(this::finish, DURATION);
                    } else {
                        SnackbarUtil.showSnackbarMessageCustom(this, getString(R.string.set_cj_consents_profile_ko));
                    }
                }
            }

        }, isProfilingConsentEnabled, isMarketingConsentEnabled, isDataToThirdPartiesConsentEnabled, SessionManager.getInstance().getSessionToken());

    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(it.bancomatpay.sdkui.R.string.warning_title)
                .setMessage(R.string.cj_warning)
                .setPositiveButton(it.bancomatpay.sdkui.R.string.ok, (dialog, id) -> finish())
                .setNegativeButton(it.bancomatpay.sdkui.R.string.cancel, null)
                .setCancelable(false);
        builder.show();
    }

    private void showRevocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.consent_revocation)
                .setMessage(R.string.revocation_notification)
                .setPositiveButton(R.string.ok, (dialog, id) -> finish())
                .setCancelable(false);
        builder.show();
    }

    private void manageAllowProfileConsent(boolean allowProfiling) {
        isProfilingConsentEnabled = allowProfiling;
        binding.confirmButton.setEnabled(true);
        isChangingProfileConsent = true;
    }

    private void manageAllowMarketingConsent(boolean allowMarketing) {
        isMarketingConsentEnabled = allowMarketing;
        binding.confirmButton.setEnabled(true);
        isChangingMarketingConsent = true;
    }

    private void manageAllowDataToThirdPartiesConsent(boolean allowDataToThirdParties) {
        isDataToThirdPartiesConsentEnabled = allowDataToThirdParties;
        binding.confirmButton.setEnabled(true);
        isChangingDataToThirdPartiesConsent = true;
    }

    @Override
    public void onBackPressed() {
        if (isFromProfileActivity) {
            if (isChangingProfileConsent || isChangingMarketingConsent || isChangingDataToThirdPartiesConsent) {
                showWarningDialog();
            } else {
                finish();
            }
        }
    }

}