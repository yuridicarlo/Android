//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.07.13 at 03:12:47 PM CEST 
//


package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;


public class DtoOauthTokens implements Serializable {


    protected DtoOauthToken authorizationToken;

    protected DtoOauthToken refreshToken;

    /**
     * Gets the value of the authorizationToken property.
     * 
     * @return
     *     possible object is
     *     {@link DtoOauthToken }
     *     
     */
    public DtoOauthToken getAuthorizationToken() {
        return authorizationToken;
    }

    /**
     * Sets the value of the authorizationToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtoOauthToken }
     *     
     */
    public void setAuthorizationToken(DtoOauthToken value) {
        this.authorizationToken = value;
    }

    /**
     * Gets the value of the refreshToken property.
     * 
     * @return
     *     possible object is
     *     {@link DtoOauthToken }
     *     
     */
    public DtoOauthToken getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets the value of the refreshToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtoOauthToken }
     *     
     */
    public void setRefreshToken(DtoOauthToken value) {
        this.refreshToken = value;
    }

}
