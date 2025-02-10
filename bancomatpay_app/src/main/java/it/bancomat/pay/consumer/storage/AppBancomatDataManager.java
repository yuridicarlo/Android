package it.bancomat.pay.consumer.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.UUID;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomat.pay.consumer.storage.model.AppFlagModel;
import it.bancomat.pay.consumer.storage.model.AppMobileDevice;
import it.bancomat.pay.consumer.storage.model.AppTokens;
import it.bancomat.pay.consumer.storage.model.Pskc;
import it.bancomatpay.sdk.core.SecurityPreferenceHelper;
import it.bancomatpay.sdk.core.SecurityStorageHelperInterface;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.utilities.Conversion;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AppBancomatDataManager extends AppBaseDataManager {

    protected static final String TAG = AppBancomatDataManager.class.getSimpleName();

    private static final String DEFAULT_PREFERENCES = "DEFAULT_PREFERENCES_APP";

    private static final String IV_DATA = "D_IV";
    private static final String T_ID_DATA = "D_DIT";
    private final static String TOKENS = "tokens";
    private final static String DEVICE_DATA = "deviceData";
    private final static String PSKC = "pskc";
    private final static String BANK_UUID = "bankUUID";
    private final static String ACTIVATION_TOKEN = "activationToken";
    private final static String USER_ACCOUNT_ID = "userAccountId";
    private final static String FLAG_MODEL = "flagModel";
    private final static String PREFIX_COUNTRY_CODE = "prefixCountryCode";
    private final static String ACTIVATION_PHONE_NUMBER = "activationPhoneNumber";
    private final static String PUSH_TOKEN = "pushToken";
    private final static String CJ_CONSENTS_TIMESTAMP_FOR_KO = "cjKoTimestamp";
    private final static String DATA_PREFIX_PHONE_NUMBER = "dataPrefixPhoneNumber";
    private final static String BIOMETRIC_AES_KEY = "r";
    private final static String AES_IV = "g";
    private final static String BIOMETRY_TOKEN = "d";

    private final static int version = 2;
    private static AppBancomatDataManager instance = null;

    public AppBancomatDataManager(byte[] seed, SecurityStorageHelperInterface securityHelperInterface) {
        super(seed, securityHelperInterface, version);
        CustomLogger.i(TAG, "SecurityHelperInterface = " + securityHelperInterface.getClass().getSimpleName());
    }

    public static synchronized AppBancomatDataManager getInstance() {
        if (instance == null) {
            instance = new AppBancomatDataManager(null, new SecurityPreferenceHelper());
        }
        return instance;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CustomLogger.i(TAG, "onUpgrade called");
    }

    public void deleteUserData() {
        SharedPreferences preferences = BancomatApplication.getAppContext().getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        String jsonMobileDevice = getString(DEVICE_DATA, "");
        clear();
        if (!TextUtils.isEmpty(jsonMobileDevice)) {
            putString(DEVICE_DATA, jsonMobileDevice);
        }
    }

    public synchronized void deleteTouchIdData() {
        putString(T_ID_DATA, null);
    }

    public synchronized void putTIdData(byte[] tIdData) {
        putString(T_ID_DATA, Base64.encodeToString(tIdData, Base64.DEFAULT));
    }

    public synchronized byte[] getTIdData() {
        String tIdData = getString(T_ID_DATA, "");
        if (TextUtils.isEmpty(tIdData)) {
            return null;
        }
        return Base64.decode(tIdData, Base64.DEFAULT);
    }

    public void putIvData(byte[] iv) {
        putString(IV_DATA, Base64.encodeToString(iv, Base64.DEFAULT));
    }

    public byte[] getIvData() {
        String ivData = getString(IV_DATA, "");
        if (TextUtils.isEmpty(ivData)) {
            return null;
        }
        return Base64.decode(ivData, Base64.DEFAULT);
    }

    public synchronized void putTokens(String oauth, String refresh) {
        AppTokens tokens = new AppTokens();
        tokens.setOauth(oauth);
        tokens.setRefres(refresh);
        Gson gson = new Gson();
        String json = gson.toJson(tokens);
        putString(TOKENS, json);

        BancomatDataManager.getInstance().putTokens(oauth, refresh);
    }

    public synchronized AppTokens getTokens() {
        String json = getString(TOKENS, "");
        AppTokens tokens = new AppTokens();
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            try {
                tokens = gson.fromJson(json, AppTokens.class);
            } catch (JsonSyntaxException e) {
                throw new RuntimeException("Error in getTokens: " + e.getMessage() + " - tokens = " + tokens);
            }
        }
        return tokens;
    }

    public synchronized void putUserAccountId(String userAccountId) {
        putString(USER_ACCOUNT_ID, userAccountId);
    }

    public synchronized String getUserAccountId() {
        return getString(USER_ACCOUNT_ID, "");
    }

  /*  public synchronized void putPrefixCountryCode(String prefixCountryCode) {
        putString(PREFIX_COUNTRY_CODE, prefixCountryCode);
    }

    public synchronized String getPrefixCountryCode() {
        return getString(PREFIX_COUNTRY_CODE, "");
    }

    public synchronized void putDataPrefixPhoneNumber(DataPrefixPhoneNumber dataPrefixPhoneNumber) {
        Gson gson = new Gson();
        String json = gson.toJson(dataPrefixPhoneNumber);
        putString(DATA_PREFIX_PHONE_NUMBER, json);
    }

    public synchronized DataPrefixPhoneNumber getDataPrefixPhoneNumber() {
        String json = getString(DATA_PREFIX_PHONE_NUMBER, "");
        DataPrefixPhoneNumber dataPrefixPhoneNumber = null;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            dataPrefixPhoneNumber = gson.fromJson(json, DataPrefixPhoneNumber.class);
        }
        return dataPrefixPhoneNumber;
    }*/

    public synchronized void putActivationPhoneNumber(String activationPhoneNumber) {
        putString(ACTIVATION_PHONE_NUMBER, activationPhoneNumber);
        BancomatDataManager.getInstance().putUserPhoneNumber(activationPhoneNumber);
    }

    public synchronized String getActivationPhoneNumber() {
        return getString(ACTIVATION_PHONE_NUMBER, "");
    }

    public synchronized void putBankUuid(String bankUuid) {
        putString(BANK_UUID, bankUuid);
    }

    public synchronized String getBankUuid() {
        return getString(BANK_UUID, "");
    }

    public synchronized void putActivationToken(String activationToken) {
        putString(ACTIVATION_TOKEN, activationToken);
    }

    public synchronized String getActivationToken() {
        return getString(ACTIVATION_TOKEN, "");
    }

    public synchronized void putPushToken(String pushToken) {
        putString(PUSH_TOKEN, pushToken);
    }

    public synchronized String getPushToken() {
        return getString(PUSH_TOKEN, "");
    }

    public synchronized void putPskc(Pskc pskc) {
        Gson gson = new Gson();
        String json = gson.toJson(pskc);
        putString(PSKC, json);
    }

    public synchronized Pskc getPskc() {
        String json = getString(PSKC, "");
        Pskc pskc = null;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            pskc = gson.fromJson(json, Pskc.class);
        }
        return pskc;
    }

    public synchronized AppMobileDevice getMobileDevice() {
        String jsonMobileDevice = getString(DEVICE_DATA, "");
        AppMobileDevice mobileDevice = new AppMobileDevice();
        if (!TextUtils.isEmpty(jsonMobileDevice)) {
            Gson gson = new Gson();
            try {
                mobileDevice = gson.fromJson(jsonMobileDevice, AppMobileDevice.class);
            } catch (JsonSyntaxException e) {
                throw new RuntimeException("Error in getMobileDevice: " + e.getMessage() + " - jsonMobileDevice = " + jsonMobileDevice);
            }
        }
        return mobileDevice;
    }

    public synchronized boolean putMobileDeviceData(String key) {
        Gson gson = new Gson();
        AppMobileDevice mobileDevice;
        String jsonMobileDevice = getString(DEVICE_DATA, "");
        if (!TextUtils.isEmpty(jsonMobileDevice)) {
            mobileDevice = gson.fromJson(jsonMobileDevice, AppMobileDevice.class);
            mobileDevice.setKey(key);
            jsonMobileDevice = gson.toJson(mobileDevice);
            return putString(DEVICE_DATA, jsonMobileDevice);
        }
        return false;
    }

    public synchronized AppFlagModel getFlagModel() {

        String json = getString(FLAG_MODEL, "");
        AppFlagModel mobileDevice = new AppFlagModel();
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            mobileDevice = gson.fromJson(json, AppFlagModel.class);
        }
        return mobileDevice;
    }

    public synchronized boolean putFlagmodel(AppFlagModel flagModel) {
        Gson gson = new Gson();
        String jsonMobileDevice = gson.toJson(flagModel);
        return putString(FLAG_MODEL, jsonMobileDevice);
    }

    public synchronized void generateUuid() {
        Gson gson = new Gson();
        AppMobileDevice mobileDevice;
        String uuid = UUID.randomUUID().toString();
        mobileDevice = new AppMobileDevice();
        mobileDevice.setUuid(uuid);
        String jsonMobileDevice = gson.toJson(mobileDevice);
        putString(DEVICE_DATA, jsonMobileDevice);
    }

    public synchronized void putCjConsentsTimestampForKo(String timeStamp) {
        putString(CJ_CONSENTS_TIMESTAMP_FOR_KO, timeStamp);
    }

    public synchronized String getCjConsentsTimestampForKo() {
        return getString(CJ_CONSENTS_TIMESTAMP_FOR_KO, "");
    }

    public synchronized void putBiometricAESKey(byte[] value) {
        if (value == null) {
            putString(BIOMETRIC_AES_KEY, "");
        } else {
            putString(BIOMETRIC_AES_KEY, Conversion.byteArrayToStringBase64(value));
        }
    }

    public synchronized byte[] getBiometricAESKey() {
        byte[] value = null;
        String raw = getString(BIOMETRIC_AES_KEY, "");
        if (!TextUtils.isEmpty(raw)) {
            value = Conversion.stringBase64ToByteArray(raw);
        }
        return value;
    }

    public synchronized void putAESIV(byte[] value) {
        if (value == null) {
            putString(AES_IV, "");
        } else {
            putString(AES_IV, Conversion.byteArrayToStringBase64(value));
        }
    }

    public synchronized byte[] getAESIV() {
        byte[] value = null;
        String raw = getString(AES_IV, "");
        if (!TextUtils.isEmpty(raw)) {
            value = Conversion.stringBase64ToByteArray(raw);
        }
        return value;
    }


    public synchronized boolean putDataAppAuthentication(byte[] value) {
        if (value == null) {
            return putString(BIOMETRY_TOKEN, "");
        } else {
            return putString(BIOMETRY_TOKEN, Conversion.byteArrayToStringBase64(value));
        }
    }

    public synchronized byte[] getDataAppAuthentication() {
        byte[] value = null;
        String raw = getString(BIOMETRY_TOKEN, "");
        if (!TextUtils.isEmpty(raw)) {
            value = Conversion.stringBase64ToByteArray(raw);
        }
        return value;
    }
}
