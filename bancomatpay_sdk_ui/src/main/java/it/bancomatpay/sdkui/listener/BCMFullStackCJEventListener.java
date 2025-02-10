package it.bancomatpay.sdkui.listener;

import android.content.Context;

import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;

public interface BCMFullStackCJEventListener {
	void onSendCJTagEvent(Context context, CustomerJourneyTag tagEvent);
}
