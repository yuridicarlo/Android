package it.bancomat.pay.consumer.network;

import android.content.Context;

import java.util.Locale;

import it.bancomat.pay.consumer.network.dto.AppDtoHandleRequest;
import it.bancomat.pay.consumer.network.dto.AppDtoMobileInfo;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.AppConstants;
import it.bancomatpay.sdk.BuildConfig;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.EPlayServicesType;

public class AppMobileInfoEnrichment implements AppMessageEnrichment {

    @Override
    public AppDtoHandleRequest doEnrichment(DtoAppRequest<?> message, Context ctx) {

        String version = BuildConfig.APP_NAME_VERSION;
        String osVersion = android.os.Build.VERSION.RELEASE;
        String phoneModel = android.os.Build.MODEL;
        AppBancomatDataManager bcmDataManager = AppBancomatDataManager.getInstance();

        String mobileDeviceId = bcmDataManager.getMobileDevice().getUuid();

        String appId = AppConstants.APP_ID;
        String serviceProvider = AppConstants.SERVICE_PROVIDER;
        String language = Locale.getDefault().getLanguage();

        final AppDtoMobileInfo dtoMobileInfo = new AppDtoMobileInfo();

        dtoMobileInfo.setOsVersion(osVersion);
        dtoMobileInfo.setAppVersion(version);
        dtoMobileInfo.setPhoneModel(phoneModel);
        dtoMobileInfo.setPhoneLanguage(language);
        dtoMobileInfo.setMobileDeviceId(mobileDeviceId);
        dtoMobileInfo.setAppId(appId);
        dtoMobileInfo.setServiceProvider(serviceProvider);

        boolean hasHuaweiServices = BancomatDataManager.getInstance().getPlayServicesType() == EPlayServicesType.HUAWEI_SERVICES;
        dtoMobileInfo.setOsName(hasHuaweiServices ? AppConstants.OS_NAME + "_HMS" : AppConstants.OS_NAME);

        AppDtoHandleRequest request = new AppDtoHandleRequest();
        request.setDtoAppRequest(message);
        request.setDtoMobileInfo(dtoMobileInfo);

        return request;
    }

}
