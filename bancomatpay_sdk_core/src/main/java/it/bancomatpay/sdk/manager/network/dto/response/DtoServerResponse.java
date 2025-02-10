package it.bancomatpay.sdk.manager.network.dto.response;


import androidx.annotation.NonNull;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoSvc;

public class DtoServerResponse implements Serializable {
	
	protected DtoSvc dtoSvcResponse;

	public DtoSvc getDtoSvc() {
		return dtoSvcResponse;
	}

	public void setSvcRest(DtoSvc dtoSvcResponse) {
		this.dtoSvcResponse = dtoSvcResponse;
	}

	@NonNull
	@Override
	public String toString() {
		return "DtoServerResponse{" +
				"dtoSvcResponse=" + dtoSvcResponse +
				'}';
	}
}
