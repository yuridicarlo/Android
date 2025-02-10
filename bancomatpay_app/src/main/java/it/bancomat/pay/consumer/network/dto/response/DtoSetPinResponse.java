package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

import it.bancomat.pay.consumer.network.dto.AppDtoOauthTokens;

public class DtoSetPinResponse implements Serializable {

    protected String pskc;
    protected AppDtoOauthTokens tokens;
    protected boolean outgoingIbanSet;
    protected String abiCode;
    protected String groupCode;
    protected String loyaltyToken;

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

    public AppDtoOauthTokens getTokens() {
        return tokens;
    }

    public void setTokens(AppDtoOauthTokens tokens) {
        this.tokens = tokens;
    }

    public boolean isOutgoingIbanSet() {
        return outgoingIbanSet;
    }

    public void setOutgoingIbanSet(boolean outgoingIbanSet) {
        this.outgoingIbanSet = outgoingIbanSet;
    }

    public String getAbiCode() {
        return abiCode;
    }

    public void setAbiCode(String abiCode) {
        this.abiCode = abiCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getLoyaltyToken() {
        return loyaltyToken;
    }

    public void setLoyaltyToken(String loyaltyToken) {
        this.loyaltyToken = loyaltyToken;
    }
}
