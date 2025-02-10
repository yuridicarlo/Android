package it.bancomat.pay.consumer.network.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoAppInfo;
import it.bancomatpay.sdk.manager.network.dto.DtoUserInfo;
import it.bancomatpay.sdk.manager.network.dto.request.DtoAppRequest;

public class AppDtoHandleRequest implements Serializable {

    private DtoAppRequest<?> dtoAppRequest;
    private AppDtoMobileInfo dtoMobileInfo;
    private DtoAppInfo dtoAppInfo;
    private DtoUserInfo dtoUserInfo;

    public DtoAppRequest<?> getDtoAppRequest() {
        return dtoAppRequest;
    }

    public void setDtoAppRequest(DtoAppRequest<?> dtoAppRequest) {
        this.dtoAppRequest = dtoAppRequest;
    }

    public AppDtoMobileInfo getDtoMobileInfo() {
        return dtoMobileInfo;
    }

    public void setDtoMobileInfo(AppDtoMobileInfo dtoMobileInfo) {
        this.dtoMobileInfo = dtoMobileInfo;
    }

    public DtoAppInfo getDtoAppInfo() {
        return dtoAppInfo;
    }

    public void setDtoAppInfo(DtoAppInfo dtoAppInfo) {
        this.dtoAppInfo = dtoAppInfo;
    }

    public DtoUserInfo getDtoUserInfo() {
        return dtoUserInfo;
    }

    public void setDtoUserInfo(DtoUserInfo dtoUserInfo) {
        this.dtoUserInfo = dtoUserInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppDtoHandleRequest{" +
                "dtoAppRequest=" + dtoAppRequest +
                ", dtoMobileInfo=" + dtoMobileInfo +
                ", dtoUserInfo=" + dtoUserInfo +
                '}';
    }

}
