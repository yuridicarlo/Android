package it.bancomatpay.sdk.manager.network.dto;

public enum PaymentStateType {

    EXECUTED,
    PENDING,
    SENT,
    WAIT,
    AUTHORIZED,
    FAILED;

    public String value() {
        return name();
    }

    public static PaymentStateType fromValue(String v) {
        return valueOf(v);
    }

}
