package it.bancomat.pay.consumer.extended.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdkui.activities.ContactsActivity;

public class ContactsActivityExtended extends ContactsActivity {

	@Nullable
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (toolbar != null) {
			toolbar.setLeftImage(R.drawable.empty);
			toolbar.setLeftImageVisibility(false);
			toolbar.setOnClickLeftImageListener(null);
		}
	}

}
