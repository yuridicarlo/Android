package it.bancomat.pay.consumer.notification;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;

public class Push implements Serializable {

    public enum Type {
        INFO,  //nessuna azione
        PAYMENT_REQUEST, //se ci troviamo in home mostriamo schermata accettazione, altrimenti popoup che al click mostra la schermata di accettazione
        PAYMENTS_HISTORY,
        BANKID,
        DIRECT_DEBIT
    }

    private String message;
    private String paymentRequestId;
    private String title;
    private Type type;
    private boolean showDialog;
    private PaymentRequestType paymentRequestType;

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getMessage() {
        return message;
    }

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public PaymentRequestType getPaymentRequestType() {
        return paymentRequestType;
    }

    public void setPaymentRequestType(PaymentRequestType paymentRequestType) {
        this.paymentRequestType = paymentRequestType;
    }

    @Override
    public String toString() {
        return "Push{" +
                "message='" + message + '\'' +
                ", paymentRequestId='" + paymentRequestId + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", showDialog=" + showDialog +
                ", paymentRequestType=" + paymentRequestType +
                '}';
    }
}
