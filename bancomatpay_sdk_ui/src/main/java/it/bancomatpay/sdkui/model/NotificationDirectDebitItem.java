package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;

import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;

public class NotificationDirectDebitItem implements HomeNotificationData{

    private DirectDebitRequest notificationdirectDebitData;

    public NotificationDirectDebitItem(DirectDebitRequest notificationdirectDebitData) {
        this.notificationdirectDebitData = notificationdirectDebitData;
    }


    public DirectDebitRequest getNotificationdirectDebitData() {
        return notificationdirectDebitData;
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public String getLetter() {
        return null;
    }
}
