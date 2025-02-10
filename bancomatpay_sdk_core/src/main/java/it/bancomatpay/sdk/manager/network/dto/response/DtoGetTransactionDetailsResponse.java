package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoGetTransactionDetailsResponse implements Serializable {

    protected String iban;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
