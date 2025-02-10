package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

public class DtoGetAuthorizationTokenResponse implements Serializable {

    protected String lastAttempts;
    protected String authorizationToken;
    protected String loyaltyToken;

    public String getLastAttempts() {
        return lastAttempts;
    }

    public void setLastAttempts(String lastAttempts) {
        this.lastAttempts = lastAttempts;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getLoyaltyToken() {
        return loyaltyToken;
    }

    public void setLoyaltyToken(String loyaltyToken) {
        this.loyaltyToken = loyaltyToken;
    }

}
