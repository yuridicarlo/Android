package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoRefreshTokenResponse implements Serializable {

    protected DtoOauthTokens tokens;

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

}
