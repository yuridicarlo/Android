package it.bancomatpay.sdk.manager.network.dto;

public class DtoSplitBill {

    private String splitBillUUID;

    private String amount;

    private String requestDate;

    private String causal;

    private String description;

    public String getSplitBillUUID() {
        return splitBillUUID;
    }

    public void setSplitBillUUID(String splitBillUUID) {
        this.splitBillUUID = splitBillUUID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
