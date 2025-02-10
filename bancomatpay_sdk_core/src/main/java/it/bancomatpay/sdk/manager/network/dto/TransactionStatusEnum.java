package it.bancomatpay.sdk.manager.network.dto;

public enum TransactionStatusEnum {

    INV,
    ANN_P2P,
    PND,
    RIC,
    PAG,
    ANN_P2B,
    STR,
    ADD,
    ATM,
    ANN_ATM,
    POS,
    ANN_POS,
    CASHBACK;

    public String value() {
        return name();
    }

    public static TransactionStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}
