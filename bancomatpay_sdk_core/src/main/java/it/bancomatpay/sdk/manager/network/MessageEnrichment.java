package it.bancomatpay.sdk.manager.network;

import android.content.Context;

import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;
import it.bancomatpay.sdk.manager.network.dto.request.DtoHandleRequest;

public interface MessageEnrichment {

	/**
	 * Add some mobile related data to message 
	 * @param message the message to be enrich
	 * @param ctx
	 * @return
	 */
	DtoHandleRequest doEnrichment(DtoAppRequest<?> message, Context ctx);
	
	class Factory{
		
		public static MessageEnrichment create(){
			return new MobileInfoEnrichment();
		}
	}
}
