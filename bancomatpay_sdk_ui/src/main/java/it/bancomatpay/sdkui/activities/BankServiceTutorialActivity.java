package it.bancomatpay.sdkui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ActivityBcmBankServiceTutorialBinding;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.fragment.tutorial.TutorialAtmCardlessFragment;
import it.bancomatpay.sdkui.fragment.tutorial.TutorialBankIdFragment;
import it.bancomatpay.sdkui.fragment.tutorial.TutorialDocumentsFragment;
import it.bancomatpay.sdkui.fragment.tutorial.TutorialLoyaltyCardsFragment;
import it.bancomatpay.sdkui.fragment.tutorial.TutorialPetrolFragment;

import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.TUTORIAL_BANK_SERVICE;
import static it.bancomatpay.sdkui.flowmanager.PetrolFlowManager.PETROL_MERCHANT_DATA;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_ATM_CARDLESS;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_BANK_ID;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_DOCUMENTS;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_LOYALTY_CARDS;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.BANK_SERVICE_PETROL;
import static it.bancomatpay.sdkui.utilities.TutorialFlowManager.TUTORIAL_HIDE_BUTTON_NEXT;

public class BankServiceTutorialActivity extends GenericErrorActivity
		implements TutorialLoyaltyCardsFragment.InteractionListener, TutorialDocumentsFragment.InteractionListener,
		TutorialBankIdFragment.InteractionListener, TutorialAtmCardlessFragment.InteractionListener,
		TutorialPetrolFragment.InteractionListener {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActivityName(BankServiceTutorialActivity.class.getSimpleName());
		ActivityBcmBankServiceTutorialBinding binding = ActivityBcmBankServiceTutorialBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

		boolean hideButtonNext = getIntent().getBooleanExtra(TUTORIAL_HIDE_BUTTON_NEXT, false);

		String bankService = getIntent().getStringExtra(TUTORIAL_BANK_SERVICE);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch (bankService) {
			case BANK_SERVICE_LOYALTY_CARDS:
				transaction.replace(R.id.container_fragment, TutorialLoyaltyCardsFragment.newInstance(hideButtonNext));
				break;
			case BANK_SERVICE_DOCUMENTS:
				transaction.replace(R.id.container_fragment, TutorialDocumentsFragment.newInstance(hideButtonNext));
				break;
			case BANK_SERVICE_BANK_ID:
				transaction.replace(R.id.container_fragment, TutorialBankIdFragment.newInstance(hideButtonNext));
				break;
			case BANK_SERVICE_ATM_CARDLESS:
				transaction.replace(R.id.container_fragment, TutorialAtmCardlessFragment.newInstance(hideButtonNext));
				break;
			case BANK_SERVICE_PETROL:
				transaction.replace(R.id.container_fragment, TutorialPetrolFragment.newInstance(hideButtonNext));
				break;
		}
		transaction.commit();

	}

	@Override
	public void onTutorialLoyaltyCardsClose() {
		HomeFlowManager.goToLoyaltyCards(this);
		finish();
	}

	@Override
	public void onTutorialBankIdClose() {
		HomeFlowManager.goToBankId(this);
		finish();
	}

	@Override
	public void onTutorialDocumentsClose() {
		HomeFlowManager.goToDocuments(this);
		finish();
	}

	@Override
	public void onTutorialAtmCardlessClose() {
		HomeFlowManager.goToAtmCardless(this);
		finish();
	}

	@Override
	public void onTutorialPetrolClose() {
		HomeFlowManager.goToPetrol(this, (ShopItem) getIntent().getSerializableExtra(PETROL_MERCHANT_DATA));
		finish();
	}

}
