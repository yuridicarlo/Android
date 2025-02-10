package it.bancomatpay.sdk.manager.network.dto.request;

public class DtoVerifyPaymentStateRequest {

    private String paymentId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
