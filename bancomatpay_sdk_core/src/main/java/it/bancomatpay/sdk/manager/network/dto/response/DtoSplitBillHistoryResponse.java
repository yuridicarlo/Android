package it.bancomatpay.sdk.manager.network.dto.response;

import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoSplitBill;

public class DtoSplitBillHistoryResponse {

    private List<DtoSplitBill> splitBills;


    public List<DtoSplitBill> getSplitBills() {
        return splitBills;
    }

    public void setSplitBills(List<DtoSplitBill> splitBills) {
        this.splitBills = splitBills;
    }
}
