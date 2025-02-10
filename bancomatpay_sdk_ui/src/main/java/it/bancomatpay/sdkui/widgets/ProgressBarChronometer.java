package it.bancomatpay.sdkui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import it.bancomatpay.sdkui.model.NotificationPaymentItem;

public class ProgressBarChronometer extends ProgressBar {

    private NotificationPaymentItem mItem;
    private long mExpirationTimeStamp;
    private long mStartTimeStamp;
    private long mMaxTimeValidity;
   /* private Handler handler;
    private Runnable runnable;*/

    public ProgressBarChronometer(Context context) {
        super(context);
    }

    public ProgressBarChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setItem(NotificationPaymentItem item) {
        this.mItem = item;
        setMaxValidityInterval();
    }

    private void setMaxValidityInterval() {
        mExpirationTimeStamp = mItem.getNotificationPaymentData().getPaymentItem().getExpirationDate().getTime();
        mStartTimeStamp = mItem.getNotificationPaymentData().getPaymentItem().getPaymentDate().getTime();
        mMaxTimeValidity = mExpirationTimeStamp - mStartTimeStamp;
        start();
    }

    private long getMaxTimeValidity() {
        return mMaxTimeValidity;
    }

    private long getRemainingTime() {
        return mExpirationTimeStamp - System.currentTimeMillis();

    }

    private int calculateTimePercentage() {
        return (int) ((getRemainingTime() * 100) / getMaxTimeValidity());
    }


    private synchronized void start() {
      /*  handler = new android.os.Handler();
        runnable = () -> {*/
        setProgress(calculateTimePercentage());
/*            handler.postDelayed(runnable, 500);
        };
        handler.postDelayed(runnable, 500);*/
    }
}
