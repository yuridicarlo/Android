package it.bancomatpay.sdkui.prefixphonenumber;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bancomatpay.sdk.manager.utilities.JsonMinify;
import it.bancomatpay.sdkui.utilities.JsonFileUtil;

public class DataPrefixPhoneNumberManager {

    private static final String JSON_FILE_NAME = "PrefixPhoneNumberInfoDataList.json";

    private final static List<DataPrefixPhoneNumber> fullDataList = new ArrayList<>();
    private final static Map<String, DataPrefixPhoneNumber> fullDataMap = new HashMap<>();

    private static void addPrefixData(DataPrefixPhoneNumber dataPrefixPhoneNumber) {
        fullDataList.add(dataPrefixPhoneNumber);
        fullDataMap.put(dataPrefixPhoneNumber.getCountryCode(), dataPrefixPhoneNumber);
    }

    public static List<DataPrefixPhoneNumber> getFullDataList() {
        return fullDataList;
    }

    public static ArrayList<DataPrefixPhoneNumber> getDataList(List<String> dataIds) {
        ArrayList<DataPrefixPhoneNumber> dataList = new ArrayList<>();
        if (dataIds != null) {
            for (String id : dataIds) {
                if (fullDataMap.containsKey(id)) {
                    dataList.add(fullDataMap.get(id));
                }
            }
        }
        return dataList;
    }

    public static DataPrefixPhoneNumber getData(String countryCode) {
        return fullDataMap.get(countryCode);
    }

    static {
        Gson gson = new Gson();
        PrefixPhoneNumberInfoDataListJson infoDataListJson = gson.fromJson(
                JsonMinify.minify(JsonFileUtil.loadJSONFromAsset(JSON_FILE_NAME))
                , PrefixPhoneNumberInfoDataListJson.class);

        if (infoDataListJson.prefixPhoneNumberList != null) {
            for (PrefixPhoneNumber prefix : infoDataListJson.prefixPhoneNumberList) {
                addPrefixData(getPrefixData(prefix));
            }
        }
    }

    private static DataPrefixPhoneNumber getPrefixData(PrefixPhoneNumber prefix) {
        DataPrefixPhoneNumber dataPrefix = new DataPrefixPhoneNumber();
        dataPrefix.setLogoFlag(prefix.logoFlag);
        dataPrefix.setPrefix(prefix.prefix);
        dataPrefix.setCountry(JsonFileUtil.getStringId(prefix.country));
        dataPrefix.setCountryCode(prefix.countryCode);
        return dataPrefix;
    }

}
