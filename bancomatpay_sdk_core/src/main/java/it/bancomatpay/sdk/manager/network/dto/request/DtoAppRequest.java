package it.bancomatpay.sdk.manager.network.dto.request;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class DtoAppRequest<T> implements Serializable {

    private String cmd;
    private T req;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public T getReq() {
        return req;
    }

    public void setReq(T req) {
        this.req = req;
    }

    @NonNull
    @Override
    public String toString() {
        return "DtoAppRequest{" +
                "cmd='" + cmd + '\'' +
                ", req=" + req +
                '}';
    }

}
