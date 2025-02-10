package it.bancomatpay.sdk.manager.network.dto.response;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoBankIdRequest;

public class DtoGetBankIdRequestsResponse {

    protected List<DtoBankIdRequest> dtoBankIdRequests;


    public void setDtoBankIdRequests(List<DtoBankIdRequest> dtoBankIdRequests) {
        this.dtoBankIdRequests = dtoBankIdRequests;
    }

    public List<DtoBankIdRequest> getDtoBankIdRequests() {
        if (dtoBankIdRequests == null) {
            dtoBankIdRequests = new ArrayList<>();
        }
        return this.dtoBankIdRequests;
    }
}
