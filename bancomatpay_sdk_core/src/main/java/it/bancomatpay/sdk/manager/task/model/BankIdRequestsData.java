package it.bancomatpay.sdk.manager.task.model;

import java.util.List;

public class BankIdRequestsData {

    private List<BankIdRequest> bankIdRequests;

    public List<BankIdRequest> getBankIdRequestsList() {
        return bankIdRequests;
    }

    public void setBankIdRequestsList(List<BankIdRequest> bankIdRequests) {
        this.bankIdRequests = bankIdRequests;
    }

}
