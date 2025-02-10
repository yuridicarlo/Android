package it.bancomatpay.sdk.core;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHelperCBC implements SecurityNetworkHelperInterface {

    private static final int SYMMETRIC_KEY_LENGTH = 16; // length in byte, i.e. 128-bit key length
    private static final int SYMMETRIC_IV_LENGTH = 16; // length in byte, i.e. 128 bits of IV (for AES)

    private int iterationCount = 1000;

    /**
     * Return iteration count for generating PBEKey
     *
     * @return the iteration count.
     */
    public int getIterationCount() {
        return iterationCount;
    }

    /**
     * Set iteration count for generating PBEKey
     * default 1000
     */
    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    /**
     * This method check the RSA message signature
     *
     * @param pem       that contains an RSA public key
     * @param message   that will be signed for check
     * @param signature the signature that will be check
     * @return true if the signature is correct false otherwise
     * @throws InvalidParameterException
     */

    public boolean checkMessageSignature(String pem, String message, String signature) throws InvalidParameterException {

        if (TextUtils.isEmpty(pem)) {
            throw new InvalidParameterException("Pem is empty");
        } else if (TextUtils.isEmpty(message)) {
            throw new InvalidParameterException("Message is empty");
        } else if (TextUtils.isEmpty(signature)) {
            throw new InvalidParameterException("Signature is empty");
        }

        try {

            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(getPublicKeyByPem(pem));
            sign.update(Conversion.stringUTF8ToByteArray(message));
            return sign.verify(Base64.decode(signature, Base64.DEFAULT));

        } catch (Exception e) {
            // verify fail, return false
            return false;
        }
    }

    /**
     * This method perform an RSA message signature
     *
     * @param pem     that contains an RSA private key
     * @param message that will be signed
     * @return the message signature
     * @throws InvalidParameterException
     */
    @Override
    public String getMessageSignature(String pem, String message) throws InvalidParameterException {

        if (TextUtils.isEmpty(pem)) {
            throw new InvalidParameterException("Pem is empty");
        } else if (TextUtils.isEmpty(message)) {
            throw new InvalidParameterException("Message is empty");
        }

        try {

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(getPrivateKeyByPem(pem));
            signature.update(Conversion.stringUTF8ToByteArray(message));
            byte[] byteSignedData = signature.sign();
            byte[] result = Base64.encode(byteSignedData, Base64.DEFAULT);

            return new String(result);
        } catch (Exception e) {
            // sign fail, we put an empty string as sign
            return "";
        }
    }

    /**
     * This method calculate the plaintext from a chiper text with RSA
     *
     * @param pem that contains an RSA private key
     * @return plain text
     * @throws InvalidParameterException
     */

    protected PublicKey getPublicKeyByPem(String pem) {

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(Conversion.stringBase64ToByteArray(pem)));
            return publicKey;
        } catch (Exception e) {
            return null;
        }

    }

    protected PrivateKey getPrivateKeyByPem(String pem) {

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Conversion.stringBase64ToByteArray(pem)));
            return privateKey;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @return new aes 256 length key
     * @throws NoSuchAlgorithmException
     */
    public SecretKey generateSimmetricKey() throws NoSuchAlgorithmException {
        // Generate a 128-bit key
        final int outputKeyLength = 128;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }


    private byte[] decrypt(byte[] key, SecurityHelperCBC.EncryptedIVAndText encryptedIvTextBytes) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); //da sistemare
        // Extract IV.
        byte[] iv = encryptedIvTextBytes.getIv();

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        // Extract encrypted part.

        byte[] encryptedBytes = encryptedIvTextBytes.getEncrypted();

        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return decrypted;
    }

    private byte[] getRawKey(byte[] seed) throws Exception {
        String password = new String(seed);
        // Store these things on disk used to derive key later:
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc

        // Use this to derive the key from the password:
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), seed, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return keyBytes;
    }

    private SecurityHelperCBC.EncryptedIVAndText encrypt(byte[] key, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        byte[] iv = cipher.getIV();
        return new SecurityHelperCBC.EncryptedIVAndText(encrypted, iv);
    }

    @Override
    public String decryptMessage(String pem, String chiperText) throws InvalidParameterException {

        if (TextUtils.isEmpty(pem)) {
            throw new InvalidParameterException("Pem is empty");
        } else if (TextUtils.isEmpty(chiperText)) {
            throw new InvalidParameterException("CipherText is empty");
        }

        try {

            byte[] chiperMessage = Base64.decode(chiperText, Base64.DEFAULT);

            if (chiperMessage.length < 255) {
                throw new InvalidParameterException("CipherMessage length < 255");
            }

            byte[] keyAndIv = Arrays.copyOfRange(chiperMessage, 0, 256);
            byte[] message = Arrays.copyOfRange(chiperMessage, 256, chiperMessage.length);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            rsaCipher.init(Cipher.DECRYPT_MODE, getPrivateKeyByPem(pem));
            // Getting key and IV for AES
            byte[] plainKeyAndIv = rsaCipher.doFinal(keyAndIv);

            byte[] key = new byte[SYMMETRIC_KEY_LENGTH];
            byte[] iv = new byte[SYMMETRIC_IV_LENGTH];

            System.arraycopy(plainKeyAndIv, 0, key, 0, SYMMETRIC_KEY_LENGTH);
            System.arraycopy(plainKeyAndIv, SYMMETRIC_KEY_LENGTH, iv, 0, SYMMETRIC_IV_LENGTH);

            return new String(decrypt(key, new SecurityHelperCBC.EncryptedIVAndText(message, iv)));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * This method calculate encrypt a plain text with RSA
     *
     * @param pem       that contains an RSA public key
     * @param plainText string that will be encrypted
     * @return chiper text
     * @throws InvalidParameterException
     */
    @Override
    public String encryptMessage(String pem, String plainText) throws InvalidParameterException {

        if (TextUtils.isEmpty(pem)) {
            throw new InvalidParameterException("Pem is empty");
        } else if (TextUtils.isEmpty(plainText)) {
            throw new InvalidParameterException("PlainText is empty");
        }

        try {
            //generate new key
            SecretKey key = generateSimmetricKey();
            SecurityHelperCBC.EncryptedIVAndText byteMessageEncrypted = encrypt(key.getEncoded(), plainText.getBytes());

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            rsaCipher.init(Cipher.ENCRYPT_MODE, getPublicKeyByPem(pem));
            byte[] keyAndIv = new byte[key.getEncoded().length + byteMessageEncrypted.getIv().length];
            System.arraycopy(key.getEncoded(), 0, keyAndIv, 0, key.getEncoded().length);
            System.arraycopy(byteMessageEncrypted.getIv(), 0, keyAndIv, key.getEncoded().length, byteMessageEncrypted.getIv().length);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(rsaCipher.doFinal(keyAndIv));
            outputStream.write(byteMessageEncrypted.getEncrypted());
            return new String(Base64.encode(outputStream.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            return "";
        }
    }

    class EncryptedIVAndText {

        byte[] encrypted;
        byte[] iv;

        public EncryptedIVAndText(byte[] encrypted, byte[] iv) {
            this.encrypted = encrypted;
            this.iv = iv;
        }

        public byte[] getEncrypted() {
            return encrypted;
        }

        public byte[] getIv() {
            return iv;
        }
    }
}

