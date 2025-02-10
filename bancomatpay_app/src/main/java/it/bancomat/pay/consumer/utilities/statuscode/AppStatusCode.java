package it.bancomat.pay.consumer.utilities.statuscode;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public class AppStatusCode extends StatusCode {

    public enum ServerExtended implements StatusCodeInterface {

        AUTH_FAILED_PIN_VALIDATION,
        AUTH_FAILED_HMAC_NOT_EQUAL,
        PIN_LOCKED;

        @Override
        public boolean isSuccess() {
            return false;
        }

    }

}

