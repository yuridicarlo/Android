package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdkui.activities.ChooseAmountActivity;
import it.bancomatpay.sdkui.activities.ConfirmPaymentActivity;
import it.bancomatpay.sdkui.activities.HomeActivity;
import it.bancomatpay.sdkui.activities.PaymentAcceptanceActivity;
import it.bancomatpay.sdkui.activities.PaymentDataLoadingActivity;
import it.bancomatpay.sdkui.activities.ResultPaymentActivity;
import it.bancomatpay.sdkui.activities.SaveContactNumberActivity;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;

import static it.bancomatpay.sdkui.flowmanager.ExitAppFlowManager.FINISH_SDK_FLOW;
import static it.bancomatpay.sdkui.flowmanager.HomeFlowManager.CASHBACK_SHOW_INFO_DIALOG;

import androidx.activity.result.ActivityResultLauncher;

public class PaymentFlowManager {

    public static final String PAYMENT_DATA = "paymentData";
    public static final String HIDDEN_NAME = "hiddenName";
    public static final String OPERATION_TYPE = "operationType";
    public static final String SHOP_LOCATION = "shopLocation";
    public static final String QR_DATA = "qrData";
    public static final String QR_DATA_ID = "qrDataId";

    public static final String INSERT_AMOUNT = "insertAmount";
    public static final String PAYMENT_FROM_DEEP_LINK = "paymentFromDeepLink";
    public static final String IS_FROM_NOTIFICATION = "isFromNotification";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static String PAYMENT_CONTACT_FLOW_TYPE = "payment_contact_flow_type";
    public static String CAN_CHANGE_FLOW_TYPE = "can_change_flow_type";
    public static final int REQUEST_CODE_SAVE_CONTACT = 1000;

    public static void goToInsertAmount(Activity activity, DisplayData consumerDisplayData, boolean isNewTask, PaymentContactFlowType flowType, boolean canChangeFlowType) {
        Intent i = new Intent(activity, ChooseAmountActivity.class);
        i.putExtra(INSERT_AMOUNT, consumerDisplayData);
        i.putExtra(PAYMENT_CONTACT_FLOW_TYPE, flowType);
        i.putExtra(CAN_CHANGE_FLOW_TYPE, canChangeFlowType);
        if (isNewTask) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(i);
    }

    public static void goToQrPaymentData(Activity activity, QrCodeDetailsData qrData, boolean isNewTask) {
        Intent i = new Intent(activity, PaymentDataLoadingActivity.class);
        i.putExtra(QR_DATA, qrData);
        if (isNewTask) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(PAYMENT_FROM_DEEP_LINK, true);
        }
        activity.startActivity(i);
    }

    public static void goToQrPaymentData(Activity activity, String qrDataId, boolean isNewTask) {
        Intent i = new Intent(activity, PaymentDataLoadingActivity.class);
        i.putExtra(QR_DATA_ID, qrDataId);
        if (isNewTask) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(PAYMENT_FROM_DEEP_LINK, true);
        }
        activity.startActivity(i);
    }

    public static void goToConfirm(Activity activity, AbstractPaymentData paymentData, boolean isSendMoney, boolean isNewTask) {
        Intent i = new Intent(activity, ConfirmPaymentActivity.class);
        i.putExtra(PAYMENT_DATA, paymentData);
        i.putExtra(OPERATION_TYPE, isSendMoney);
        if (isNewTask) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(i);
        if (paymentData instanceof ConsumerPaymentData) {
            activity.finish();
        }
    }

    public static void goToAcceptance(Activity activity, AbstractPaymentData paymentData, boolean isFromNotification) {
        Intent i = new Intent(activity, PaymentAcceptanceActivity.class);
        i.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
        i.putExtra(PAYMENT_DATA, paymentData);
        activity.startActivity(i);
    }

    public static void goToAcceptanceNoAnimation(Activity activity, AbstractPaymentData paymentData, boolean isFromNotification) {
        Intent i = new Intent(activity, PaymentAcceptanceActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
        i.putExtra(PAYMENT_DATA, paymentData);
        activity.startActivity(i);
    }

    public static void goToHome(Activity activity, boolean finishSdkFlow) {
        Intent i = new Intent(activity, HomeActivity.class);
        if (finishSdkFlow) {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(FINISH_SDK_FLOW, true);
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        i.putExtra(CASHBACK_SHOW_INFO_DIALOG, false);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goToResultPayment(Activity activity, AbstractPaymentData paymentData, String hiddenName,
                                         boolean isSendMoney, boolean isFromDeepLink, boolean isFromNotification) {
        Intent i = new Intent(activity, ResultPaymentActivity.class);
        i.putExtra(PAYMENT_DATA, paymentData);
        i.putExtra(HIDDEN_NAME, hiddenName);
        i.putExtra(OPERATION_TYPE, isSendMoney);
        i.putExtra(PAYMENT_FROM_DEEP_LINK, isFromDeepLink);
        i.putExtra(IS_FROM_NOTIFICATION, isFromNotification);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goToResultPayment(Activity activity, AbstractPaymentData paymentData, BcmLocation shopLocation, String hiddenName,
                                         boolean isSendMoney, boolean isFromDeepLink) {
        Intent i = new Intent(activity, ResultPaymentActivity.class);
        i.putExtra(PAYMENT_DATA, paymentData);
        i.putExtra(SHOP_LOCATION, shopLocation);
        i.putExtra(HIDDEN_NAME, hiddenName);
        i.putExtra(OPERATION_TYPE, isSendMoney);
        i.putExtra(PAYMENT_FROM_DEEP_LINK, isFromDeepLink);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goToSaveContact(Activity activity, String phoneNumber, ActivityResultLauncher<Intent> activityResultLauncher) {
        Intent i = new Intent(activity, SaveContactNumberActivity.class);
        i.putExtra(PHONE_NUMBER, phoneNumber);
        activityResultLauncher.launch(i);
    }
}
