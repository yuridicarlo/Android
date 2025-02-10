package it.bancomat.pay.consumer.network.dto;

public enum PinOperationType {

    LOGIN_WITH_PIN,
    LOGIN_WITHOUT_PIN,
    VERIFY_PIN;

    public String value() {
        return name();
    }

    public static PinOperationType fromValue(String v) {
        return valueOf(v);
    }

}
