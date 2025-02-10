package it.bancomatpay.sdkui.prefixphonenumber;

import java.io.Serializable;

public class DataPrefixPhoneNumber implements Serializable {

    private String logoFlag;
    private String prefix;
    private String country;
    private String countryCode;

    public String getLogoFlag() {
        return logoFlag;
    }

    public void setLogoFlag(String logoFlag) {
        this.logoFlag = logoFlag;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
