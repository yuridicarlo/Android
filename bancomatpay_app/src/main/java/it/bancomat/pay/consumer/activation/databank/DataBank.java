package it.bancomat.pay.consumer.activation.databank;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class DataBank implements Serializable {

    private String bankUUID;
    private boolean multiIban;
    private List<InstrumentData> instrument;
    private String label;
    private String logoSearch;
    private String logoHome;
    private String linkStore;
    private String tags;
    private String email;
    private String phoneNumber;
    private String phoneNumberForeign;
    private String support_opening_time;

    public boolean checkTags(String filter) {
        return StringUtils.contains(tags, filter);
    }

    public String getBankUUID() {
        return bankUUID;
    }

    public void setBankUUID(String bankUUID) {
        this.bankUUID = bankUUID;
    }

    public boolean isMultiIban() {
        return multiIban;
    }

    public void setMultiIban(boolean multiIban) {
        this.multiIban = multiIban;
    }

    public List<InstrumentData> getInstrument() {
        return instrument;
    }

    public void setInstrument(List<InstrumentData> instrument) {
        this.instrument = instrument;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLogoSearch() {
        return logoSearch;
    }

    public void setLogoSearch(String logoSearch) {
        this.logoSearch = logoSearch;
    }

    public String getLogoHome() {
        return logoHome;
    }

    public void setLogoHome(String logoHome) {
        this.logoHome = logoHome;
    }

    public String getLinkStore() {
        return linkStore;
    }

    public void setLinkStore(String linkStore) {
        this.linkStore = linkStore;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberForeign() {
        return phoneNumberForeign;
    }

    public void setPhoneNumberForeign(String phoneNumberForeign) {
        this.phoneNumberForeign = phoneNumberForeign;
    }

    public String getSupportOpeningTime() {
        return support_opening_time;
    }

    public void setSupportOpeningTime(String supportOpeningTime) {
        this.support_opening_time = supportOpeningTime;
    }

}
