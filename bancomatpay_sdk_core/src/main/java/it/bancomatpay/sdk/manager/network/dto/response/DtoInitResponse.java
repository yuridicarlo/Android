package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

public class DtoInitResponse implements Serializable {

    protected String key;
    protected List<String> bankServiceList;

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        this.key = value;
    }

    public List<String> getBankServiceList() {
        return bankServiceList;
    }

    public void setBankServiceList(List<String> bankServiceList) {
        this.bankServiceList = bankServiceList;
    }

}
