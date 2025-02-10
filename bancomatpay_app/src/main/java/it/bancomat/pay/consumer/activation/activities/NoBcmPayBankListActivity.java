package it.bancomat.pay.consumer.activation.activities;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import it.bancomat.pay.consumer.activation.ActivationFlowManager;
import it.bancomat.pay.consumer.activation.adapter.DataBankUnsubscribedListAdapter;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.utilities.AppGenericErrorActivity;
import it.bancomatpay.consumer.databinding.ActivityNoBcmPayBankListBinding;

public class NoBcmPayBankListActivity extends AppGenericErrorActivity implements DataBankUnsubscribedListAdapter.UnsubscribedBankClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityNoBcmPayBankListBinding binding = ActivityNoBcmPayBankListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

		binding.activationBankList.setLayoutManager(new LinearLayoutManager(this));
		List<DataBank> dataBank = DataBankManager.getUnsubscribedDataBankList();
		DataBankUnsubscribedListAdapter adapter = new DataBankUnsubscribedListAdapter(dataBank, this);
		binding.activationBankList.setAdapter(adapter);

	}

	@Override
	public void onBankInteraction(DataBank dataBank) {
		if (!TextUtils.isEmpty(dataBank.getLinkStore())) {
			ActivationFlowManager.goToPlayStore(this, dataBank);
		}
	}

}
