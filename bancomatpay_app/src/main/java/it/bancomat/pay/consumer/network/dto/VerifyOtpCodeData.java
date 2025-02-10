package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

import it.bancomat.pay.consumer.activation.databank.DataBank;

public class VerifyOtpCodeData implements Serializable {

    private DataBank dataBank;
    private String token;

    public boolean isMultiIbanBank() {
        return dataBank.getInstrument() != null && dataBank.getInstrument().size() > 1;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DataBank getDataBank() {
        return dataBank;
    }

    public void setDataBank(DataBank dataBank) {
        this.dataBank = dataBank;
    }
}
