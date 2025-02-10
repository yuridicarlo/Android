package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;
import it.bancomatpay.sdkui.activities.directdebit.DirectDebitAuthorizeActivity;
import it.bancomatpay.sdkui.activities.directdebit.DirectDebitAuthorizeResultActivity;
import it.bancomatpay.sdkui.activities.directdebit.DirectDebitMerchantDetailActivity;
import it.bancomatpay.sdkui.activities.directdebit.DirectDebitMerchantListActivity;
import it.bancomatpay.sdkui.model.DirectDebit;

public class DirectDebitFlowManager {

	public static final String IS_DENY_REQUEST = "IS_DENY_REQUEST";
	public static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
	public static final String DIRECT_DEBIT_REQUEST_DATA = "DIRECT_DEBIT_REQUEST_DATA";
	public static final String DIRECT_DEBIT_HISTORY_DATA = "DIRECT_DEBIT_MERCHANT_DATA";
	public static final String IS_FROM_NOTIFICATION = "isFromNotification";

	public static void goToDirectDebitAuthorizeResult(Activity activity, boolean isDenyDirectDebitRequest, String authToken, DirectDebitRequest directDebitRequest, boolean isFromNotification){
		Intent intent = new Intent(activity, DirectDebitAuthorizeResultActivity.class);
		intent.putExtra(IS_DENY_REQUEST, isDenyDirectDebitRequest);
		intent.putExtra(AUTHORIZATION_TOKEN, authToken);
		intent.putExtra(DIRECT_DEBIT_REQUEST_DATA, directDebitRequest);
		intent.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
		activity.startActivity(intent);
		activity.finish();
	}

	public static void goToDirectDebitMerchantList(Activity activity){
		Intent intent = new Intent(activity, DirectDebitMerchantListActivity.class);
		activity.startActivity(intent);
	}

	public static void goToDirectDebitAuthorize(Activity activity, DirectDebitRequest directDebitRequest, boolean isFromNotification){
		Intent intent = new Intent(activity, DirectDebitAuthorizeActivity.class);
		intent.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
		intent.putExtra(DIRECT_DEBIT_REQUEST_DATA, directDebitRequest);
	    activity.startActivity(intent);
	}

	public static void goToDirectDebitAuthorizeNoAnimation(Activity activity, DirectDebitRequest directDebitRequest, boolean isFromNotification){
		Intent intent = new Intent(activity, DirectDebitAuthorizeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
		intent.putExtra(DIRECT_DEBIT_REQUEST_DATA, directDebitRequest);
		activity.startActivity(intent);
	}

	public static void goToDetailDirectDebit(Activity activity, DirectDebit item){
		Intent intent = new Intent(activity, DirectDebitMerchantDetailActivity.class);
		intent.putExtra(DIRECT_DEBIT_HISTORY_DATA, item);
		activity.startActivity(intent);
	}

}
