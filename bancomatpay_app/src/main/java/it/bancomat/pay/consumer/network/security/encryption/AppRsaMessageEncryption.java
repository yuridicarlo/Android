package it.bancomat.pay.consumer.network.security.encryption;

import android.content.Context;
import android.text.TextUtils;

import java.security.InvalidParameterException;

import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomatpay.sdk.manager.security.SecurityHelperWrapper;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;


public class AppRsaMessageEncryption implements AppMessageEncryption, AppMessageDecryption {

	@Override
	public String getEncryptedMessage(String plainText) {
		String pem = AppConstants.PUBLIC_KEY;
		return SecurityHelperWrapper.encryptMessage(pem, plainText);
	}

	@Override
	public String getDecryptedMessage(String chiperText, Context context) {

		AppBancomatDataManager bcmDataManager = AppBancomatDataManager.getInstance();
		String pem = bcmDataManager.getMobileDevice().getKey();

		String decryptedMessage = "";
		try {
			decryptedMessage = SecurityHelperWrapper.decryptMessage(pem, chiperText);
		} catch (InvalidParameterException e) {
			if (TextUtils.isEmpty(pem)) {
				CustomLogger.e("AppMessageCipherException", "Pem is empty");
			} else if (TextUtils.isEmpty(chiperText)) {
				CustomLogger.e("AppMessageCipherException", "CipherText is empty");
			}
		}

		return decryptedMessage;
	}

}
