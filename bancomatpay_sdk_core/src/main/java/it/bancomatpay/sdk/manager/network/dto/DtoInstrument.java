package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoInstrument implements Serializable {

    protected String iban;
    protected String cipheredIban;
    protected boolean isDefaultIncoming;
    protected boolean isDefaultOutgoing;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getCipheredIban() {
        return cipheredIban;
    }

    public void setCipheredIban(String cipheredIban) {
        this.cipheredIban = cipheredIban;
    }

    public boolean isDefaultIncoming() {
        return isDefaultIncoming;
    }

    public void setDefaultIncoming(boolean defaultIncoming) {
        isDefaultIncoming = defaultIncoming;
    }

    public boolean isDefaultOutgoing() {
        return isDefaultOutgoing;
    }

    public void setDefaultOutgoing(boolean defaultOutgoing) {
        isDefaultOutgoing = defaultOutgoing;
    }

}
