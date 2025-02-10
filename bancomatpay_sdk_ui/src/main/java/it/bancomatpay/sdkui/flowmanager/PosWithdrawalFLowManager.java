package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdkui.activities.pos.PosWithdrawalDataLoadingActivity;
import it.bancomatpay.sdkui.activities.pos.PosWithdrawalResultActivity;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;

public class PosWithdrawalFLowManager {

    public static final String QR_DATA = "qrData";
    public static final String IS_FROM_SERVICE_FRAGMENT = "isFromServiceFragment";
    public static final String WITHDRAWAL_PAYMENT_ITEM_EXTRA = "WITHDRAWAL_PAYMENT_ITEM_EXTRA";

    public static void goToPosWithdrawalPaymentData(Activity activity, QrCodeDetailsData qrData, boolean isFromServiceFragment) {
        Intent i = new Intent(activity, PosWithdrawalDataLoadingActivity.class);
        i.putExtra(QR_DATA, qrData);
        i.putExtra(IS_FROM_SERVICE_FRAGMENT, isFromServiceFragment);
        activity.startActivity(i);
    }

    public static void goToPosWithdrawalResult(Activity activity, MerchantQrPaymentData paymentData, boolean isFromServiceFragment) {
        Intent intent = new Intent(activity, PosWithdrawalResultActivity.class);
        intent.putExtra(WITHDRAWAL_PAYMENT_ITEM_EXTRA, paymentData);
        intent.putExtra(IS_FROM_SERVICE_FRAGMENT, isFromServiceFragment);
        activity.startActivity(intent);
    }
}
