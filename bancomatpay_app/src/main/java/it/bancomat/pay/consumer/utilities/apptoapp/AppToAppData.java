package it.bancomat.pay.consumer.utilities.apptoapp;

public class AppToAppData {

    private String bankUuid;
    private String activationCode;
    private String token;

    public String getBankUuid() {
        return bankUuid;
    }

    public void setBankUuid(String bankUuid) {
        this.bankUuid = bankUuid;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
