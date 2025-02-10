package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoDocument;

public class DtoGetDocumentsResponse implements Serializable {

	private List<DtoDocument> dtoDocuments;

	public List<DtoDocument> getDtoDocuments() {
		return dtoDocuments;
	}

	public void setDtoDocuments(List<DtoDocument> dtoDocuments) {
		this.dtoDocuments = dtoDocuments;
	}

}
