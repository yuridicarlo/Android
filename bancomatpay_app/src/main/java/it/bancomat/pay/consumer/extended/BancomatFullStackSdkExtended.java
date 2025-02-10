package it.bancomat.pay.consumer.extended;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.gson.Gson;

import java.util.Map;

import it.bancomat.pay.consumer.extended.activities.HomeActivityExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankInfo;
import it.bancomatpay.sdk.manager.storage.model.FlagModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.config.BancomatSdkConfig;
import it.bancomatpay.sdkui.utilities.JsonFileUtil;
import it.bancomatpay.sdkui.utilities.LogoBankSingleton;

import static it.bancomatpay.sdkui.config.BancomatSdkConfig.SDK_CONFIG_JSON_FILE_NAME;

public class BancomatFullStackSdkExtended extends BancomatFullStackSdk implements BancomatFullStackSdkInterfaceExtended {

    private static final String APPS_FLYER_TAG = "AppsFlyer";
    public static final String PROCESS_PUSH_EXTRA = "PROCESS_PUSH_EXTRA";

    private static BancomatFullStackSdkExtended instance;

    public static synchronized BancomatFullStackSdkExtended getInstance() {
        if (instance == null) {
            instance = new BancomatFullStackSdkExtended();
        }
        return instance;
    }

    @Override
    public void startBancomatPayFlow(@NonNull Activity activity, Drawable logoBank, String sessionToken) {

        initAppsFlyerSdk(activity);

        Gson gson = new Gson();
        BancomatSdkConfig sdkConfig;
        try {
            sdkConfig = gson.fromJson(JsonFileUtil.loadJSONFromAsset(SDK_CONFIG_JSON_FILE_NAME), BancomatSdkConfig.class);
        } catch (Exception e) {
            sdkConfig = new BancomatSdkConfig();
            sdkConfig.getGenericFlags().setHideSpendingLimits(false);
            sdkConfig.getAndroidFlags().setFileProviderAuthority("");
        }

        BankInfo bankInfo = BancomatDataManager.getInstance().getBankInfo();

        BancomatSdkInterface.Factory.getInstance().doInitSdk(activity, result -> {
            //Ignoriamo risultato initSdk, chiamata atta solo a recuperare la lista servizi aggiornata
        }, null, null, bankInfo.getAbiCode(), bankInfo.getGroupCode());

        SessionManager.getInstance().setSessionToken(sessionToken);
        LogoBankSingleton.getInstance().setLogoBank(logoBank);

        BancomatDataManager.getInstance().putFileProviderAuthority(sdkConfig.getAndroidFlags().getFileProviderAuthority());
        BancomatDataManager.getInstance().putHideSpendingLimits(sdkConfig.getGenericFlags().isHideSpendingLimits());

        BCMAbortCallback.getInstance().setSessionRefreshListener(refreshedToken -> {
            SessionManager.getInstance().setSessionToken(refreshedToken);
            retrySessionTask();
        });

        Intent intentHome = new Intent(activity, HomeActivityExtended.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intentHome);
    }

    @Override
    public void startBancomatPayFlowClearTask(@NonNull Activity activity, Drawable logoBank, boolean processPush, String sessionToken) {

        initAppsFlyerSdk(activity);

        Gson gson = new Gson();
        BancomatSdkConfig sdkConfig;
        try {
            sdkConfig = gson.fromJson(JsonFileUtil.loadJSONFromAsset(SDK_CONFIG_JSON_FILE_NAME), BancomatSdkConfig.class);
        } catch (Exception e) {
            sdkConfig = new BancomatSdkConfig();
            sdkConfig.getGenericFlags().setHideSpendingLimits(false);
            sdkConfig.getAndroidFlags().setFileProviderAuthority("");
        }

        BankInfo bankInfo = BancomatDataManager.getInstance().getBankInfo();

        BancomatSdkInterface.Factory.getInstance().doInitSdk(activity, result -> {
            //Ignoriamo risultato initSdk, chiamata atta solo a recuperare la lista servizi aggiornata
        }, null, null, bankInfo.getAbiCode(), bankInfo.getGroupCode());

        SessionManager.getInstance().setSessionToken(sessionToken);
        LogoBankSingleton.getInstance().setLogoBank(logoBank);

        BancomatDataManager.getInstance().putFileProviderAuthority(sdkConfig.getAndroidFlags().getFileProviderAuthority());
        BancomatDataManager.getInstance().putHideSpendingLimits(sdkConfig.getGenericFlags().isHideSpendingLimits());

        BCMAbortCallback.getInstance().setSessionRefreshListener(refreshedToken -> {
            SessionManager.getInstance().setSessionToken(refreshedToken);
            retrySessionTask();
        });

        Intent intentHome = new Intent(activity, HomeActivityExtended.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentHome.putExtra(PROCESS_PUSH_EXTRA, processPush);
        activity.startActivity(intentHome);
    }

    @Override
    public void setLoyaltyToken(String loyaltyToken) {
        LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);
    }

    private void initAppsFlyerSdk(Activity activity) {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    CustomLogger.d(APPS_FLYER_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                CustomLogger.d(APPS_FLYER_TAG, "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    CustomLogger.d(APPS_FLYER_TAG, "attribute: " + attrName + " = " + attributionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                CustomLogger.d(APPS_FLYER_TAG, "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init(activity.getString(it.bancomatpay.sdkui.R.string.APPSFLYER_DEV_KEY), conversionListener, activity);

        String appsFlyerCustomerUserId = BancomatDataManager.getInstance().getAppsFlyerCustomerUserId();
        AppsFlyerLib.getInstance().setCustomerUserId(appsFlyerCustomerUserId);

        if (BancomatPayApiInterface.Factory.getInstance().isProfilingConsentAllowed()) {
            AppsFlyerLib.getInstance().start(activity);
            CustomLogger.d(APPS_FLYER_TAG, "AppsFlyerLib started");
            AppsFlyerLib.getInstance().logEvent(activity, "ACTIVATION_COMPLETED", null);
        } else {
            CustomLogger.d(APPS_FLYER_TAG, "AppsFlyerLib NOT started");
        }
    }

}
