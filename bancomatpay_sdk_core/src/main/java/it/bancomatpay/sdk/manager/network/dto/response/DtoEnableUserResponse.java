package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;


public class DtoEnableUserResponse implements Serializable {

    protected String userAccountId;

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String value) {
        this.userAccountId = value;
    }

}
