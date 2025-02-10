package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoUserMonitoringRequest implements Serializable {

    private String bankUUID;
    private String tag;
    private String event;
    private String note;


    public DtoUserMonitoringRequest() {
    }

    public String getBankUUID() {
        return bankUUID;
    }

    public void setBankUUID(String bankUUID) {
        this.bankUUID = bankUUID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
