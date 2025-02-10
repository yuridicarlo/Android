package it.bancomat.pay.consumer.network;

import android.content.Context;

import it.bancomat.pay.consumer.network.dto.AppDtoHandleRequest;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;

public interface AppMessageEnrichment {

    /**
     * Add some mobile related data to message
     *
     * @param message the message to be enrich
     * @param ctx
     * @return
     */
    AppDtoHandleRequest doEnrichment(DtoAppRequest<?> message, Context ctx);

    class Factory {
        public static AppMessageEnrichment create() {
            return new AppMobileInfoEnrichment();
        }
    }

}
