package it.bancomatpay.sdk.manager.task.model;

public class CameraPicture {

	private byte[] initialData;
	private String fileName;
	private boolean isCardDocument;

	public byte[] getInitialData() {
		return initialData;
	}

	public void setInitialData(byte[] initialData) {
		this.initialData = initialData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isCardDocument() {
		return isCardDocument;
	}

	public void setCardDocument(boolean cardDocument) {
		isCardDocument = cardDocument;
	}

}
