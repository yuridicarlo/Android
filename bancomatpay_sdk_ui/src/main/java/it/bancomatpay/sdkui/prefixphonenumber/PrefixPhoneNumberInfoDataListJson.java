package it.bancomatpay.sdkui.prefixphonenumber;

import androidx.annotation.NonNull;

import java.util.List;

public class PrefixPhoneNumberInfoDataListJson {

    public String version;
    List<PrefixPhoneNumber> prefixPhoneNumberList;

    @NonNull
    @Override
    public String toString() {
        return "PrefixPhoneNumberInfoDataListJson{" +
                "version='" + version + '\'' +
                ", prefixPhoneNumberList=" + prefixPhoneNumberList +
                '}';
    }

}
