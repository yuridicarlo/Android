package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdkui.activities.bankid.BankIdAddAddressActivity;
import it.bancomatpay.sdkui.activities.bankid.BankIdAnagraphicActivity;
import it.bancomatpay.sdkui.activities.bankid.BankIdAuthorizeActivity;
import it.bancomatpay.sdkui.activities.bankid.BankIdAuthorizeResultActivity;

public class BankIdFlowManager {

	public static final String ADDRESS_DATA_EXTRA = "ADDRESS_DATA_EXTRA";
	public static final String IS_DENY_REQUEST = "IS_DENY_REQUEST";
	public static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
	public static final String IS_ADDRESS_LIST_UPDATED = "IS_ADDRESS_LIST_UPDATED";
	public static final String CAN_ADD_BILLING_ADDRESS = "CAN_ADD_BILLING_ADDRESS";
	public static final String CAN_ADD_SHIPPING_ADDRESS = "CAN_ADD_SHIPPING_ADDRESS";
	public static final String CAN_EDIT_BILLING_ADDRESS = "CAN_EDIT_BILLING_ADDRESS";
	public static final String CAN_EDIT_SHIPPING_ADDRESS = "CAN_EDIT_SHIPPING_ADDRESS";
	public static final String REQUEST_ID = "REQUEST_ID";
	public static final String IS_FROM_NOTIFICATION = "isFromNotification";
	public static final String PROVIDER_MERCHANT_DATA = "PROVIDER_MERCHANT_DATA";

	public static final int ADD_OR_MODIFY_ADDRESS_REQUEST_CODE = 1100;

	public static void goToAnagraphic(Activity activity) {
		Intent intent = new Intent(activity, BankIdAnagraphicActivity.class);
		activity.startActivity(intent);
	}

	public static void goToAddAddress(Activity activity, boolean canAddBillingAddress, boolean canAddShippingAddress, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, BankIdAddAddressActivity.class);
		intent.putExtra(CAN_ADD_BILLING_ADDRESS, canAddBillingAddress);
		intent.putExtra(CAN_ADD_SHIPPING_ADDRESS, canAddShippingAddress);
		activityResultLauncher.launch(intent);
	}

	public static void goToModifyAddress(Activity activity, BankIdAddress address, boolean canEditBillingAddress, boolean canEditShippingAddress, ActivityResultLauncher<Intent> activityResultLauncher) {
		Intent intent = new Intent(activity, BankIdAddAddressActivity.class);
		intent.putExtra(ADDRESS_DATA_EXTRA, address);
		intent.putExtra(CAN_EDIT_BILLING_ADDRESS, canEditBillingAddress);
		intent.putExtra(CAN_EDIT_SHIPPING_ADDRESS, canEditShippingAddress);
		activityResultLauncher.launch(intent);
	}

	public static void goToBankIdAuthorize(Activity activity, String requestId, BankIdMerchantData merchantData, boolean isFromNotification) {
		Intent intent = new Intent(activity, BankIdAuthorizeActivity.class);
		intent.putExtra(REQUEST_ID, requestId);
		intent.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
		intent.putExtra(PROVIDER_MERCHANT_DATA, merchantData);
		activity.startActivity(intent);
	}

	public static void goToBankIdAuthorizeNoAnimation(Activity activity, String requestId, BankIdMerchantData merchantData, boolean isFromNotification) {
		Intent intent = new Intent(activity, BankIdAuthorizeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(REQUEST_ID, requestId);
		intent.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
		intent.putExtra(PROVIDER_MERCHANT_DATA, merchantData);
		activity.startActivity(intent);
	}

	public static void goToBankIdAuthorizeResult(Activity activity, boolean isDenyBankIdRequest, String authToken, String requestId, BankIdMerchantData merchantData){
		Intent intent = new Intent(activity, BankIdAuthorizeResultActivity.class);
		intent.putExtra(IS_DENY_REQUEST, isDenyBankIdRequest);
		intent.putExtra(AUTHORIZATION_TOKEN, authToken);
		intent.putExtra(REQUEST_ID, requestId);
		intent.putExtra(PROVIDER_MERCHANT_DATA, merchantData);
		activity.startActivity(intent);
		activity.finish();
	}

}
