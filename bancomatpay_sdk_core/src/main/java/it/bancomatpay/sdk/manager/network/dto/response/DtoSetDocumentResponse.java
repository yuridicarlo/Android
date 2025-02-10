package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoSetDocumentResponse implements Serializable {

	private String documentUuid;

	public String getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(String documentUuid) {
		this.documentUuid = documentUuid;
	}

}
