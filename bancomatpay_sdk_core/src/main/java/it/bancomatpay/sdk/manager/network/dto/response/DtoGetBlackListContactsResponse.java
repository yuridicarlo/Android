package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

public class DtoGetBlackListContactsResponse implements Serializable {

    protected List<String> blackListMsidns;

    public List<String> getBlackListMsidns() {
        return blackListMsidns;
    }

    public void setBlackListMsidns(List<String> blackListMsidns) {
        this.blackListMsidns = blackListMsidns;
    }

}
