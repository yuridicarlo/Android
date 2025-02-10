package it.bancomatpay.sdk.manager.security.encryption;

import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.Constants;

public interface MessageEncryption {


	/**
	 * Encrypt message
	 * @param plainText
	 * @return
	 */
	String getEncryptedMessage(String plainText);
	
	class Factory{
		
		public static MessageEncryption create(Cmd cmd){
			if(Constants.MESSAGE_ENCRYPTION_ENABLED){
				try {
					return (MessageEncryption) cmd.messageEncryptionClass.newInstance();
				} catch (Exception e) {
					return new NoMessageEncryption();
				}
			}
			return new NoMessageEncryption();

		}
	}
}
