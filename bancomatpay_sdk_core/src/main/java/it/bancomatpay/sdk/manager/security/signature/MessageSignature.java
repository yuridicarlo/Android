package it.bancomatpay.sdk.manager.security.signature;

import android.content.Context;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.Constants;

public interface MessageSignature {

	/**
	 * Calculate message signature
	 * @param message
	 * @return
	 */
	String getMessageSignature(String message, Context context) throws InvalidParameterException;
	
	
	class Factory{
		
		public static MessageSignature create(Cmd cmd){
			if(Constants.MESSAGE_SIGN_ENABLED){
				try {
					return (MessageSignature) cmd.messageSignatureClass.newInstance();
				} catch (Exception e) {
					return new NoMessageSignature();
				}
			}else{
				return new NoMessageSignature();
			}
		}
	}
}
