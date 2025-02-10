package it.bancomatpay.sdk.manager.network.dto.response;

import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoSplitBillDetail;

public class DtoSplitBillHistoryDetailResponse {

    private List<DtoSplitBillDetail> splitBillDetails;

    public List<DtoSplitBillDetail> getSplitBillDetails() {
        return splitBillDetails;
    }

    public void setSplitBillDetails(List<DtoSplitBillDetail> splitBillDetails) {
        this.splitBillDetails = splitBillDetails;
    }
}
