package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdkui.activities.atmcardless.AtmCardlessChooseAmountActivity;
import it.bancomatpay.sdkui.activities.atmcardless.AtmCardlessChooseAmountManuallyActivity;
import it.bancomatpay.sdkui.activities.atmcardless.AtmCardlessResultActivity;
import it.bancomatpay.sdkui.activities.atmcardless.AtmCardlessScanQrCodeActivity;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;

public class AtmCardlessFlowManager {

	public static final String ATM_PAYMENT_ITEM_EXTRA = "ATM_PAYMENT_ITEM_EXTRA";
	public static final String ATM_PAYMENT_SHOP_DATA = "ATM_PAYMENT_SHOP_DATA";
	public static final String ATM_PAYMENT_CENTS_AMOUNT = "ATM_PAYMENT_CENTS_AMOUNT";
	public static final String IS_FROM_SERVICE_FRAGMENT = "IS_FROM_SERVICE_FRAGMENT";

	public static void goToScanQrCode(Activity activity) {
		Intent intent = new Intent(activity, AtmCardlessScanQrCodeActivity.class);
		activity.startActivity(intent);
	}

	public static void goToChooseAmount(Activity activity, MerchantQrPaymentData paymentData, boolean isFromServiceFragment) {
		Intent intent = new Intent(activity, AtmCardlessChooseAmountActivity.class);
		intent.putExtra(ATM_PAYMENT_ITEM_EXTRA, paymentData);
		intent.putExtra(IS_FROM_SERVICE_FRAGMENT, isFromServiceFragment);
		activity.startActivity(intent);
	}

	public static void goToChooseAmountManually(Activity activity, MerchantQrPaymentData paymentData, ShopsDataMerchant merchantItem, boolean isFromServiceFragment) {
		Intent intent = new Intent(activity, AtmCardlessChooseAmountManuallyActivity.class);
		intent.putExtra(ATM_PAYMENT_ITEM_EXTRA, paymentData);
		intent.putExtra(ATM_PAYMENT_SHOP_DATA, merchantItem);
		intent.putExtra(IS_FROM_SERVICE_FRAGMENT, isFromServiceFragment);
		activity.startActivity(intent);
	}

	public static void goToAtmCardlessResult(Activity activity, MerchantQrPaymentData paymentData, ShopsDataMerchant merchantItem, String centsAmount, boolean isFromServiceFragment) {
		Intent intent = new Intent(activity, AtmCardlessResultActivity.class);
		intent.putExtra(ATM_PAYMENT_ITEM_EXTRA, paymentData);
		intent.putExtra(ATM_PAYMENT_SHOP_DATA, merchantItem);
		intent.putExtra(ATM_PAYMENT_CENTS_AMOUNT, centsAmount);
		intent.putExtra(IS_FROM_SERVICE_FRAGMENT, isFromServiceFragment);
		activity.startActivity(intent);
	}

}
