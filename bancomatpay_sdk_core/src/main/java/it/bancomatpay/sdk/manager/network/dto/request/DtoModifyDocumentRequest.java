package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoDocument;

public class DtoModifyDocumentRequest implements Serializable {

    private DtoDocument dtoDocument;

    public DtoDocument getDtoDocument() {
        return dtoDocument;
    }

    public void setDtoDocument(DtoDocument dtoDocument) {
        this.dtoDocument = dtoDocument;
    }

}
