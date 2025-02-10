package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoOauthTokens;

public class DtoModifyPinResponse implements Serializable {

    protected String pskc;
    protected DtoOauthTokens tokens;
    protected String lastAttempts;

    public String getLastAttempts() {
        return lastAttempts;
    }

    public void setLastAttempts(String lastAttempts) {
        this.lastAttempts = lastAttempts;
    }

    /**
     * Gets the value of the pskc property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPskc() {
        return pskc;
    }

    /**
     * Sets the value of the pskc property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPskc(String value) {
        this.pskc = value;
    }

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
