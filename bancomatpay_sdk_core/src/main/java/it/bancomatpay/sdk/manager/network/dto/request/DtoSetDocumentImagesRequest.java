package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoSetDocumentImagesRequest implements Serializable {


    private String documentUuid;
    private String frontImage;
    private String backImage;


    public String getDocumentUuid() {
        return documentUuid;
    }

    public void setDocumentUuid(String documentUuid) {
        this.documentUuid = documentUuid;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }
}
