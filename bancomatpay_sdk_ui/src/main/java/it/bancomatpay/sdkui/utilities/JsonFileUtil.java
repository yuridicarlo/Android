package it.bancomatpay.sdkui.utilities;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdkui.R;

public class JsonFileUtil {

	public static String loadJSONFromAsset(String fileName) {
		String json;
		try {
			InputStream is = PayCore.getAppContext().getAssets().open(fileName);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			return null;
		}
		return json;
	}

	public static String getStringId(String idString) {

		if (!TextUtils.isEmpty(idString)) {
			Context c = PayCore.getAppContext();
			int res = c.getResources().getIdentifier("string/" + idString, null, c.getPackageName());
			if (res > 0) {
				return c.getResources().getString(res);
			}
		}
		return "";
	}

	public static int getImageId(String idString) {
		if (!TextUtils.isEmpty(idString)) {
			Context c = PayCore.getAppContext();
			int res = c.getResources().getIdentifier("drawable/" + idString, null, c.getPackageName());
			if (res > 0) {
				return res;
			}
		}
		return R.drawable.placeholder;
	}

	public static int getColor(String idColor) {
		int intColor;
		try {
			intColor = Color.parseColor(idColor);
		} catch (Exception e) {
			intColor = Color.LTGRAY;
		}
		return intColor;
	}

}
