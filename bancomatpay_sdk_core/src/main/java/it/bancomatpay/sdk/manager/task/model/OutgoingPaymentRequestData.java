package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.List;

public class OutgoingPaymentRequestData implements Serializable {

    private List<TransactionDataOutgoing> paymentRequest;

    public List<TransactionDataOutgoing> getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(List<TransactionDataOutgoing> paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

}
