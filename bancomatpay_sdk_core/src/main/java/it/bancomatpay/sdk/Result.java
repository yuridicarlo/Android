package it.bancomatpay.sdk;

import androidx.annotation.NonNull;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public class Result<T> implements Serializable {

    private T result;
    private StatusCodeInterface statusCode;
    private String statusCodeDetail;
    private String statusCodeMessage;

    public boolean isSuccess() {
        return statusCode.isSuccess();
    }

    public boolean isSessionExpired() {
        return statusCode == StatusCode.Http.UNAUTHORIZED;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public StatusCodeInterface getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodeInterface statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCodeDetail() {
        return statusCodeDetail;
    }

    public void setStatusCodeDetail(String statusCodeDetail) {
        this.statusCodeDetail = statusCodeDetail;
    }

    public String getStatusCodeMessage() {
        return statusCodeMessage;
    }

    public void setStatusCodeMessage(String statusCodeMessage) {
        this.statusCodeMessage = statusCodeMessage;
    }

    @NonNull
    @Override
    public String toString() {
        return "Result{" +
                "result=" + result +
                ", statusCode=" + statusCode +
                ", statusCodeDetail='" + statusCodeDetail + '\'' +
                ", statusCodeMessage='" + statusCodeMessage + '\'' +
                '}';
    }

}
