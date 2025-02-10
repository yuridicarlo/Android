package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

public class DtoVerifyActivationCodeResponse implements Serializable {

    private String token;
    private String bankUUID;
    private String msisdn;
    private String maskedMsisdn;
    private String msisdnAreaCode;
    private String userId;

    public String getBankUUID() {
        return bankUUID;
    }

    public void setBankUUID(String bankUUID) {
        this.bankUUID = bankUUID;
    }

    /**
     * Gets the value of the token property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setToken(String value) {
        this.token = value;
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

    public String getMsisdnAreaCode() {
        return msisdnAreaCode;
    }

    public void setMsisdnAreaCode(String msisdnAreaCode) {
        this.msisdnAreaCode = msisdnAreaCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
