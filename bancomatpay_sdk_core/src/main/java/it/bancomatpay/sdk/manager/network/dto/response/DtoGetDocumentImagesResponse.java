package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoGetDocumentImagesResponse implements Serializable {

	private String frontImage;
	private String backImage;
	private String documentUuid;

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
