package it.bancomatpay.sdkui.utilities;


import it.bancomatpay.sdk.manager.task.model.DateDisplayData;


public class TransactionSeparator implements DateDisplayData {

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
        return dateName;
    }

    @Override
    public String getShortDateName() {
        return shortDateName;
    }

}
