package it.bancomat.pay.consumer.network.totp;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

class PSKCSecurityHandler {

    private static String signAlgorithm = "HmacSHA512";
    private static String chipherAlgorithm = "AES/CBC/NoPadding";
    private static String secretKeyFactoryAlgorithm = "PBKDF2WithHmacSHA1";
    private static int secretKeyAlgorithKeyLength = 256;
    private static String secretKeySpecAlgorithm = "AES";

    private static final String TAG = PSKCSecurityHandler.class.getSimpleName();

    static SecretKey extractDerivedKeyWithPassword(String password, byte[] salt, int iteratorCounter) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyFactoryAlgorithm); //"PBKDF2WithHmacSHA1"
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iteratorCounter, secretKeyAlgorithKeyLength); // 256
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), secretKeySpecAlgorithm); //AES
        CustomLogger.d(TAG, "key: " + new String(Base64.encode(key.getEncoded(), Base64.NO_WRAP)));
        return key;
    }

    static byte[] generateRandomValue(int length) {
        SecureRandom random = new SecureRandom();
        byte[] buffer = new byte[length];
        random.nextBytes(buffer);
        return buffer;
    }

    static byte[] signData(byte[] data, SecretKey hmacKey) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] output;
        Mac hmac = Mac.getInstance(signAlgorithm);
        hmac.init(hmacKey);
        output = hmac.doFinal(data);
        return output;

    }

    static byte[] encryptData(byte[] data, SecretKey derivedKey, byte[] ivData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(chipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, derivedKey, new IvParameterSpec(ivData));
        return cipher.doFinal(data);
    }

    static byte[] decryptData(byte[] data, SecretKey derivedKey, byte[] ivData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(chipherAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, derivedKey, new IvParameterSpec(ivData));
        return cipher.doFinal(data);
    }

}
