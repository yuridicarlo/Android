package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;


public class DtoEnableUserRequest implements Serializable {

    protected String msisdn;
    protected String iban;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
