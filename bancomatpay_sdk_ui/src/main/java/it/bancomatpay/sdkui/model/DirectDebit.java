package it.bancomatpay.sdkui.model;

import java.io.Serializable;
import java.util.Date;

import it.bancomatpay.sdk.manager.network.dto.response.DirectDebitStatus;
import it.bancomatpay.sdk.manager.task.model.DirectDebitHistoryElement;

public class DirectDebit implements Serializable {

    private DirectDebitHistoryElement directDebitHistoryElement;

    public DirectDebit(DirectDebitHistoryElement directDebitHistoryElement) {
        this.directDebitHistoryElement = directDebitHistoryElement;
    }

    public Date getDisplayDate() {
        if (directDebitHistoryElement.getStartingDate() != null) {
            return directDebitHistoryElement.getStartingDate();
        } else if (directDebitHistoryElement.getAuthorizationDate() != null) {
            return directDebitHistoryElement.getAuthorizationDate();
        }
        return new Date();
    }

    public String getMerchantName() {
        return directDebitHistoryElement.getMerchantName();
    }

    public DirectDebitStatus getMerchantStatus() {
        return directDebitHistoryElement.getDirectDebitStatus();
    }

    public String getMerchantDescription() {
        return directDebitHistoryElement.getDirectDebitDescription();
    }

    public Date getMerchantEndingDate() {
        if (directDebitHistoryElement.getEndingDate() != null) {
            return directDebitHistoryElement.getEndingDate();
        }
        return new Date();

    }

    public String getMerchantIban(){
        return directDebitHistoryElement.getIban();
    }

}





