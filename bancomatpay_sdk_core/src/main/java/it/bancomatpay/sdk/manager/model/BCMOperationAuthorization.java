package it.bancomatpay.sdk.manager.model;

import java.io.Serializable;

public class BCMOperationAuthorization implements Serializable {

    private AuthenticationOperationType operation;
    private String paymentId;
    private String amount;
    private String sender;
    private String receiver;
    private boolean isP2B;

    public AuthenticationOperationType getOperation() {
        return operation;
    }

    public void setOperation(AuthenticationOperationType operation) {
        this.operation = operation;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isP2B() {
        return isP2B;
    }

    public void setP2B(boolean p2B) {
        isP2B = p2B;
    }

}
