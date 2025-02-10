package it.bancomatpay.sdk.manager.security.encryption;

import android.content.Context;

public class NoMessageEncryption implements MessageEncryption, MessageDecryption {

	@Override
	public String getEncryptedMessage(String plainText) {
		return plainText;
	}

	@Override
	public String getDecryptedMessage(String chiperText, Context context) {
		return chiperText;
	}

}
