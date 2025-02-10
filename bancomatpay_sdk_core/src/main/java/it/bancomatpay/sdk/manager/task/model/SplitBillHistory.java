package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import it.bancomatpay.sdk.manager.utilities.Mapper;

public class SplitBillHistory implements DateDisplayData, Serializable {

    private String splitBillUUID;

    private String amount;

    private String requestDate;

    private String causal;

    private String description;

    private List<SplitBeneficiary> splitBeneficiary;

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

    public List<SplitBeneficiary> getSplitBeneficiary() {
        return splitBeneficiary;
    }

    public void setSplitBeneficiary(List<SplitBeneficiary> splitBeneficiary) {
        this.splitBeneficiary = splitBeneficiary;
    }

    @Override
    public String getDateName() {
        String dateFormatted = new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(Mapper.getDate(requestDate));
        dateFormatted = dateFormatted.substring(0,3) + dateFormatted.substring(3,4).toUpperCase()+dateFormatted.substring(4);
        return dateFormatted;
    }

    @Override
    public String getShortDateName() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Mapper.getDate(requestDate));
    }
}
