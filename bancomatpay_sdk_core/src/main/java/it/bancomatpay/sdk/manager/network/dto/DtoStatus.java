package it.bancomatpay.sdk.manager.network.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DtoStatus implements Serializable {

    protected String statusCode;
    protected String statusDetailCode;

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusDetailCode(String statusDetailCode) {
        this.statusDetailCode = statusDetailCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusDetailCode() {
        return statusDetailCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "DtoStatus{" +
                "statusCode='" + statusCode + '\'' +
                ", statusDetailCode='" + statusDetailCode + '\'' +
                '}';
    }

}
