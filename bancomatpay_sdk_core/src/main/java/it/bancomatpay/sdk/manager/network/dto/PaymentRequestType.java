package it.bancomatpay.sdk.manager.network.dto;

public enum PaymentRequestType {

    BANKID,
    P2P,
    P2B,
    ALL,
    DIRECT_DEBIT;

    public String value() {
        return name();
    }

    public static PaymentRequestType fromValue(String v) {
        return valueOf(v);
    }

}
