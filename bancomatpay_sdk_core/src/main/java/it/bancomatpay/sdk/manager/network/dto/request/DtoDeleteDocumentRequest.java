package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoDeleteDocumentRequest implements Serializable {

    private String documentUuid;

    public String getDocumentUuid() {
        return documentUuid;
    }

    public void setDocumentUuid(String documentUuid) {
        this.documentUuid = documentUuid;
    }


}
