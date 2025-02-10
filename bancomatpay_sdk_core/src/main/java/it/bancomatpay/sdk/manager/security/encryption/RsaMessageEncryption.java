package it.bancomatpay.sdk.manager.security.encryption;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;


public class RsaMessageEncryption implements MessageEncryption, MessageDecryption {

	@Override
	public String getEncryptedMessage(String plainText) {
		String pem = Constants.PUBLIC_KEY;
		return SecurityHelperWrapper.encryptMessage(pem, plainText);
	}

	@Override
	public String getDecryptedMessage(String chiperText, Context context) {

		BancomatDataManager bcmDataManager = BancomatDataManager.getInstance();
		String pem = bcmDataManager.getMobileDevice().getKey();

		String decryptedMessage = "";
		try {
			decryptedMessage = SecurityHelperWrapper.decryptMessage(pem, chiperText);
		} catch (InvalidParameterException e) {
			if (TextUtils.isEmpty(pem)) {
				CustomLogger.e("MessageCipherException", "Pem is empty");
			} else if (TextUtils.isEmpty(chiperText)) {
				CustomLogger.e("MessageCipherException", "CipherText is empty");
			}
		}

		return decryptedMessage;
	}

}
