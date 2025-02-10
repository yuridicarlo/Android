package it.bancomatpay.sdk.core;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class SecurityPreferenceHelper implements SecurityStorageHelperInterface {

    private final static String SHARED_PREFERENCE = "sharedPreference";
    private final static String AES_KEY = "aesKey";
    private final static String TAG = SecurityPreferenceHelper.class.getSimpleName();
    private static final String ALIAS_PRE_23 = "aliasPre23";
    private static final String ALIAS_POST_23 = "aliaPost23";

    private String aesKeyEncrypted;
    private KeyStore keyStore;
    private Context context;

    public SecurityPreferenceHelper() {
        try {

            this.context = PayCore.getAppContext();
            keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            // Create a KeyGenParameterSpec builder and
            // set the alias and different purposes of the key

            // END_INCLUDE(create_keypair)

            // BEGIN_INCLUDE(create_spec)
            // The KeyPairGeneratorSpec object is how parameters for your key pair are passed
            // to the KeyPairGenerator.
            AlgorithmParameterSpec spec;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                KeyPairGenerator kpGenerator = KeyPairGenerator
                        .getInstance(SecurityConstants.TYPE_RSA,
                                SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

                keyStore.load(null);

                if (!keyStore.containsAlias(ALIAS_PRE_23) || TextUtils.isEmpty(context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(AES_KEY, ""))) {

                    Calendar start = new GregorianCalendar();
                    Calendar end = new GregorianCalendar();
                    end.add(Calendar.YEAR, 100);
                    // Below Android M, use the KeyPairGeneratorSpec.Builder.

                    spec = new KeyPairGeneratorSpec.Builder(context)
                            // You'll use the alias later to retrieve the key.  It's a key for the key!
                            .setAlias(ALIAS_PRE_23)
                            // The subject used for the self-signed certificate of the generated pair
                            .setSubject(new X500Principal("CN=" + ALIAS_PRE_23))
                            // The serial number used for the self-signed certificate of the
                            // generated pair.
                            .setSerialNumber(BigInteger.valueOf(1337))
                            // Date range of validity for the generated pair.
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();

                    kpGenerator.initialize(spec);

                    KeyPair kp = kpGenerator.generateKeyPair();
                    // END_INCLUDE(create_spec)
                    CustomLogger.d(TAG, "Public Key is: " + kp.getPublic().toString());

                    KeyGenerator keyGenerator = KeyGenerator.getInstance(SecurityConstants.TYPE_AES);
                    keyGenerator.init(128);
                    SecretKey key = keyGenerator.generateKey();


                    Cipher rsaCipher = Cipher.getInstance(SecurityConstants.CHIPER_RSA_ECB_PKCS1PADDING);
                    rsaCipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
                    byte[] encrypted = rsaCipher.doFinal(key.getEncoded());
                    aesKeyEncrypted = new String(Base64.encode(encrypted, Base64.DEFAULT));

                    context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE).edit().putString(AES_KEY, aesKeyEncrypted).apply();
                    // The KeyGenParameterSpec is how parameters for your key are passed to the
                    // generator. Choose wisely!
                }

            } else {

                KeyGenerator kpGenerator = KeyGenerator
                        .getInstance(SecurityConstants.TYPE_AES,
                                SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

                keyStore.load(null);
                if (!keyStore.containsAlias(ALIAS_POST_23)) {
                    // On Android M or above, use the KeyGenparameterSpec.Builder and specify permitted
                    // properties  and restrictions of the key.
                    spec = new KeyGenParameterSpec.Builder(
                            ALIAS_POST_23,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setKeySize(256)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build();

                    kpGenerator.init(spec);
                    // Set the alias of the entry in Android KeyStore where the key will appear
                    // and the constrains (purposes) in the constructor of the Builder

                    kpGenerator.generateKey();
                }
            }


        } catch (Exception e) {
            CustomLogger.e(TAG, "Init error: ", e);

        }
    }

    public void delete() {
        try {
            keyStore.deleteEntry(ALIAS_POST_23);
            keyStore.deleteEntry(ALIAS_PRE_23);
        } catch (KeyStoreException ignored) {

        }
    }

    public SecretKey getAESKey() throws Exception {
        KeyStore ks = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
        // to call "load", or it'll crash.
        ks.load(null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            KeyStore.Entry entry = ks.getEntry(ALIAS_PRE_23, null);

            Cipher rsaCipher = Cipher.getInstance(SecurityConstants.CHIPER_RSA_ECB_PKCS1PADDING);
            rsaCipher.init(Cipher.DECRYPT_MODE, ((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
            if (TextUtils.isEmpty(aesKeyEncrypted)) {
                aesKeyEncrypted = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(AES_KEY, "");
            }
            byte[] raw = Base64.decode(aesKeyEncrypted, Base64.DEFAULT);
            byte[] rte = rsaCipher.doFinal(raw);

            return new SecretKeySpec(rte, SecurityConstants.TYPE_AES);
        } else {

            return (SecretKey) keyStore.getKey(ALIAS_POST_23, null);
        }
    }

    @Override
    public String encrypt(byte[] seed, String plainText) throws Exception {

        byte[] clean = plainText.getBytes();

        SecretKey aesKey = getAESKey();
        // Encrypt.
        Cipher aesCipher = Cipher.getInstance(SecurityConstants.CHIPER_AES_CBC_PKCS7PADDING);

        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = aesCipher.doFinal(clean);
        byte[] iv = aesCipher.getIV();
        // Combine IV and encrypted part.
        byte[] encryptedIVAndText = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedIVAndText, iv.length, encrypted.length);

        return new String(Base64.encode(encryptedIVAndText, Base64.DEFAULT));

    }

    @Override
    public String decrypt(byte[] seed, String encryptedIvTextString) throws Exception {

        Cipher cipherDecrypt = Cipher.getInstance(SecurityConstants.CHIPER_AES_CBC_PKCS7PADDING);
        int ivSize = cipherDecrypt.getBlockSize();

        byte[] encryptedIvTextBytes = Base64.decode(encryptedIvTextString, Base64.DEFAULT);
        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

        // Hash key.
        //SecretKey key = (SecretKey) keyStore.getKey(ALIAS, null);
        //da usare per decrifare la aes
        // Decrypt.


        SecretKey aesKey = getAESKey();


        cipherDecrypt.init(Cipher.DECRYPT_MODE, aesKey, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        return new String(decrypted);
    }

}

