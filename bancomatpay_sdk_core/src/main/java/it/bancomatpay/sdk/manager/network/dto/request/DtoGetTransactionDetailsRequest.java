package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoGetTransactionDetailsRequest implements Serializable {

    protected String paymentId;
    protected boolean isP2P;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isP2p() {
        return isP2P;
    }

    public void setP2p(boolean p2p) {
        isP2P = p2p;
    }

}
