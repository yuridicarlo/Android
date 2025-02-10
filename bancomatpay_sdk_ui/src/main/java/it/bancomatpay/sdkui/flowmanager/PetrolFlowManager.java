package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.Till;
import it.bancomatpay.sdkui.activities.petrol.PetrolChooseAmountActivity;
import it.bancomatpay.sdkui.activities.petrol.PetrolResultActivity;

public class PetrolFlowManager {

	public static final String PETROL_MERCHANT_DATA = "PETROL_MERCHANT_DATA";
	public static final String PETROL_PUMP_DATA_EXTRA = "PETROL_PUMP_DATA_EXTRA";
	public static final String PETROL_CENTS_AMOUNT = "PETROL_CENTS_AMOUNT";

	public static void goToChooseAmount(Activity activity, ShopItem shopItem, Till pumpData) {
		Intent intent = new Intent(activity, PetrolChooseAmountActivity.class);
		intent.putExtra(PETROL_PUMP_DATA_EXTRA, pumpData);
		intent.putExtra(PETROL_MERCHANT_DATA, shopItem);
		activity.startActivity(intent);
	}

	public static void goToPetrolResult(Activity activity, ShopItem shopItem, Till pumpData, int centsAmount) {
		Intent intent = new Intent(activity, PetrolResultActivity.class);
		intent.putExtra(PETROL_MERCHANT_DATA, shopItem);
		intent.putExtra(PETROL_PUMP_DATA_EXTRA, pumpData);
		intent.putExtra(PETROL_CENTS_AMOUNT, centsAmount);
		activity.startActivity(intent);
	}

}
