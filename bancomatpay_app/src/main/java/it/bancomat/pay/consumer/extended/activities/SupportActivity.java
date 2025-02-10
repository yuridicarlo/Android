package it.bancomat.pay.consumer.extended.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.extended.flowmanager.SupportFlowManager;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomatpay.consumer.BuildConfig;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityBcmSupportBinding;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class SupportActivity extends AppGenericErrorActivity {

    private DataBank bank;

    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBcmSupportBinding binding = ActivityBcmSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        String bankUuid = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();
        bank = DataBankManager.getDataBank(bankUuid);

        binding.textSupportPhone.setText(bank.getPhoneNumber());
        if (!TextUtils.isEmpty(bank.getSupportOpeningTime())) {
            binding.textSupportTime.setText(bank.getSupportOpeningTime());
        } else {
            binding.textSupportTime.setVisibility(View.GONE);
            binding.textSupportPhone.setLines(2);
        }
        if (!TextUtils.isEmpty(bank.getPhoneNumberForeign())) {
            binding.textSupportPhone.append(" " + bank.getPhoneNumberForeign());
        }
        if (!TextUtils.isEmpty(bank.getEmail())) {
            binding.textSupportEmail.setText(bank.getEmail());
        } else {
            binding.layoutEmail.setVisibility(View.GONE);
            binding.viewSeparator.setVisibility(View.GONE);
        }

        String appVersion;
        if (BuildConfig.DEBUG) {
            if (!TextUtils.isEmpty(AppConstants.SERVER_ENV)) {
                appVersion = String.format(getString(R.string.version_app_env), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, AppConstants.SERVER_ENV);
            } else {
                appVersion = String.format(getString(R.string.version_app), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
            }
        } else {
            appVersion = String.format(getString(R.string.version_app), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        }
        binding.textAppVersion.setText(appVersion);

        binding.layoutPhone.setOnClickListener(new CustomOnClickListener(v -> {
		/*	Iterable<PhoneNumberMatch> phoneNumbers = PhoneNumberUtil.getInstance().findNumbers(bank.getPhoneNumber(), Locale.getDefault().getCountry());
		Iterator<PhoneNumberMatch> iterator = phoneNumbers.iterator();
		if (iterator.hasNext()) {
				SupportFlowManager.goToOpenPhone(this,
						PhoneNumberUtil.getInstance().format(iterator.next().number(), PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
		}*/
            SupportFlowManager.goToOpenPhone(this, PhoneNumberUtil.normalizeDiallableCharsOnly(bank.getPhoneNumber())); //allineato a comportamento iOS
        }));

        binding.layoutEmail.setOnClickListener(new CustomOnClickListener(v ->
                SupportFlowManager.goToOpenEmail(SupportActivity.this, bank.getEmail())));
        binding.activationPrivacy.setOnClickListener(new CustomOnClickListener(v ->
                ActivationFlowManager.goToShowTermsAndConditions(this, getString(R.string.privacy_url))));
        binding.activationTermsAndConditions.setOnClickListener(new CustomOnClickListener(v ->
                ActivationFlowManager.goToShowTermsAndConditions(this, getString(R.string.terms_and_conditions_url))));

    }

}
