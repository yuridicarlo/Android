package it.bancomat.pay.consumer.biometric.callable;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.biometric.BiometricManager;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.biometric.SecurityConstants;
import it.bancomatpay.sdk.core.PayCore;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

public class GetPublicKey implements Callable<PublicKey> {

	@Override
	public PublicKey call() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
		keyStore.load(null);

		PrivateKey privateKey = (PrivateKey) keyStore.getKey(SecurityConstants.BIOMETRIC_KEY_RSA_ALIAS,null);
		Certificate certificate = null;

		if (privateKey != null) {
			certificate = keyStore.getCertificate(SecurityConstants.BIOMETRIC_KEY_RSA_ALIAS);
		}

		PublicKey publicKey;

		if (certificate == null) {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SecurityConstants.BIO_KEY_ALGORITHM, SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
			KeyGenParameterSpec.Builder specBuilder = new KeyGenParameterSpec.Builder(SecurityConstants.BIOMETRIC_KEY_RSA_ALIAS,
					KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
					.setBlockModes(SecurityConstants.BIO_BLOCK_MODE)
					.setKeySize(2048)
					.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1)
					.setEncryptionPaddings(SecurityConstants.BIO_PADDING)
					.setRandomizedEncryptionRequired(true);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				specBuilder.setUserAuthenticationParameters(0,
						KeyProperties.AUTH_BIOMETRIC_STRONG | KeyProperties.AUTH_DEVICE_CREDENTIAL);
			} else {
				specBuilder.setUserAuthenticationValidityDurationSeconds(SecurityConstants.AUTHENTICATION_DURATION_SECONDS);
			}

			int canAuthenticate = BiometricManager.from(PayCore.getAppContext()).canAuthenticate(BIOMETRIC_STRONG);
			specBuilder.setUserAuthenticationRequired(canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS);

			keyPairGenerator.initialize(specBuilder.build());
			publicKey = keyPairGenerator.generateKeyPair().getPublic();
		} else {
			publicKey = certificate.getPublicKey();
		}

		return KeyFactory.getInstance(publicKey.getAlgorithm())
				.generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
	}

}
