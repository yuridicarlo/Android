package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoOauthTokens;

public class DtoLoginResponse implements Serializable {

    protected DtoOauthTokens tokens;
    protected String lastAttempts;
    protected String loyaltyToken;

    /**
     * Gets the value of the tokens property.
     *
     * @return possible object is
     * {@link DtoOauthTokens }
     */
    public DtoOauthTokens getTokens() {
        return tokens;
    }

    /**
     * Sets the value of the tokens property.
     *
     * @param value allowed object is
     *              {@link DtoOauthTokens }
     */
    public void setTokens(DtoOauthTokens value) {
        this.tokens = value;
    }

    /**
     * Gets the value of the numberOfAttempts property.
     *
     * @return possible object is
     * {@link String }
     */

    public String getLastAttempts() {
        return lastAttempts;
    }

    public void setLastAttempts(String lastAttempts) {
        this.lastAttempts = lastAttempts;
    }

    public String getLoyaltyToken() {
        return loyaltyToken;
    }

    public void setLoyaltyToken(String loyaltyToken) {
        this.loyaltyToken = loyaltyToken;
    }

}
