package it.bancomatpay.sdkui.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdkui.prefixphonenumber.DataPrefixPhoneNumber;

public class FullStackSdkDataManager {

    protected static final String TAG = FullStackSdkDataManager.class.getSimpleName();

    private final static String SHARED_PREFERENCES_TUTORIAL = "SHARED_PREFERENCES_TUTORIAL";

    private final static String HOME_PANEL_EXPANDED = "homePanelExpanded";
    private final static String SHOW_BALANCE = "showBalance";
    private final static String PREFIX_LIST = "prefixList";
    private final static String IS_TUTORIAL_ALREADY_SHOWN_LOYALTY_CARDS = "tutorialLoyaltyCards";
    private final static String IS_TUTORIAL_ALREADY_SHOWN_DOCUMENTS = "tutorialDocuments";
    private final static String IS_TUTORIAL_ALREADY_SHOWN_BANK_ID = "tutorialBankId";
    private final static String IS_TUTORIAL_ALREADY_SHOWN_ATM_CARDLESS = "tutorialAtmCardless";
    private final static String IS_TUTORIAL_ALREADY_SHOWN_PETROL = "tutorialPetrol";
    private final static String TIMES_TO_SHOW_CASHBACK_DIALOG = "timeToShowCashbackDialog";
    private final static String DATA_PREFIX_PHONE_NUMBER = "dataPrefixPhoneNumber";
    private final static String PREFIX_COUNTRY_CODE = "prefixCountryCode";
    private final static String IS_BPLAY_BALLOON_ALREADY_SHOWN = "isBplayBalloonAlreadyShown";
    private final static String IS_BPLAY_STAR_GIF_ALREADY_SHOWN = "isBplayStarGifAlreadyShown";
    private final static String TIMES_TO_ACCESS_HOME = "timesToAccessHome";

    private static FullStackSdkDataManager instance = null;

    public static synchronized FullStackSdkDataManager getInstance() {
        if (instance == null) {
            instance = new FullStackSdkDataManager();
        }
        return instance;
    }

