package it.bancomat.pay.consumer.cj;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

import static it.bancomatpay.sdkui.utilities.CjConstants.CJ_INTENT_DATA_EXTRA;

public class CustomerJourneyIntentService extends JobIntentService {

    private static final String TAG = CustomerJourneyIntentService.class.getSimpleName();

    static final int CJ_TAG_EVENT_JOB_ID = 9999;

    public static void enqueueWork(Context context, CustomerJourneyTag tagEvent) {
        Intent intent = new Intent(context, CustomerJourneyIntentService.class);
        intent.putExtra(CJ_INTENT_DATA_EXTRA, tagEvent);
        enqueueWork(context, CustomerJourneyIntentService.class, CJ_TAG_EVENT_JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CustomLogger.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        CustomLogger.d(TAG, "onHandleIntent");

        CustomerJourneyTag tag = (CustomerJourneyTag) intent.getSerializableExtra(CJ_INTENT_DATA_EXTRA);

        CustomLogger.d(TAG, "doSetCustomerJourneyTag call for " + tag.getTagKey());
        BancomatPayApiInterface.Factory.getInstance().doSetCustomerJourneyTag(
                result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            CustomLogger.d(TAG, "doSetCustomerJourneyTag success!");
                        } else {
                            CustomLogger.e(TAG, "doSetCustomerJourneyTag on error: " + result.getStatusCode());
                        }
                    } else {
                        CustomLogger.e(TAG, "doSetCustomerJourneyTag on network error");
                    }
                }, tag);

        CustomLogger.d(TAG, "finished");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomLogger.d(TAG, "onDestroy");
    }

}