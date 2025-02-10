package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoSetOutgoingDefaultInstrumentRequest implements Serializable {

    protected String iban;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
