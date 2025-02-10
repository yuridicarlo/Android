package it.bancomatpay.sdk.manager.model;

public class BCMTransaction {

    private String paymentId;
    private boolean isP2p;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isP2p() {
        return isP2p;
    }

    public void setP2p(boolean p2p) {
        isP2p = p2p;
    }

}
