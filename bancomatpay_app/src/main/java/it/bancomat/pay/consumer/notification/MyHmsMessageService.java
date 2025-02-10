package it.bancomat.pay.consumer.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import it.bancomat.pay.consumer.SplashActivity;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.AppTokens;
import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;

public class MyHmsMessageService extends HmsMessageService {

    private static final String TAG = MyHmsMessageService.class.getSimpleName();


    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        CustomLogger.d(TAG, "Refreshed Hms Message token: " + refreshedToken);
        AppTokens tokens = AppBancomatDataManager.getInstance().getTokens();
        String sessionToken = tokens != null ? tokens.getOauth() : "";
        BancomatPayApiInterface.Factory.getInstance().doSubscribePush(null, refreshedToken, sessionToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        CustomLogger.d(TAG, "remoteMessage: " + remoteMessage.toString());
        // super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {

            Map<String, String> pushMap = remoteMessage.getDataOfMap();
            CustomLogger.d(TAG, "Notification Message Body: " + remoteMessage.getDataOfMap().toString());
            Push push = new Push();
            if (pushMap.containsKey(PushConstant.TYPE_KEY)) {
                push.setType(Push.Type.valueOf(pushMap.get(PushConstant.TYPE_KEY)));
            }
            push.setMessage(pushMap.get(PushConstant.MESSAGE_KEY));
            push.setTitle(pushMap.get(PushConstant.TITLE_KEY));
            if (pushMap.containsKey(PushConstant.PAYMENT_ID_KEY)) {
                push.setPaymentRequestId(pushMap.get(PushConstant.PAYMENT_ID_KEY));
            }
            if (pushMap.containsKey(PushConstant.PAYMENT_REQUEST_TYPE)) {
                push.setPaymentRequestType(PaymentRequestType.valueOf(pushMap.get(PushConstant.PAYMENT_REQUEST_TYPE)));
            }
            push.setShowDialog(false);

            if (EventBus.getDefault().hasSubscriberForEvent(Push.class)
                    && (push.getType() == Push.Type.PAYMENT_REQUEST || push.getType() == Push.Type.BANKID || push.getType() == Push.Type.DIRECT_DEBIT)) {

                EventBus.getDefault().post(push);

            } else {

                Intent intent = new Intent(this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(PushConstant.EXTRA_PUSH, push);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PushConstant.MAIN_CHANNEL_ID);

                Bitmap largeIcon = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(this.getResources(), it.bancomatpay.sdkui.R.drawable.ico_logo_notifica),
                        (int) getResources().getDimension(android.R.dimen.notification_large_icon_width),
                        (int) getResources().getDimension(android.R.dimen.notification_large_icon_height),
                        false);

                builder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(it.bancomatpay.sdkui.R.drawable.ico_push_new)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(push.getTitle())
                        .setContentText(push.getMessage())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(push.getMessage()))
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

                builder.setContentIntent(contentIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(PushConstant.MAIN_CHANNEL_ID,
                            PushConstant.NOTIFICATION_CHANNEL_TITLE,
                            NotificationManager.IMPORTANCE_DEFAULT);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                if (notificationManager != null) {
                    notificationManager.notify(NotificationID.getID(), builder.build());
                }

            }

        }
    }

    public static void registerCurrentToken(Activity activity) {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = new AGConnectOptionsBuilder().build(activity).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(activity).getToken(appId, "HCM");
                    CustomLogger.i(TAG, "get token:" + token);
                    if (!TextUtils.isEmpty(token)) {
                        AppTokens tokens = AppBancomatDataManager.getInstance().getTokens();
                        String sessionToken = tokens != null ? tokens.getOauth() : "";
                        BancomatPayApiInterface.Factory.getInstance().doSubscribePush(
                                result -> {
                                    if (result != null) {
                                        if (result.isSuccess()) {
                                            CustomLogger.d(TAG, "doSubscribePush success!");
                                        } else if (result.isSessionExpired()) {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbortSession(activity, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                        }
                                    }
                                }, token, sessionToken);
                        CustomLogger.d(TAG, "Refreshed HmsService token: " + token);
                    } else {
                        CustomLogger.e(TAG, "HmsServiceId getInstanceId token is NULL");
                    }

                } catch (ApiException e) {
                    CustomLogger.e(TAG, "get token failed, " + e);
                }
            }
        }
        .start();
    }


}
