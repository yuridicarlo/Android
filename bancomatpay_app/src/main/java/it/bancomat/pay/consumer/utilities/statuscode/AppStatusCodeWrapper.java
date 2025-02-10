package it.bancomat.pay.consumer.utilities.statuscode;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public enum AppStatusCodeWrapper {

    SCS_0000(AppStatusCode.Mobile.OK),
    SCS_0005_0002(AppStatusCode.ServerExtended.PIN_LOCKED),
    SCS_0005_0004(AppStatusCode.ServerExtended.AUTH_FAILED_PIN_VALIDATION),
    SCS_0005_0006(AppStatusCode.ServerExtended.AUTH_FAILED_HMAC_NOT_EQUAL),
    SCS_0005_0012(AppStatusCode.ServerExtended.PIN_LOCKED),

    SCS_9999_9999(StatusCode.Server.GENERIC_SERVER_ERROR);

    StatusCodeInterface statusCodeWrapped;

    AppStatusCodeWrapper(StatusCodeInterface statusCodeWrapped) {
        this.statusCodeWrapped = statusCodeWrapped;
    }

    public StatusCodeInterface getStatusCodeWrapped() {
        return statusCodeWrapped;
    }

}
