package it.bancomatpay.sdk.manager.utilities;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public class ExtendedException extends RuntimeException {

    private StatusCodeInterface statusCodeInterface;

    public ExtendedException(StatusCodeInterface statusCodeInterface){
        this.statusCodeInterface = statusCodeInterface;
    }

    public StatusCodeInterface getStatusCodeInterface() {
        return statusCodeInterface;
    }
}

