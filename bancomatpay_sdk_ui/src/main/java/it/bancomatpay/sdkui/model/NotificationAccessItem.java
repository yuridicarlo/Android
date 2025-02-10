package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.BankIdRequest;
import it.bancomatpay.sdkui.R;

public class NotificationAccessItem implements HomeNotificationData {

    private BankIdRequest notificationAccessData;

    public NotificationAccessItem(BankIdRequest notificationAccessData) {
        this.notificationAccessData = notificationAccessData;
    }

    @Override
    public Bitmap getBitmap() {
        return BitmapFactory.decodeResource(PayCore.getAppContext().getResources(), R.drawable.placeholder_merchant);
    }

    @Override
    public String getLetter() {
        if (TextUtils.isEmpty(notificationAccessData.getBankIdMerchantData().getMerchantName())) {
            return "#";
        } else {
            return notificationAccessData.getBankIdMerchantData().getMerchantName();
        }
    }

    public String getInitials() {
        if (notificationAccessData.getBankIdMerchantData().getMerchantName().contains(" ")) {
            String[] nameList = notificationAccessData.getBankIdMerchantData().getMerchantName().split(" ");
            if (TextUtils.isEmpty(nameList[1])) {
                return getLetter();
            } else if (nameList[1].length() >= 1) {
                return getLetter() + nameList[1].substring(0, 1);
            }
        }
        return getLetter();
    }

    public BankIdRequest getNotificationAccessData() {
        return notificationAccessData;
    }

}
