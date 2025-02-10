package it.bancomat.pay.consumer.biometric.callable.simmetric;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.biometric.BiometricManager;

import java.security.KeyStore;
import java.util.concurrent.Callable;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomatpay.sdk.core.PayCore;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

public class GetSecretKey implements Callable<SecretKey> {

	@Override
	public SecretKey call() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
		keyStore.load(null);

		SecretKey secretKey = (SecretKey) keyStore.getKey(SecurityConstants.BIOMETRIC_KEY_AES_ALIAS,null);

		if (secretKey == null) {
			KeyGenerator keyPairGenerator = KeyGenerator.getInstance(SecurityConstants.S_BIO_KEY_ALGORITHM, SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
			KeyGenParameterSpec.Builder specBuilder = new KeyGenParameterSpec.Builder(SecurityConstants.BIOMETRIC_KEY_AES_ALIAS,
					KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
					.setBlockModes(SecurityConstants.S_BIO_BLOCK_MODE)
					.setKeySize(256)
					.setEncryptionPaddings(SecurityConstants.S_BIO_PADDING);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				specBuilder.setUserAuthenticationParameters(0,
						KeyProperties.AUTH_BIOMETRIC_STRONG | KeyProperties.AUTH_DEVICE_CREDENTIAL);
			} else {
				specBuilder.setUserAuthenticationValidityDurationSeconds(SecurityConstants.AUTHENTICATION_DURATION_SECONDS);
			}

			int canAuthenticate = BiometricManager.from(PayCore.getAppContext()).canAuthenticate(BIOMETRIC_STRONG);
			specBuilder.setUserAuthenticationRequired(canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS);

			keyPairGenerator.init(specBuilder.build());
			return keyPairGenerator.generateKey();
		} else {
			return secretKey;
		}
	}

}
