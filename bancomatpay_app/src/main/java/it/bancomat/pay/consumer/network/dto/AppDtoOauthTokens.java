package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoOauthToken;


public class AppDtoOauthTokens implements Serializable {

    protected DtoOauthToken authorizationToken;

    protected DtoOauthToken refreshToken;

    /**
     * Gets the value of the authorizationToken property.
     *
     * @return possible object is
     * {@link DtoOauthToken }
     */
    public DtoOauthToken getAuthorizationToken() {
        return authorizationToken;
    }

    /**
     * Sets the value of the authorizationToken property.
     *
     * @param value allowed object is
     *              {@link DtoOauthToken }
     */
    public void setAuthorizationToken(DtoOauthToken value) {
        this.authorizationToken = value;
    }

    /**
     * Gets the value of the refreshToken property.
     *
     * @return possible object is
     * {@link DtoOauthToken }
     */
    public DtoOauthToken getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets the value of the refreshToken property.
     *
     * @param value allowed object is
     *              {@link DtoOauthToken }
     */
    public void setRefreshToken(DtoOauthToken value) {
        this.refreshToken = value;
    }

}
