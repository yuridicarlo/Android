package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.util.List;

public class DtoGetBlackListContactsRequest implements Serializable {

    protected List<String> msisdns;

    public List<String> getMsisdns() {
        return msisdns;
    }

    public void setMsisdns(List<String> msisdns) {
        this.msisdns = msisdns;
    }

}
