package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.request.DtoP2PPaymentRequest;


public class DtoGetOutgoingPaymentRequestResponse implements Serializable {

    protected List<DtoP2PPaymentRequest> paymentRequests;

    public List<DtoP2PPaymentRequest> getPaymentRequest() {
        return paymentRequests;
    }

    public void setPaymentRequest(List<DtoP2PPaymentRequest> paymentRequest) {
        this.paymentRequests = paymentRequest;
    }

}
