package it.bancomatpay.sdkui.utilities;


import it.bancomatpay.sdk.manager.task.model.DateDisplayData;


public class DirectDebitSeparator implements DateDisplayData {

    private String dateName;
    private String shortDateName;

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public void setShortDateName(String shortDateName) {
        this.shortDateName = shortDateName;
    }

    @Override
    public String getDateName() {
        return dateName.substring(0,3) + dateName.substring(3,4).toUpperCase()+dateName.substring(4);
    }

    @Override
    public String getShortDateName() {
        return shortDateName;
    }

}
