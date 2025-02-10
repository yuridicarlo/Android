package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoGenerateNewOtpRequest implements Serializable {

    protected String activationCode;
    protected String token;

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
