package it.bancomatpay.sdk.manager.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.UUID;

import it.bancomatpay.sdk.core.BaseDataManager;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.core.SecurityPreferenceHelper;
import it.bancomatpay.sdk.core.SecurityStorageHelperInterface;
import it.bancomatpay.sdk.manager.storage.model.BankInfo;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdk.manager.storage.model.EPlayServicesType;
import it.bancomatpay.sdk.manager.storage.model.FlagModel;
import it.bancomatpay.sdk.manager.storage.model.MobileDevice;
import it.bancomatpay.sdk.manager.storage.model.Tokens;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class BancomatDataManager extends BaseDataManager {

    protected static final String TAG = BancomatDataManager.class.getSimpleName();

    private static final String DEFAULT_PREFERENCES = "DEFAULT_PREFERENCES";

    private final static String SDK_DEVICE_DATA = "sdkDeviceData";
    private final static String USER_ACCOUNT_ID = "sdkUserAccountId";
    private final static String FLAG_MODEL = "flagModel";
    private final static String FILE_PROVIDER_AUTHORITY = "fileProviderAuthority";
    private final static String PREFIX_COUNTRY_CODE = "prefixCountryCode";
    private final static String DEFAULT_COUNTRY_CODE = "defaultCountryCode";
    private final static String ANTIROOT_CHECK = "antirootCheck";
    private final static String BLOCK_IF_ROOTED = "blockIfRooted";
    private final static String BANK_SERVICES = "bankServices";
    private final static String BANK_INFO = "bankInfo";
    private final static String USER_PHONE_NUMBER = "userPhoneNumber";
    private final static String TOKENS = "tokens";
    private final static String REQUEST_ID = "requestId";
    private final static String HIDE_SPENDING_LIMITS = "hideSpendingLimits";
    private final static String SCREEN_INSET_TOP = "screenInsetTop";
    private final static String SCREEN_INSET_BOTTOM = "screenInsetBottom";
    private final static String APPS_FLYER_CUSTOMER_USER_ID = "appsFlyerCustomerUserId";
    private final static String PLAY_SERVICES_TYPE = "playServicesType";
    private final static String BPAY_TERMS_AND_CONDITIONS_ACCEPTED_TIMESTAMP = "isbPayTermsAndConditionsAcceptedTimestamp";


    private final static int version = 2;
    private static BancomatDataManager instance = null;

    public BancomatDataManager(byte[] seed, SecurityStorageHelperInterface securityHelperInterface) {
        super(seed, securityHelperInterface, version);
        CustomLogger.i(TAG, "SecurityHelperInterface = " + securityHelperInterface.getClass().getSimpleName());
    }

    public static synchronized BancomatDataManager getInstance() {
        if (instance == null) {
            instance = new BancomatDataManager(null, new SecurityPreferenceHelper());
        }
        return instance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CustomLogger.i(TAG, "onUpgrade called");
    }

    public void deleteUserData() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        String jsonMobileDeviceSdk = getString(SDK_DEVICE_DATA, "");
        EPlayServicesType ePlayServicesType = getPlayServicesType();
        clear();
        if (!TextUtils.isEmpty(jsonMobileDeviceSdk)) {
            putString(SDK_DEVICE_DATA, jsonMobileDeviceSdk);
        }
        putPlayServicesAvailability(ePlayServicesType);

    }

    public synchronized void putUserAccountId(String userAccountId) {
        putString(USER_ACCOUNT_ID, userAccountId);
    }

    public synchronized String getUserAccountId() {
        return getString(USER_ACCOUNT_ID, "");
    }

    public synchronized void putPrefixCountryCode(String prefixCountryCode) {
        putString(PREFIX_COUNTRY_CODE, prefixCountryCode);
    }

    public synchronized String getPrefixCountryCode() {
        return getString(PREFIX_COUNTRY_CODE, "");
    }

    public synchronized void putDefaultCountryCode(String prefixCountryCode) {
        putString(DEFAULT_COUNTRY_CODE, prefixCountryCode);
    }

    public synchronized String getDefaultCountryCode() {
        return getString(DEFAULT_COUNTRY_CODE, "");
    }

    public synchronized void putUserPhoneNumber(String phone) {
        putString(USER_PHONE_NUMBER, phone);
    }

    public synchronized String getUserPhoneNumber() {
        return getString(USER_PHONE_NUMBER, "");
    }

    public synchronized void putRequestId(String requestId) {
        putString(REQUEST_ID, requestId);
    }

    public synchronized String getRequestId() {
        return getString(REQUEST_ID, "");
    }

    public synchronized MobileDevice getMobileDevice() {
        String jsonMobileDevice = getString(SDK_DEVICE_DATA, "");
        MobileDevice mobileDevice = new MobileDevice();
        if (!TextUtils.isEmpty(jsonMobileDevice)) {
            Gson gson = new Gson();
            mobileDevice = gson.fromJson(jsonMobileDevice, MobileDevice.class);
        }
        return mobileDevice;
    }

    public synchronized boolean putMobileDeviceData(String key) {
        Gson gson = new Gson();
        MobileDevice mobileDevice;
        String jsonMobileDevice = getString(SDK_DEVICE_DATA, "");
        if (!TextUtils.isEmpty(jsonMobileDevice)) {
            mobileDevice = gson.fromJson(jsonMobileDevice, MobileDevice.class);
            if (TextUtils.isEmpty(mobileDevice.getKey())) {
                mobileDevice.setKey(key);
            }
            jsonMobileDevice = gson.toJson(mobileDevice);
            return putString(SDK_DEVICE_DATA, jsonMobileDevice);
        }
        return false;
    }

    public synchronized FlagModel getFlagModel() {
        String json = getString(FLAG_MODEL, "");
        FlagModel mobileDevice = new FlagModel();
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            mobileDevice = gson.fromJson(json, FlagModel.class);
        }
        return mobileDevice;
    }

    public synchronized boolean putFlagModel(FlagModel flagModel) {
        Gson gson = new Gson();
        String jsonMobileDevice = gson.toJson(flagModel);
        return putString(FLAG_MODEL, jsonMobileDevice);
    }

    public synchronized void putFileProviderAuthority(String fileProviderAuthority) {
        putString(FILE_PROVIDER_AUTHORITY, fileProviderAuthority);
    }

    public synchronized String getFileProviderAuthority() {
        return getString(FILE_PROVIDER_AUTHORITY, "");
    }

    public synchronized void putAppsFlyerCustomerUserId(String cuid) {
        putString(APPS_FLYER_CUSTOMER_USER_ID, cuid);
    }

    public synchronized String getAppsFlyerCustomerUserId() {
        return getString(APPS_FLYER_CUSTOMER_USER_ID, "");
    }

    public synchronized void putHideSpendingLimits(Boolean hideSpendingLimits) {
        if (hideSpendingLimits != null) {
            putString(HIDE_SPENDING_LIMITS, String.valueOf(hideSpendingLimits));
        }
    }

    public synchronized boolean isHideSpendingLimits() {
        return Boolean.parseBoolean(getString(HIDE_SPENDING_LIMITS, ""));
    }

    public synchronized void putPlayServicesAvailability(EPlayServicesType servicesType) {
        putString(PLAY_SERVICES_TYPE, servicesType.toString());
    }

    public synchronized EPlayServicesType getPlayServicesType() {
        return EPlayServicesType.valueOf(getString(PLAY_SERVICES_TYPE, EPlayServicesType.NONE.toString()));
    }

    public synchronized void putScreenInsetTop(int insetTop) {
        putString(SCREEN_INSET_TOP, String.valueOf(insetTop));
    }

    public synchronized int getScreenInsetTop() {
        return Integer.parseInt(getString(SCREEN_INSET_TOP, "0"));
    }

    public synchronized void putScreenInsetBottom(int insetBottom) {
        putString(SCREEN_INSET_BOTTOM, String.valueOf(insetBottom));
    }

    public synchronized int getScreenInsetBottom() {
        return Integer.parseInt(getString(SCREEN_INSET_BOTTOM, "0"));
    }

    public synchronized void putAntirootCheckEnabled(Boolean antirootCheckEnabled) {
        if (antirootCheckEnabled != null) {
            putString(ANTIROOT_CHECK, String.valueOf(antirootCheckEnabled));
        }
    }

    public synchronized boolean isAntirootCheckEnabled() {
        return Boolean.parseBoolean(getString(ANTIROOT_CHECK, ""));
    }

    public synchronized void putBlockIfRooted(Boolean blockIfRooted) {
        if (blockIfRooted != null) {
            putString(BLOCK_IF_ROOTED, String.valueOf(blockIfRooted));
        }
    }

    public synchronized boolean isBlockIfRooted() {
        return Boolean.parseBoolean(getString(BLOCK_IF_ROOTED, ""));
    }

    public synchronized void putBankActiveServices(BankServices bankServices) {
        Gson gson = new Gson();
        String json = gson.toJson(bankServices);
        putString(BANK_SERVICES, json);
    }

    public synchronized BankServices getBankBankActiveServices() {
        String json = getString(BANK_SERVICES, "");
        BankServices bankServices = null;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            bankServices = gson.fromJson(json, BankServices.class);
        }
        return bankServices;
    }

    public synchronized void putBankInfo(String abiCode, String groupCode) {
        if (!TextUtils.isEmpty(abiCode) && !TextUtils.isEmpty(groupCode)) {
            BankInfo bankInfo = new BankInfo();
            bankInfo.setAbiCode(abiCode);
            bankInfo.setGroupCode(groupCode);
            Gson gson = new Gson();
            String json = gson.toJson(bankInfo);
            putString(BANK_INFO, json);
        }
    }

    public synchronized BankInfo getBankInfo() {
        String json = getString(BANK_INFO, "");
        BankInfo bankInfo = new BankInfo();
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            bankInfo = gson.fromJson(json, BankInfo.class);
        }
        return bankInfo;
    }

    public synchronized void putTokens(String oauth, String refresh) {
        Tokens tokens = new Tokens();
        tokens.setOauth(oauth);
        tokens.setRefres(refresh);
        Gson gson = new Gson();
        String json = gson.toJson(tokens);
        putString(TOKENS, json);
    }

    public synchronized Tokens getTokens() {
        String json = getString(TOKENS, "");
        Tokens tokens = null;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            tokens = gson.fromJson(json, Tokens.class);
        }
        return tokens;
    }

    public synchronized void putBPayTermsAndConditionsAcceptedTimestamp(String timeStamp) {
        putString(BPAY_TERMS_AND_CONDITIONS_ACCEPTED_TIMESTAMP, timeStamp);
    }

    public synchronized String getBPayTermsAndConditionsAcceptedTimestamp() {
        return getString(BPAY_TERMS_AND_CONDITIONS_ACCEPTED_TIMESTAMP, "");
    }

    public synchronized void generateUuid() {
        Gson gson = new Gson();
        MobileDevice mobileDevice;
        String uuid = UUID.randomUUID().toString();
        mobileDevice = new MobileDevice();
        mobileDevice.setUuid(uuid);
        String jsonMobileDevice = gson.toJson(mobileDevice);
        putString(SDK_DEVICE_DATA, jsonMobileDevice);
    }

}
