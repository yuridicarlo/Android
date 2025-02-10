package it.bancomat.pay.consumer.network.security.encryption;

import android.content.Context;

public class AppNoMessageEncryption implements AppMessageEncryption, AppMessageDecryption {

	@Override
	public String getEncryptedMessage(String plainText) {
		return plainText;
	}

	@Override
	public String getDecryptedMessage(String chiperText, Context context) {
		return chiperText;
	}

}
