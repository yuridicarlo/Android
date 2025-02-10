package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdkui.activities.loyaltycard.AddLoyaltyCardImageActivity;
import it.bancomatpay.sdkui.activities.loyaltycard.AddLoyaltyCardManuallyActivity;
import it.bancomatpay.sdkui.activities.loyaltycard.CaptureLoyaltyCardActivity;
import it.bancomatpay.sdkui.activities.loyaltycard.LoyaltyBrandListActivity;
import it.bancomatpay.sdkui.activities.loyaltycard.LoyaltyCardDetailActivity;

public class LoyaltyCardFlowManager {

	public static final String LOYALTY_CARD_EXTRA = "LOYALTY_CARD_EXTRA";
	public static final String LOYALTY_BRAND_EXTRA = "LOYALTY_BRAND_EXTRA";
	public static final String SQUARE_CARD_IMAGE_EXTRA = "SQUARE_CARD_IMAGE_EXTRA";
	public static final String LOYALTY_CARD_BARCODE_EXTRA = "LOYALTY_CARD_BARCODE_EXTRA";
	public static final String LOYALTY_CARD_BARCODE_TYPE_EXTRA = "LOYALTY_CARD_BARCODE_TYPE_EXTRA";
	public static final String LOYALTY_CARD_UPDATED_EXTRA = "LOYALTY_CARD_UPDATED_EXTRA";
	public static final String KNOWN_BRAND_UUID_EXTRA = "KNOWN_BRAND_UUID_EXTRA";
	public static final String IS_MODIFY_LOYALTY_CARD = "IS_MODIFY_LOYALTY_CARD";
	public static final String IS_ADD_LOYALTY_CARD = "IS_ADD_LOYALTY_CARD";
	public static final String IS_ADD_LOYALTY_CARD_KNOWN_BRAND = "IS_ADD_LOYALTY_CARD_KNOWN_BRAND";
	public static final String IS_ADD_LOYALTY_CARD_UNKNOWN_BRAND = "IS_ADD_LOYALTY_CARD_UNKNOWN_BRAND";

	public static final int CAPTURE_CARD_IMAGE_REQUEST_CODE = 1111;
	public static final int CAPTURE_CARD_BARCODE_REQUEST_CODE = 2222;
	public static final int EDIT_LOYALTY_CARD_REQUEST_CODE = 3333;

	public static void goToCaptureCardBarcode(Activity activity) {
		Intent intent = new Intent(activity, CaptureLoyaltyCardActivity.class);
		activity.startActivity(intent);
	}

	public static void goToCaptureCardBarcode(Activity activity, LoyaltyBrand loyaltyBrand, boolean isModifyLoyaltyCard, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, CaptureLoyaltyCardActivity.class);
		intent.putExtra(LOYALTY_BRAND_EXTRA, loyaltyBrand);
		intent.putExtra(IS_MODIFY_LOYALTY_CARD, isModifyLoyaltyCard);
		activityResultLauncher.launch(intent);
	}

	public static void goToCardDetail(Activity activity, LoyaltyCard loyaltyCard) {
		Intent intent = new Intent(activity, LoyaltyCardDetailActivity.class);
		intent.putExtra(LOYALTY_CARD_EXTRA, loyaltyCard);
		activity.startActivity(intent);
	}

	public static void goToCardDetailAfterAddCard(Activity activity, LoyaltyCard loyaltyCard) {
		Intent intent = new Intent(activity, LoyaltyCardDetailActivity.class);
		intent.putExtra(LOYALTY_CARD_EXTRA, loyaltyCard);
		intent.putExtra(IS_ADD_LOYALTY_CARD, true);
		activity.startActivity(intent);
		activity.finish();
	}

	public static void goToBrandedCardList(Activity activity) {
		Intent intent = new Intent(activity, LoyaltyBrandListActivity.class);
		activity.startActivity(intent);
	}

	public static void goToEditCard(Activity activity, LoyaltyCard loyaltyCard, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, AddLoyaltyCardManuallyActivity.class);
		intent.putExtra(LOYALTY_CARD_EXTRA, loyaltyCard);
		activityResultLauncher.launch(intent);
	}

	public static void goToAddCardUnknownBrand(Activity activity, LoyaltyCard loyaltyCard) {
		Intent intent = new Intent(activity, AddLoyaltyCardManuallyActivity.class);
		intent.putExtra(LOYALTY_CARD_EXTRA, loyaltyCard);
		intent.putExtra(IS_ADD_LOYALTY_CARD_UNKNOWN_BRAND, true);
		activity.startActivity(intent);
		activity.finish();
	}

	public static void goToAddCardManually(Activity activity, LoyaltyCard loyaltyCard, boolean isAddOtherCard, String brandUuid) {
		Intent intent = new Intent(activity, AddLoyaltyCardManuallyActivity.class);
		intent.putExtra(LOYALTY_CARD_EXTRA, loyaltyCard);
		intent.putExtra(IS_ADD_LOYALTY_CARD_UNKNOWN_BRAND, isAddOtherCard);
		intent.putExtra(IS_ADD_LOYALTY_CARD_KNOWN_BRAND, !isAddOtherCard);
		if (!isAddOtherCard) {
			intent.putExtra(KNOWN_BRAND_UUID_EXTRA, brandUuid);
		}
		activity.startActivity(intent);
		activity.finish();
	}

	public static void goToAddCardImage(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, AddLoyaltyCardImageActivity.class);
		activityResultLauncher.launch(intent);
	}

}
