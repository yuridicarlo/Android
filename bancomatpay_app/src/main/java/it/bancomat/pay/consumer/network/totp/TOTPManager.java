package it.bancomat.pay.consumer.network.totp;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import it.bancomat.pay.consumer.storage.model.Pskc;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class TOTPManager {

    private static int DATE_LENGTH = 16;

    private static final int[] kPinModTable = {
        0,
        10,
        100,
        1000,
        10000,
        100000,
        1000000,
        10000000,
        100000000,
    };

    public static String generateTOTPWithSeed(Pskc pskc, byte[] seed) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String totp = null;
        int lenght = pskc.getRequiredDigit();
        int timeInterval = pskc.getIntervalInSeconds();

        if(seed != null){
            long currentSteps = getCurrentSteps(timeInterval);
            String steps = formattingSteps(currentSteps);
            int binary = getIntHmacSHA256WithKeyData(seed, steps);
            int totpInt = binary % kPinModTable[lenght];
            totp = Integer.toString(totpInt);
            StringBuilder builder = new StringBuilder();
            for(int i = totp.length(); i < lenght; i++){
                builder.append("0");
            }
            builder.append(totp);
            totp = builder.toString();
        }
        CustomLogger.d("TOTPManager", "totp:" + totp);
        return totp;
    }


    private static long getCurrentSteps(int timeInterval) {
        long seconds = System.currentTimeMillis() / 1000;
        //TODO CHECK TIMEINTERVAL

        return seconds / timeInterval;
    }

    private static String formattingSteps(long currentSteps) {
        String date = Long.toHexString(currentSteps).toUpperCase();
        StringBuilder builder = new StringBuilder();
        for(int i = date.length(); i < DATE_LENGTH; i++){
            builder.append("0");
        }
        builder.append(date);

        return builder.toString();
    }


    public static int getIntHmacSHA256WithKeyData(byte[] key, String date) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Compliant with base RFC 4226 (HOTP)

        // Get the HEX in a Byte[]
        byte[] cData = date.getBytes(StandardCharsets.UTF_8);
        // byte[] k = hexStr2Bytes(key);

        byte[] cHMAC = CCHmac("HmacSHA256", key, cData);

        // put selected bytes into result int
        int offset = cHMAC[cHMAC.length - 1] & 0xf;

        return ((cHMAC[offset] & 0x7f) << 24) | ((cHMAC[offset + 1] & 0xff) << 16)
                | ((cHMAC[offset + 2] & 0xff) << 8) | (cHMAC[offset + 3] & 0xff);
    }

    private static byte[] CCHmac(String crypto, byte[] keyBytes, byte[] text) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac;
        hmac = Mac.getInstance(crypto);
        SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
        hmac.init(macKey);
        return hmac.doFinal(text);
    }

}
