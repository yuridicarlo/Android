package it.bancomat.pay.consumer.activation;



public class PendingPaymentsRefreshEvent {

    private String paymentId;
    private boolean success;

    public PendingPaymentsRefreshEvent(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }


}
