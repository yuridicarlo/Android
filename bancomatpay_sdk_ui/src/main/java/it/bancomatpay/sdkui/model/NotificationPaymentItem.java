package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.NotificationPaymentData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.BitmapCache;

public class NotificationPaymentItem implements HomeNotificationData {

    private NotificationPaymentData notificationPaymentData;

    public NotificationPaymentItem(NotificationPaymentData notificationPaymentData) {
        this.notificationPaymentData = notificationPaymentData;
    }

    @Override
    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        if (notificationPaymentData.getItem() != null) {
            try {
                Uri uri = null;
                if (notificationPaymentData.getItem() instanceof ShopItem) {
                    uri = Uri.parse(((ShopItem) notificationPaymentData.getItem()).getInsignia());
                } else if (notificationPaymentData.getItem() instanceof ContactItem) {
                    uri = Uri.parse(notificationPaymentData.getItem().getImage());
                }
                bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
            } catch (Exception e) {
                if (TextUtils.isEmpty(notificationPaymentData.getItem().getTitle())) {
                    bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(), R.drawable.placeholder_contact_list);
                }
            }
        } else {
            try {
                byte[] decodedString = new byte[0];
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(), R.drawable.placeholder_merchant);
            }
        }
        return bitmap;
    }

    @Override
    public String getLetter() {
        if (TextUtils.isEmpty(notificationPaymentData.getPaymentItem().getInsignia())) {
            return "#";
        } else {
            return notificationPaymentData.getPaymentItem().getInsignia().substring(0, 1);
        }
    }

    public String getInitials() {
        if (notificationPaymentData.getPaymentItem().getInsignia().contains(" ")) {
            String[] nameList = notificationPaymentData.getPaymentItem().getInsignia().split(" ");
            if (TextUtils.isEmpty(nameList[1])) {
                return getLetter();
            } else if (nameList[1].length() >= 1) {
                return getLetter() + nameList[1].substring(0, 1);
            }
        }
        return getLetter();
    }

    public NotificationPaymentData getNotificationPaymentData() {
        return notificationPaymentData;
    }

}
