package it.bancomatpay.sdk.core;

import android.util.Base64;

import java.nio.charset.Charset;

public class Conversion {

    public static byte[] stringBase64ToByteArray(String pem){
        return Base64.decode(pem, Base64.DEFAULT);
    }


    public static byte[] stringUTF8ToByteArray(String message){
        return message.getBytes(Charset.forName("UTF-8"));
    }

    public static byte[] stringToByteArray(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String stringToHexString(String txt) {
        return byteArrayToHexString(txt.getBytes());
    }

    public static String byteArrayToHexString(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}

