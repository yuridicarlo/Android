package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoInstrument;

public class InstrumentData implements Serializable {

    public enum EHeaderType {
        HEADER_SEND_MONEY,
        HEADER_GET_MONEY,
        HEADER_OTHER_BANK
    }

    public enum EIbanCategory {
        IBAN_SEND_MONEY,
        IBAN_GET_MONEY
    }

    private DtoInstrument instrument;
    private EHeaderType eHeaderType;
    private EIbanCategory eIbanCategory;

    public InstrumentData() {
        instrument = new DtoInstrument();
    }

    public DtoInstrument getInstrument() {
        return instrument;
    }

    public InstrumentData(DtoInstrument other) {
        this.instrument = other;
    }

    public EHeaderType getHeaderType() {
        return eHeaderType;
    }

    public void setHeaderType(EHeaderType headerType) {
        eHeaderType = headerType;
    }

    public EIbanCategory getIbanCategory() {
        return eIbanCategory;
    }

    public void setIbanCategory(EIbanCategory ibanCategory) {
        this.eIbanCategory = ibanCategory;
    }

    public String getIban() {
        return instrument.getIban();
    }

    public void setIban(String iban) {
        instrument.setIban(iban);
    }

    public String getCipheredIban() {
        return instrument.getCipheredIban();
    }

    public void setCipheredIban(String cipheredIban) {
        instrument.setCipheredIban(cipheredIban);
    }

    public boolean isDefaultIncoming() {
        return instrument.isDefaultIncoming();
    }

    public void setDefaultIncoming(boolean defaultIncoming) {
        instrument.setDefaultIncoming(defaultIncoming);
    }

    public boolean isDefaultOutgoing() {
        return instrument.isDefaultOutgoing();
    }

    public void setDefaultOutgoing(boolean defaultOutgoing) {
        instrument.setDefaultOutgoing(defaultOutgoing);
    }

}
