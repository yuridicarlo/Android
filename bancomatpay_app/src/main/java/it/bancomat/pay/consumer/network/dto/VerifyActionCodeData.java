package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

public class VerifyActionCodeData implements Serializable {

    private String activationCode;
    private String token;
    private String bankUUID;
    private String msisdn;
    private String maskedMsisdn;
    private String msisdnAreaCode;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBankUUID() {
        return bankUUID;
    }

    public void setBankUUID(String bankUUID) {
        this.bankUUID = bankUUID;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMaskedMsisdn() {
        return maskedMsisdn;
    }

    public void setMaskedMsisdn(String maskedMsisdn) {
        this.maskedMsisdn = maskedMsisdn;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getMsisdnAreaCode() {
        return msisdnAreaCode;
    }

    public void setMsisdnAreaCode(String msisdnAreaCode) {
        this.msisdnAreaCode = msisdnAreaCode;
    }
}
