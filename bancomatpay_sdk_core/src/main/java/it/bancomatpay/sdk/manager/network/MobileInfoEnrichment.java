package it.bancomatpay.sdk.manager.network;

import android.content.Context;

import java.util.Locale;

import it.bancomatpay.sdk.BuildConfig;
import it.bancomatpay.sdk.manager.network.dto.DtoMobileInfo;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;
import it.bancomatpay.sdk.manager.network.dto.request.DtoHandleRequest;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.EPlayServicesType;
import it.bancomatpay.sdk.manager.utilities.Constants;

public class MobileInfoEnrichment implements MessageEnrichment {

    @Override
    public DtoHandleRequest doEnrichment(DtoAppRequest<?> message, Context ctx) {

        String version = BuildConfig.APP_NAME_VERSION;
        String osVersion = android.os.Build.VERSION.RELEASE;
        String phoneModel = android.os.Build.MODEL;
        BancomatDataManager bcmDataManager = BancomatDataManager.getInstance();

        String mobileDeviceId = bcmDataManager.getMobileDevice().getUuid();

        String appId = Constants.APP_ID;
        String serviceProvider = Constants.SERVICE_PROVIDER;
        String language = Locale.getDefault().getLanguage();

        final DtoMobileInfo dtoMobileInfo = new DtoMobileInfo();

        dtoMobileInfo.setOsVersion(osVersion);
        dtoMobileInfo.setSdkVersion(version);
        dtoMobileInfo.setPhoneModel(phoneModel);
        dtoMobileInfo.setPhoneLanguage(language);
        dtoMobileInfo.setMobileDeviceId(mobileDeviceId);
        dtoMobileInfo.setAppId(appId);
        dtoMobileInfo.setServiceProvider(serviceProvider);

        boolean hasHuaweiServices = bcmDataManager.getPlayServicesType() == EPlayServicesType.HUAWEI_SERVICES;
        dtoMobileInfo.setOsName(hasHuaweiServices ? Constants.OS_NAME + "_HMS" : Constants.OS_NAME);

        DtoHandleRequest request = new DtoHandleRequest();
        request.setDtoAppRequest(message);
        request.setDtoMobileInfo(dtoMobileInfo);

        return request;
    }

}
