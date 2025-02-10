package it.bancomatpay.sdkui.utilities;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdkui.activities.BankServiceTutorialActivity;

import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.TUTORIAL_BANK_SERVICE;

public class TutorialFlowManager {

	public static final String BANK_SERVICE_LOYALTY_CARDS = "BANK_SERVICE_TUTORIAL_LOYALTY_CARDS";
	public static final String BANK_SERVICE_DOCUMENTS = "BANK_SERVICE_TUTORIAL_DOCUMENTS";
	public static final String BANK_SERVICE_BANK_ID = "BANK_SERVICE_TUTORIAL_BANK_ID";
	public static final String BANK_SERVICE_ATM_CARDLESS = "BANK_SERVICE_ATM_CARDLESS";
	public static final String BANK_SERVICE_PETROL = "BANK_SERVICE_PETROL";

	public static final String TUTORIAL_HIDE_BUTTON_NEXT = "TUTORIAL_HIDE_BUTTON_NEXT";

	public static void goToTutorialLoyaltyCards(Activity activity) {
		Intent intent = new Intent(activity, BankServiceTutorialActivity.class);
		intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_LOYALTY_CARDS);
		intent.putExtra(TUTORIAL_HIDE_BUTTON_NEXT, true);
		activity.startActivity(intent);
	}

	public static void goToDocuments(Activity activity) {
		Intent intent = new Intent(activity, BankServiceTutorialActivity.class);
		intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_DOCUMENTS);
		intent.putExtra(TUTORIAL_HIDE_BUTTON_NEXT, true);
		activity.startActivity(intent);
	}

	public static void goToBankId(Activity activity) {
		Intent intent = new Intent(activity, BankServiceTutorialActivity.class);
		intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_BANK_ID);
		intent.putExtra(TUTORIAL_HIDE_BUTTON_NEXT, true);
		activity.startActivity(intent);
	}

	public static void goToAtmCardless(Activity activity) {
        Intent intent = new Intent(activity, BankServiceTutorialActivity.class);
        intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_ATM_CARDLESS);
        intent.putExtra(TUTORIAL_HIDE_BUTTON_NEXT, true);
        activity.startActivity(intent);
	}

	public static void goToPetrol(Activity activity) {
        Intent intent = new Intent(activity, BankServiceTutorialActivity.class);
        intent.putExtra(TUTORIAL_BANK_SERVICE, BANK_SERVICE_PETROL);
        intent.putExtra(TUTORIAL_HIDE_BUTTON_NEXT, true);
        activity.startActivity(intent);
	}

}
