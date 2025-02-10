package it.bancomatpay.sdkui.events;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;

public class TransactionRefreshEvent {

	private Result<?> result;

	public TransactionRefreshEvent(Result<PaymentHistoryData> result) {
		this.result = result;
	}

	public Result<?> getResult() {
		return result;
	}

	public void setResult(Result<?> result) {
		this.result = result;
	}

}
