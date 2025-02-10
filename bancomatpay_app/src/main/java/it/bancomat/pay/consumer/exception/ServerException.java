package it.bancomat.pay.consumer.exception;

import androidx.annotation.Nullable;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public class ServerException extends Exception {

    private Result result;

    public ServerException(Result result) {
        this.result = result;
    }

    public boolean isSessionExpired() {
        return result.getStatusCode() == StatusCode.Http.UNAUTHORIZED;
    }

    public StatusCodeInterface getStatusCode() {
        return result.getStatusCode();
    }

    public String getStatusCodeDetail() {
        return result.getStatusCodeDetail();
    }

    public String getStatusCodeMessage() {
        return result.getStatusCodeMessage();
    }

    public Result getResult() {
        return result;
    }

    @Nullable
    @Override
    public String getMessage() {
        return "ServerException: " + result.toString();

    }
}
