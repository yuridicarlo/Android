package it.bancomatpay.sdk.manager.task;

import android.app.Activity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.events.TaskEventError;
import it.bancomatpay.sdk.manager.events.request.ContactRequest;
import it.bancomatpay.sdk.manager.events.update.FrequentItemUpdate;
import it.bancomatpay.sdk.manager.events.update.UserDataUpdate;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class BackgroundSingleton implements OnCompleteListener {

    private static final String TAG = BackgroundSingleton.class.getSimpleName();

    private static BackgroundSingleton mInstance;

    public static BackgroundSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new BackgroundSingleton();
        }
        return mInstance;
    }

    public synchronized Task<?> shopListRequest(Activity activity, BcmLocation location, OnCompleteResultListener<ArrayList<ShopItem>> listener) {
        CustomLogger.d(TAG, "Start RecentItemRequest");
        return new GetShopListTask(result -> {
            CustomLogger.d(TAG, "End RecentItemRequest");
            if (result != null) {
                if (result.isSuccess()) {
                    ApplicationModel.getInstance().setShopItems(result.getResult());
                } else {
                    ApplicationModel.getInstance().setLastLocation(null);
                    ApplicationModel.getInstance().setShopItems(null);
                }

                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    listener.onComplete(result);
                }

            }
        }, location);
    }

    public synchronized void requestFrequentItem() {
        CustomLogger.d(TAG, "Start FrequentItemRequest");
        ExtendedTask<?> t = new GetUserFrequentTask(result -> {
            CustomLogger.d(TAG, "End FrequentItemRequest");
            if (result != null && result.isSuccess()) {
                ApplicationModel.getInstance().setFrequentItems(result.getResult());
                EventBus.getDefault().post(new FrequentItemUpdate(result.getResult()));
            }
        });
        t.setMasterListener(this);
        t.start();
    }

    public synchronized Task<?> userDataRequest(Activity activity, OnCompleteResultListener<UserData> listener, String sessionToken) {
        CustomLogger.d(TAG, "Start UserDataRequest");
        return new GetUserDataTask(result -> {
            CustomLogger.d(TAG, "End UserDataRequest");
            if (result != null && result.isSuccess()) {
                ApplicationModel.getInstance().setUserData(result.getResult());
            }
            EventBus.getDefault().post(new UserDataUpdate(result));

            if (!activity.isFinishing() && !activity.isDestroyed()) {
                listener.onComplete(result);
            }

        }, sessionToken);
    }

    public synchronized Task<?> contactRequest(Activity activity, final ContactRequest event, String sessionToken,
                                               OnCompleteResultListener<SyncPhoneBookData> listener) {
        CustomLogger.d(TAG, "Start ContactRequest");
        if (!event.isOneShotMode() && (ApplicationModel.getInstance().getContactItems() == null || event.isForced())) {
            return syncPhoneBook(activity, listener, false, sessionToken);
        } else {
            if (event.isOneShotMode()) {
                return syncPhoneBook(activity, listener, true, sessionToken);
            } else {
                return new CachedPhoneBookTask(result -> {
                    if (result != null && result.isSuccess()) {
                        ApplicationModel.getInstance().setContactItems(result.getResult());
                    }
                });
            }
        }
    }

    public synchronized Task<?> syncPhoneBook(Activity activity, OnCompleteResultListener<SyncPhoneBookData> listener, boolean isOneshotMode, String sessionToken) {
        return new SyncPhoneBookTask(result -> {
            if (result != null && result.isSuccess()) {
                CustomLogger.d(TAG, "End ContactRequest");
                ApplicationModel.getInstance().setContactItems(result.getResult().getContactItems());
            }

            if (!activity.isFinishing() && !activity.isDestroyed()) {
                listener.onComplete(result);
            }

        }, sessionToken);
    }

    @Override
    public void onComplete(Task<?> task) {
        //
    }

    @Override
    public void onCompleteWithError(Task<?> task, Error e) {
        //le chiamate in background non devono sollevare il popup di assenza rete sarebbero troppe
        if (!(e instanceof HttpError)) {
            EventBus.getDefault().post(new TaskEventError(task, e));
        }
    }

}
