package it.bancomatpay.sdk.manager.network.dto.request;

import androidx.annotation.NonNull;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoAppInfo;
import it.bancomatpay.sdk.manager.network.dto.DtoMobileInfo;
import it.bancomatpay.sdk.manager.network.dto.DtoUserInfo;

public class DtoHandleRequest implements Serializable {

    private DtoAppRequest<?> dtoAppRequest;
    private DtoMobileInfo dtoMobileInfo;
    private DtoAppInfo dtoAppInfo;
    private DtoUserInfo dtoUserInfo;

    public DtoAppRequest<?> getDtoAppRequest() {
        return dtoAppRequest;
    }

    public void setDtoAppRequest(DtoAppRequest<?> dtoAppRequest) {
        this.dtoAppRequest = dtoAppRequest;
    }

    public DtoMobileInfo getDtoMobileInfo() {
        return dtoMobileInfo;
    }

    public void setDtoMobileInfo(DtoMobileInfo dtoMobileInfo) {
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
        return "DtoHandleRequest{" +
                "dtoAppRequest=" + dtoAppRequest +
                ", dtoMobileInfo=" + dtoMobileInfo +
                ", dtoUserInfo=" + dtoUserInfo +
                '}';
    }

}
