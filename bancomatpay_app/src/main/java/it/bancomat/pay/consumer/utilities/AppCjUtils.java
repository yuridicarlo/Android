package it.bancomat.pay.consumer.utilities;

import android.text.TextUtils;

import java.util.UUID;

import it.bancomatpay.consumer.BuildConfig;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AppCjUtils {

	private static final String APPS_FLYER_TAG = "AppsFlyer";

	public static void updateAppsFlyerCuidIfNeeded() {
		String appsFlyerCuid = BancomatDataManager.getInstance().getAppsFlyerCustomerUserId();
		if (TextUtils.isEmpty(appsFlyerCuid)) {
			String newAppsFlyerCustomerUserId = UUID.randomUUID().toString();
			if (!BuildConfig.FLAVOR.equals("produzione")) {
				newAppsFlyerCustomerUserId = "TEST_" + newAppsFlyerCustomerUserId;
			}
			BancomatDataManager.getInstance().putAppsFlyerCustomerUserId(newAppsFlyerCustomerUserId);
			CustomLogger.d(APPS_FLYER_TAG, "CUID = " + newAppsFlyerCustomerUserId);
		}
	}

}
