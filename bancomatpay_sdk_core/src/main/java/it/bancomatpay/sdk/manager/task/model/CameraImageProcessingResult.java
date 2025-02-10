package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class CameraImageProcessingResult implements Serializable {

	private String base64Image;
	private boolean isCardDocument;

	public String getBase64Image() {
		return base64Image;
	}

	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	public boolean isCardDocument() {
		return isCardDocument;
	}

	public void setCardDocument(boolean cardDocument) {
		isCardDocument = cardDocument;
	}

}
