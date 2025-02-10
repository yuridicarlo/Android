package it.bancomat.pay.consumer.biometric;

import android.security.keystore.KeyProperties;

/**
 * Helper class, contains several constants used when encrypting/decrypting data on Android.
 * This class should not be considered a complete list of the algorithms, keystore types,
 * or signature types within the Android Platform, only the more common ones.
 */
public class SecurityConstants {
    public static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";

    public static final String TYPE_AES = "AES";
    public static final String TYPE_RSA = "RSA";

    public static final String CHIPER_AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding";
    public static final String CHIPER_RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1PADDING";

    public static final String BIOMETRIC_KEY_RSA_ALIAS = "a";

    public static final String BIO_KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA;
    public static final String BIO_BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB;
    public static final String BIO_PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;
    public static final String BIO_TRANSFORMATION_RSA = BIO_KEY_ALGORITHM + "/" + BIO_BLOCK_MODE + "/" + BIO_PADDING;



    public static final String BIOMETRIC_KEY_AES_ALIAS = "b";
    
    public static final String S_BIO_KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    public static final String S_BIO_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;
    public static final String S_BIO_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    public static final String S_BIO_TRANSFORMATION = S_BIO_KEY_ALGORITHM + "/" + S_BIO_BLOCK_MODE + "/" + S_BIO_PADDING;

    public static final int AUTHENTICATION_DURATION_SECONDS = 3;
}
