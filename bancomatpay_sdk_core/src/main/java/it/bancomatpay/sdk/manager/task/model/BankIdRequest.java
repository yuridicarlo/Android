package it.bancomatpay.sdk.manager.task.model;

import java.util.Date;

public class BankIdRequest {

    private String requestId;
    private Date requestDateTime;
    private BankIdMerchantData bankIdMerchantData;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(Date requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public BankIdMerchantData getBankIdMerchantData() {
        return bankIdMerchantData;
    }

    public void setBankIdMerchantData(BankIdMerchantData bankIdMerchantData) {
        this.bankIdMerchantData = bankIdMerchantData;
    }

}
