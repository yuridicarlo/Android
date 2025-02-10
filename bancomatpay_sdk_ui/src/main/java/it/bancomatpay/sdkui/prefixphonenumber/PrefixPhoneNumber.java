package it.bancomatpay.sdkui.prefixphonenumber;

import androidx.annotation.NonNull;

public class PrefixPhoneNumber {

    String logoFlag;
    String prefix;
    String country;
    String countryCode;

    @NonNull
    @Override
    public String toString() {
        return "PrefixPhoneNumber{" +
                "logoFlag='" + logoFlag + '\'' +
                ", prefix='" + prefix + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