    public synchronized void deleteUserData() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public synchronized void putHomePanelExpanded(boolean panelExpanded) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HOME_PANEL_EXPANDED, panelExpanded);
        editor.apply();
    }

    public synchronized boolean isHomePanelExpanded() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        return preferences.getBoolean(HOME_PANEL_EXPANDED, true);
    }

    public synchronized void putShowBalance(boolean showBalance) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_BALANCE, showBalance);
        editor.apply();
    }

    public synchronized boolean getShowBalance() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        return preferences.getBoolean(SHOW_BALANCE, false);
    }

    public synchronized List<DataPrefixPhoneNumber> getPrefixList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        String json = preferences.getString(PREFIX_LIST, "");
        List<DataPrefixPhoneNumber> prefixList = new ArrayList<>();
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            prefixList = gson.fromJson(json, new TypeToken<List<DataPrefixPhoneNumber>>() {
            }.getType());
        }
        return prefixList;
    }

    public synchronized void putPrefixList(List<DataPrefixPhoneNumber> prefixList) {
        Gson gson = new Gson();
        String jsonPrefixList = gson.toJson(prefixList);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFIX_LIST, jsonPrefixList);
        editor.apply();
    }

    public synchronized void putDataPrefixPhoneNumber(DataPrefixPhoneNumber dataPrefixPhoneNumber) {
        Gson gson = new Gson();
        String json = gson.toJson(dataPrefixPhoneNumber);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DATA_PREFIX_PHONE_NUMBER, json);
        editor.apply();
    }

    public synchronized DataPrefixPhoneNumber getDataPrefixPhoneNumber() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        String json = preferences.getString(DATA_PREFIX_PHONE_NUMBER, "");
        DataPrefixPhoneNumber dataPrefixPhoneNumber = null;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            dataPrefixPhoneNumber = gson.fromJson(json, new TypeToken<DataPrefixPhoneNumber>() {
            }.getType());
        }
        return dataPrefixPhoneNumber;
    }

    public synchronized void putPrefixCountryCode(String prefixCountryCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFIX_COUNTRY_CODE, prefixCountryCode);
        editor.apply();
    }

    public synchronized String getPrefixCountryCode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        String json = preferences.getString(PREFIX_COUNTRY_CODE, "");
        return json;
    }


    public synchronized void putTutorialLoyaltyCardsAlreadyShown(boolean isTutorialShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_TUTORIAL_ALREADY_SHOWN_LOYALTY_CARDS, isTutorialShown);
        editor.apply();
    }

    public synchronized boolean isTutorialLoyaltyCardsAlreadyShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_TUTORIAL_ALREADY_SHOWN_LOYALTY_CARDS, false);
    }

    public synchronized void putTutorialDocumentsAlreadyShown(boolean isTutorialShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_TUTORIAL_ALREADY_SHOWN_DOCUMENTS, isTutorialShown);
        editor.apply();
    }

    public synchronized boolean isTutorialDocumentsAlreadyShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_TUTORIAL_ALREADY_SHOWN_DOCUMENTS, false);
    }

    public synchronized void putTutorialBankIdAlreadyShown(boolean isTutorialShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_TUTORIAL_ALREADY_SHOWN_BANK_ID, isTutorialShown);
        editor.apply();
    }

    public synchronized boolean isTutorialBankIdAlreadyShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_TUTORIAL_ALREADY_SHOWN_BANK_ID, false);
    }

    public synchronized void putTutorialAtmCardlessAlreadyShown(boolean isTutorialShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_TUTORIAL_ALREADY_SHOWN_ATM_CARDLESS, isTutorialShown);
        editor.apply();
    }

    public synchronized boolean isTutorialAtmCardlessAlreadyShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_TUTORIAL_ALREADY_SHOWN_ATM_CARDLESS, false);
    }

    public synchronized void putTutorialPetrolAlreadyShown(boolean isTutorialShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_TUTORIAL_ALREADY_SHOWN_PETROL, isTutorialShown);
        editor.apply();
    }

    public synchronized boolean isTutorialPetrolAlreadyShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_TUTORIAL_ALREADY_SHOWN_PETROL, false);
    }

    public synchronized void putTimesToShowCashbackDialog(int timesToShow) {
        Gson gson = new Gson();
        String json = gson.toJson(timesToShow);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TIMES_TO_SHOW_CASHBACK_DIALOG, json);
        editor.apply();
    }

    public synchronized int getTimeToShowCashbackDialog() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        String json = preferences.getString(TIMES_TO_SHOW_CASHBACK_DIALOG, "");
        int timesToShow;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            timesToShow = gson.fromJson(json, new TypeToken<Integer>() {
            }.getType());
        } else timesToShow = 0;
        return timesToShow;
    }

    public synchronized void putInAppDisclosureAlreadyShown(boolean isInAppDisclosureShown, String disclosureType) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(disclosureType, isInAppDisclosureShown);
        editor.apply();
    }

    public synchronized boolean isInAppDisclosureAlreadyShown(String disclosureType) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(disclosureType, false);
    }

    public synchronized void putBplayBalloonAlreadyShown(boolean isBalloonShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_BPLAY_BALLOON_ALREADY_SHOWN, isBalloonShown);
        editor.apply();
    }

    public synchronized boolean isBplayBalloonAlreadyShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_BPLAY_BALLOON_ALREADY_SHOWN, false);
    }

    public synchronized void putBplayStarGifAlreadyShown(boolean isBplayStarGifShown) {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_BPLAY_STAR_GIF_ALREADY_SHOWN, isBplayStarGifShown);
        editor.apply();
    }

    public synchronized boolean isBplayStarGifShown() {
        SharedPreferences preferences = PayCore.getAppContext().getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_BPLAY_STAR_GIF_ALREADY_SHOWN, false);
    }

    public synchronized void putTimeToAccessInHome(int timesToShow) {
        Gson gson = new Gson();
        String json = gson.toJson(timesToShow);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TIMES_TO_ACCESS_HOME, json);
        editor.apply();
    }

    public synchronized void deleteTimeToAccessInHome(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(TIMES_TO_ACCESS_HOME).commit();
    }

    public synchronized int getTimeToAccessInHome() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayCore.getAppContext());
        String json = preferences.getString(TIMES_TO_ACCESS_HOME, "");
        int timesToShow;
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            timesToShow = gson.fromJson(json, new TypeToken<Integer>() {
            }.getType());
        } else timesToShow = 0;
        return timesToShow;
    }


}
