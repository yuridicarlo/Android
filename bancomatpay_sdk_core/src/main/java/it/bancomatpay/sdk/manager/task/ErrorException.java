package it.bancomatpay.sdk.manager.task;

public class ErrorException extends Exception{

    Error error;

    public ErrorException(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
