package it.bancomat.pay.consumer.network.totp;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.Pskc;

public class PSKCManager {

    private int SALT_BYTE_LENGTH = 16;
    private int IV_SEED_BYTE_LENGTH = 16;
    private int ENCRYPT_OTP_MULTIPLE_BYTE = 16;
    private static PSKCManager instance;

    private Pskc pskc;

    public static PSKCManager getInstance() {
        if (instance == null) {
            instance = new PSKCManager();
            instance.pskc = AppBancomatDataManager.getInstance().getPskc();
        }
        return instance;
    }

    public void setPskc(Pskc pskc) {
        this.pskc = pskc;
    }

    public SecretKey getPinDerivedKeyFrom(String pin) throws InvalidKeySpecException, NoSuchAlgorithmException {

        byte[] saltData = Base64.decode(pskc.getSalt(), Base64.NO_WRAP);
        int itCounter = pskc.getIteratorCounter();
        return PSKCSecurityHandler.extractDerivedKeyWithPassword(pin, saltData, itCounter);
    }


    public byte[] getSeedWithPinDerivedKey(SecretKey derivedKey) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] cipherData = Base64.decode(pskc.getKeyPackageCipherValue(), Base64.NO_WRAP);
        byte[] ivData = Arrays.copyOfRange(cipherData, 0, IV_SEED_BYTE_LENGTH);
        byte[] cSeedData = Arrays.copyOfRange(cipherData, IV_SEED_BYTE_LENGTH, cipherData.length);
        return PSKCSecurityHandler.decryptData(cSeedData, derivedKey, ivData);
    }

    public byte[] getHmacKeyWithPinDerivedKey(SecretKey derivedKey) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] cipherData = Base64.decode(pskc.getMacMethodCipValue(), Base64.NO_WRAP);
        byte[] ivData = Arrays.copyOfRange(cipherData, 0, IV_SEED_BYTE_LENGTH);
        byte[] hmacData = Arrays.copyOfRange(cipherData, IV_SEED_BYTE_LENGTH, cipherData.length);

        return PSKCSecurityHandler.decryptData(hmacData, derivedKey, ivData);
    }

    public String encryptString(String clearText, SecretKey toptDerivedKey, SecretKey hmacKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        byte[] clearData = addPaddingIfNeeded(clearText).getBytes(StandardCharsets.UTF_8);
        clearData = getArray(clearData);
        ByteArrayOutputStream finalData = new ByteArrayOutputStream();

        byte[] ivData = PSKCSecurityHandler.generateRandomValue(IV_SEED_BYTE_LENGTH);
        byte[] encryptedData = PSKCSecurityHandler.encryptData(clearData, toptDerivedKey, ivData);
        byte[] signData = PSKCSecurityHandler.signData(encryptedData, hmacKey);


        finalData.write(signData);
        finalData.write(ivData);
        finalData.write(encryptedData);
        return new String(Base64.encode(finalData.toByteArray(), Base64.NO_WRAP));

    }

    private byte[] getArray(byte[] array) {
        if (array.length % ENCRYPT_OTP_MULTIPLE_BYTE == 0) {
            return array;
        } else {
            int length = (array.length / ENCRYPT_OTP_MULTIPLE_BYTE + 1) * ENCRYPT_OTP_MULTIPLE_BYTE;
            byte[] padding = new byte[length];
            System.arraycopy(array, 0, padding, 0, array.length);
            return padding;
        }
    }

    private String addPaddingIfNeeded(String data) {
        if (data.length() % ENCRYPT_OTP_MULTIPLE_BYTE != 0) {
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            SecureRandom random = new SecureRandom();

            StringBuilder dataBuilder = new StringBuilder(data);
            while (dataBuilder.length() % ENCRYPT_OTP_MULTIPLE_BYTE != 0) {
                int randomCharIndex = random.nextInt(chars.length);
                dataBuilder.append(chars[randomCharIndex]);
            }
            data = dataBuilder.toString();
        }
        return data;
    }


    public DerivedKeyInfoContainer getTOTPDerivedKeyFromSeed(byte[] seed) throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String totp = getTotpFromSeed(seed);
        byte[] salt = PSKCSecurityHandler.generateRandomValue(SALT_BYTE_LENGTH);
        int itCounter = pskc.getIteratorCounter();

        SecretKey derivedKey = PSKCSecurityHandler.extractDerivedKeyWithPassword(totp, salt, itCounter);

        DerivedKeyInfoContainer derivedKeyInfo = new DerivedKeyInfoContainer();
        derivedKeyInfo.setKey(derivedKey);
        derivedKeyInfo.setItCounter(itCounter);
        derivedKeyInfo.setSaltB64(Base64.encodeToString(salt, Base64.NO_WRAP));
        return derivedKeyInfo;
    }

    private String getTotpFromSeed(byte[] seed) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        return TOTPManager.generateTOTPWithSeed(pskc, seed);
    }

}
