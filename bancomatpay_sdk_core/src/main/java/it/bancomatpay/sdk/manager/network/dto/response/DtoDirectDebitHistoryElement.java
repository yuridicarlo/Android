package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoDirectDebitHistoryElement implements Serializable {

    private DirectDebitStatus directDebitStatus;
    private String startingDate;
    private String endingDate;
    private String authorizationDate;
    private String iban;
    private String merchantName;
    private String directDebitDescription;

    public String getAuthorizationDate() {
        return authorizationDate;
    }

    public void setAuthorizationDate(String authorizationDate) {
        this.authorizationDate = authorizationDate;
    }

    public String getDirectDebitDescription() {
        return directDebitDescription;
    }

    public void setDirectDebitDescription(String directDebitDescription) {
        this.directDebitDescription = directDebitDescription;
    }


    public DirectDebitStatus getDirectDebitStatus() {
        return directDebitStatus;
    }

    public void setDirectDebitStatus(DirectDebitStatus directDebitStatus) {
        this.directDebitStatus = directDebitStatus;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
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
}
