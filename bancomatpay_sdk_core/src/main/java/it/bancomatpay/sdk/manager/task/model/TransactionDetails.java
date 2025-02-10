package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class TransactionDetails implements Serializable {

    protected String iban;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
