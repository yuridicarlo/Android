package it.bancomatpay.sdk.manager.security.signature;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.Constants;

public interface MessageSignatureVerify {

	/**
	 * Check if the signature is correct
	 * @param message
	 * @param signature
	 * @return
	 */
	boolean checkMessageSignature(String message, String signature) throws InvalidParameterException;
	
	class Factory{
		
		public static MessageSignatureVerify create(Cmd cmd){
			if(Constants.MESSAGE_VERIFICATION_ENABLED){
				try {
					return (MessageSignatureVerify) cmd.messageSignatureVerifyClass.newInstance();
				} catch (Exception e) {
					return new NoMessageSignature();
				}

			}else{
				return new NoMessageSignature();
			}
		}
	}
}
