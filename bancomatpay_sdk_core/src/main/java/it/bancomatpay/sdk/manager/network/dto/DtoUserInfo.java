package it.bancomatpay.sdk.manager.network.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DtoUserInfo implements Serializable {

    protected String userId;
    protected String jsessionClient;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJsessionClient() {
        return jsessionClient;
    }

    public void setJsessionClient(String jsessionClient) {
        this.jsessionClient = jsessionClient;
    }

    @NonNull
    @Override
    public String toString() {
        return "DtoUserInfo{" +
                "userId='" + userId + '\'' +
                ", jsessionClient='" + jsessionClient + '\'' +
                '}';
    }

}
