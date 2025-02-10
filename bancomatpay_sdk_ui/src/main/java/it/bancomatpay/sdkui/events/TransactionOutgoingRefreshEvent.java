package it.bancomatpay.sdkui.events;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;

public class TransactionOutgoingRefreshEvent {

	private Result<OutgoingPaymentRequestData> result;

	public TransactionOutgoingRefreshEvent(Result<OutgoingPaymentRequestData> result) {
		this.result = result;
	}

	public Result<OutgoingPaymentRequestData> getResult() {
		return result;
	}

	public void setResult(Result<OutgoingPaymentRequestData> result) {
		this.result = result;
	}

}
