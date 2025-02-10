package it.bancomat.pay.consumer.network.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AppDtoMobileInfo implements Serializable {

    protected String osVersion;
    protected String appVersion;
    protected String phoneModel;
    protected String phoneLanguage;
    protected String mobileDeviceId;
    protected String appId;
    protected String serviceProvider;
    protected String isHceAvailable;
    protected String osName;

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getIsHceAvailable() {
        return isHceAvailable;
    }

    public void setIsHceAvailable(String isHceAvailable) {
        this.isHceAvailable = isHceAvailable;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneLanguage() {
        return phoneLanguage;
    }

    public void setPhoneLanguage(String phoneLanguage) {
        this.phoneLanguage = phoneLanguage;
    }

    public String getMobileDeviceId() {
        return mobileDeviceId;
    }

    public void setMobileDeviceId(String mobileDeviceId) {
        this.mobileDeviceId = mobileDeviceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppDtoMobileInfo{" +
                "osVersion='" + osVersion + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", phoneModel='" + phoneModel + '\'' +
                ", phoneLanguage='" + phoneLanguage + '\'' +
                ", mobileDeviceId='" + mobileDeviceId + '\'' +
                ", appId='" + appId + '\'' +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", isHceAvailable='" + isHceAvailable + '\'' +
                ", osName='" + osName + '\'' +
                '}';
    }
}
