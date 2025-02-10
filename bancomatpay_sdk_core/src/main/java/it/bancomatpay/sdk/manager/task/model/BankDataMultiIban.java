package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoInstrument;

public class BankDataMultiIban implements Serializable {

    protected String bankUUID;
    protected boolean multiIban;
    protected List<DtoInstrument> instruments;

    public String getBankUUID() {
        return bankUUID;
    }

    public void setBankUUID(String bankUUID) {
        this.bankUUID = bankUUID;
    }

    public boolean isMultiIban() {
        return multiIban;
    }

    public void setMultiIban(boolean multiIban) {
        this.multiIban = multiIban;
    }

    public List<DtoInstrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<DtoInstrument> instruments) {
        this.instruments = instruments;
    }

}
