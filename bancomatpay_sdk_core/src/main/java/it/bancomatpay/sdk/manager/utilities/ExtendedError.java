package it.bancomatpay.sdk.manager.utilities;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public class ExtendedError extends Error {

    private StatusCodeInterface statusCodeInterface;

    public ExtendedError(Throwable cause, StatusCodeInterface statusCodeInterface) {
        super(cause);
        this.statusCodeInterface = statusCodeInterface;
    }

    public StatusCodeInterface getStatusCodeInterface() {
        return statusCodeInterface;
    }
}
