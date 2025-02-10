package it.bancomat.pay.consumer.activation.databank;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bancomat.pay.consumer.exception.BanksDataDbException;
import it.bancomat.pay.consumer.storage.AppUserDbHelper;
import it.bancomatpay.sdk.manager.task.model.BankDataMultiIban;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdkui.utilities.JsonFileUtil;

public class DataBankManager {

    private static final String TAG = DataBankManager.class.getSimpleName();
    private static final String JSON_FILE_NAME = "BankInfoDataList.json";

    private final static List<DataBank> subscribedDataBankList = new ArrayList<>();
    private final static List<DataBank> unsubscribedDataBankList = new ArrayList<>();
    private final static Map<String, DataBank> fullDataBankMap = new HashMap<>();

    private static void addBankDataSubscribed(DataBank dataBank) {
        subscribedDataBankList.add(dataBank);
        fullDataBankMap.put(dataBank.getBankUUID(), dataBank);
    }

    private static void addBankDataUnsubscribed(DataBank dataBank) {
        unsubscribedDataBankList.add(dataBank);
        fullDataBankMap.put(dataBank.getBankUUID(), dataBank);
    }

    public static ArrayList<DataBank> getUserDataBankListMultiIban(List<BankDataMultiIban> banksDataMultiIban) {
        ArrayList<DataBank> dataBankList = new ArrayList<>();
        if (banksDataMultiIban != null) {
            for (BankDataMultiIban bankData : banksDataMultiIban) {
                if (fullDataBankMap.containsKey(bankData.getBankUUID())) {
                    DataBank dataBankMultiIban = fullDataBankMap.get(bankData.getBankUUID());
                    if (bankData.isMultiIban()) {
                        dataBankMultiIban.setMultiIban(true);
                        dataBankMultiIban.setInstrument(Mapper.getInstruments(bankData.getInstruments()));
                    }
                    dataBankList.add(dataBankMultiIban);
                }
            }
        }
        return dataBankList;
    }

    public static DataBank getDataBank(String bankUuid) {
        return fullDataBankMap.get(bankUuid);
    }

    static {
        try {
            Gson gson = new Gson();
            BankInfoDataListJson bankInfoDataListJson;
            if (!TextUtils.isEmpty(AppUserDbHelper.getInstance().getBanksData().getFile())) {
                bankInfoDataListJson = gson.fromJson(AppUserDbHelper.getInstance().getBanksData().getFile(), BankInfoDataListJson.class);
            } else {
                bankInfoDataListJson = gson.fromJson(JsonFileUtil.loadJSONFromAsset(JSON_FILE_NAME), BankInfoDataListJson.class);
            }
            if (bankInfoDataListJson.subscribedBanks != null) {
                for (Bank bank : bankInfoDataListJson.subscribedBanks) {
                    addBankDataSubscribed(getBankDataDownloaded(bank));
                }
            }
            if (bankInfoDataListJson.unsubscribedBanks != null) {
                for (Bank bank : bankInfoDataListJson.unsubscribedBanks) {
                    addBankDataUnsubscribed(getBankDataDownloaded(bank));
                }
            }
        } catch (BanksDataDbException e) {
            CustomLogger.e(TAG, "Database error in BanksData initialization");
        }
    }

    public static void forceDownloadedBanksJson() {
        try {
            Gson gson = new Gson();
            BankInfoDataListJson bankInfoDataListJson = gson.fromJson(
                    AppUserDbHelper.getInstance().getBanksData().getFile(), BankInfoDataListJson.class);

            subscribedDataBankList.clear();
            unsubscribedDataBankList.clear();
            fullDataBankMap.clear();

            if (bankInfoDataListJson.subscribedBanks != null) {
                for (Bank bank : bankInfoDataListJson.subscribedBanks) {
                    addBankDataSubscribed(getBankDataDownloaded(bank));
                }
            }
            if (bankInfoDataListJson.unsubscribedBanks != null) {
                for (Bank bank : bankInfoDataListJson.unsubscribedBanks) {
                    addBankDataUnsubscribed(getBankDataDownloaded(bank));
                }
            }
        } catch (BanksDataDbException e) {
            CustomLogger.e(TAG, "Database error in BanksData initialization");
        }
    }

    public static void forceLocalBanksJson() {

        Gson gson = new Gson();
        BankInfoDataListJson bankInfoDataListJson = gson.fromJson(JsonFileUtil.loadJSONFromAsset(JSON_FILE_NAME), BankInfoDataListJson.class);

        subscribedDataBankList.clear();
        unsubscribedDataBankList.clear();
        fullDataBankMap.clear();

        if (bankInfoDataListJson.subscribedBanks != null) {
            for (Bank bank : bankInfoDataListJson.subscribedBanks) {
                addBankDataSubscribed(getBankDataDownloaded(bank));
            }
        }
        if (bankInfoDataListJson.unsubscribedBanks != null) {
            for (Bank bank : bankInfoDataListJson.unsubscribedBanks) {
                addBankDataUnsubscribed(getBankDataDownloaded(bank));
            }
        }
    }

    private static DataBank getBankDataDownloaded(Bank bank) {
        DataBank dataBank = new DataBank();
        dataBank.setBankUUID(bank.bankUUID);
        dataBank.setLabel(bank.label);
        dataBank.setLinkStore(bank.link_store_android);
        dataBank.setTags(bank.tags);
        dataBank.setLogoHome(bank.logo_home);
        dataBank.setLogoSearch(bank.logo_search);
        dataBank.setEmail(bank.support_email);
        dataBank.setPhoneNumber(bank.support_phone);
        dataBank.setPhoneNumberForeign(bank.support_phone_foreign);
        dataBank.setSupportOpeningTime(bank.support_opening_time);
        return dataBank;
    }

    public static List<DataBank> getSubscribedDataBankList() {
        return subscribedDataBankList;
    }

    public static List<DataBank> getUnsubscribedDataBankList() {
        return unsubscribedDataBankList;
    }

    public static Map<String, DataBank> getSubscribedDataBankMap() {
        Map<String, DataBank> subscribedBankMap = new HashMap<>();
        for (DataBank item : subscribedDataBankList) {
            subscribedBankMap.put(item.getBankUUID(), item);
        }
        return subscribedBankMap;
    }

    public static Map<String, DataBank> getUnsubscribedDataBankMap() {
        Map<String, DataBank> unsubscribedBankMap = new HashMap<>();
        for (DataBank item : unsubscribedDataBankList) {
            unsubscribedBankMap.put(item.getBankUUID(), item);
        }
        return unsubscribedBankMap;
    }

}
