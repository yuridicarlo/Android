package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.Date;

import it.bancomatpay.sdk.manager.network.dto.response.DirectDebitStatus;

public class DirectDebitHistoryElement implements Serializable {

    private DirectDebitStatus directDebitStatus;
    private Date startingDate;
    private Date endingDate;
    private Date authorizationDate;
    private String iban;
    private String merchantName;
    private String directDebitDescription;

    public DirectDebitStatus getDirectDebitStatus() {
        return directDebitStatus;
    }

    public void setDirectDebitStatus(DirectDebitStatus directDebitStatus) {
        this.directDebitStatus = directDebitStatus;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public Date getAuthorizationDate() {
        return authorizationDate;
    }

    public void setAuthorizationDate(Date authorizationDate) {
        this.authorizationDate = authorizationDate;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getDirectDebitDescription() {
        return directDebitDescription;
    }

    public void setDirectDebitDescription(String directDebitDescription) {
        this.directDebitDescription = directDebitDescription;
    }





}
