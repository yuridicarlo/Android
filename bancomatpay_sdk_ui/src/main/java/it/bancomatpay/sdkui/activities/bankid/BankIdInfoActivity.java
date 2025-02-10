package it.bancomatpay.sdkui.activities.bankid;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmBankIdInfoBinding;
import it.bancomatpay.sdkui.flowmanager.BankIdFlowManager;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;

public class BankIdInfoActivity extends GenericErrorActivity {

	private ActivityBcmBankIdInfoBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityBcmBankIdInfoBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setActivityName(BankIdInfoActivity.class.getSimpleName());

		binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
		if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
			binding.toolbarSimple.setOnClickRightImageListener(v -> TutorialFlowManager.goToBankId(this));
		} else {
			binding.toolbarSimple.setRightImageVisibility(false);
		}
		binding.buttonBankUuidAnagraphic.setOnClickListener(new CustomOnClickListener(v -> BankIdFlowManager.goToAnagraphic(BankIdInfoActivity.this)));

		setDescriptionText();

	}

	@Override
	protected void onDestroy() {
		BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
		super.onDestroy();
	}

	private void setDescriptionText() {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		builder.append(getString(R.string.bank_id_info_description_part_1)).append(" ");
		builder.setSpan(new ImageSpan(this, R.drawable.logo_bancomat_blue_small),
				builder.length() - 1, builder.length(), 0);
		builder.append(getString(R.string.bank_id_info_description_part_2));

		binding.textBankUuidDescription.setText(builder);
	}

}
