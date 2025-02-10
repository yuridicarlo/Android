package it.bancomat.pay.consumer.extended.activities;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HELP_OPEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import it.bancomat.pay.consumer.cj.CjConsentsActivity;
import it.bancomat.pay.consumer.extended.flowmanager.HomeFlowManagerExtended;
import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdkui.activities.ProfileActivity;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class ProfileActivityExtended extends ProfileActivity {

	public static final String CJ_OPEN_FROM_PROFILE = "cjOpenFromProfile";

	View profileCjConsents;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActivityName(ProfileActivity.class.getSimpleName());

		profileCjConsents = findViewById(R.id.profile_cj_consents);
		if (!Constants.CUSTOMER_JOURNEY_ENABLED) {
			profileCjConsents.setVisibility(View.GONE);
		} else {
			profileCjConsents.setVisibility(View.VISIBLE);
			profileCjConsents.setOnClickListener(new CustomOnClickListener(v -> {
				Intent intent = new Intent(ProfileActivityExtended.this, CjConsentsActivity.class);
				intent.putExtra(CJ_OPEN_FROM_PROFILE, true);
				startActivity(intent);
			}));
		}

		View supportLayout = findViewById(R.id.support_layout);
		supportLayout.setOnClickListener(new CustomOnClickListener(v -> {
			CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_HELP_OPEN, null, false);
			HomeFlowManagerExtended.goToSupport(this);
		}));

		View settingsLayout = findViewById(R.id.settings_layout);
		settingsLayout.setOnClickListener(new CustomOnClickListener(v -> HomeFlowManagerExtended.goToSettings(this)));

	}

}
