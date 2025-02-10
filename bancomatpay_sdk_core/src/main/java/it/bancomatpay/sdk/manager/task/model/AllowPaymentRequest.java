package it.bancomatpay.sdk.manager.task.model;

public class AllowPaymentRequest {

    private boolean forP2P;
    private boolean forP2B;

    public boolean isForP2P() {
        return forP2P;
    }

    public void setForP2P(boolean forP2P) {
        this.forP2P = forP2P;
    }

    public boolean isForP2B() {
        return forP2B;
    }

    public void setForP2B(boolean forP2B) {
        this.forP2B = forP2B;
    }

}
