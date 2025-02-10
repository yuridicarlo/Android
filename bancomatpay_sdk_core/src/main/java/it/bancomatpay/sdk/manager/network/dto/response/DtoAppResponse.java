package it.bancomatpay.sdk.manager.network.dto.response;

import androidx.annotation.NonNull;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoStatus;

public class DtoAppResponse<T> implements Serializable {

    protected String cmd;
    protected T res;
    protected DtoStatus dtoStatus;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public T getRes() {
        return res;
    }

    public void setRes(T res) {
        this.res = res;
    }

    public DtoStatus getDtoStatus() {
        return dtoStatus;
    }

    public void setDtoStatus(DtoStatus dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "DtoAppResponse{" +
                "cmd='" + cmd + '\'' +
                ", res=" + res +
                ", dtoStatus=" + dtoStatus +
                '}';
    }

}

