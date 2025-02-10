package it.bancomatpay.sdk.manager.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class Conversion {

    public static byte[] stringBase64ToByteArray(String pem){
        return Base64.decode(pem, Base64.DEFAULT);
    }


    public static String byteArrayToStringBase64(byte[] pem){
        return Base64.encodeToString(pem, Base64.DEFAULT);
    }

    public static byte[] utf8stringToByteArray(String message){
        return message.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] hexStringToByteArray(String hexString) {
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


    public static Result<Bitmap> doGetBitmapFromBase64(String base64Image) {
        Result<Bitmap> result = new Result<>();
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            result.setResult(bitmap);
            result.setStatusCode(StatusCode.Mobile.OK);
        } catch (Exception e) {
            result.setResult(null);
            result.setStatusCode(StatusCode.Mobile.DECODE_BITMAP_ERROR);
        }
        return result;
    }

    public static Result<String> doGetBase64FromDrawable(int drawableResource) {
        Result<String> result = new Result<>();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;

        Bitmap bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(), drawableResource, options);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        if (!TextUtils.isEmpty(encoded)) {
            result.setResult(encoded);
            result.setStatusCode(StatusCode.Mobile.OK);
        } else {
            result.setResult(null);
            result.setStatusCode(StatusCode.Mobile.ENCODE_BASE_64_ERROR);
        }

        return result;
    }
}

