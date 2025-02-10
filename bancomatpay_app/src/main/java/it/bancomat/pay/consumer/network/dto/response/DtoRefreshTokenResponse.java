package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

import it.bancomat.pay.consumer.network.dto.AppDtoOauthTokens;

public class DtoRefreshTokenResponse implements Serializable {

    protected AppDtoOauthTokens tokens;

    /**
     * Gets the value of the tokens property.
     * 
     * @return
     *     possible object is
     *     {@link AppDtoOauthTokens }
     *     
     */
    public AppDtoOauthTokens getTokens() {
        return tokens;
    }

    /**
     * Sets the value of the tokens property.
     * 
     * @param value
     *     allowed object is
     *     {@link AppDtoOauthTokens }
     *     
     */
    public void setTokens(AppDtoOauthTokens value) {
        this.tokens = value;
    }

}
