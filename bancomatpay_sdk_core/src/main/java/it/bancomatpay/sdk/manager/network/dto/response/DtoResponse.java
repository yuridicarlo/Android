package it.bancomatpay.sdk.manager.network.dto.response;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DtoResponse<T> implements Serializable {

	protected DtoAppResponse<T> dtoAppResponse ;

	public void setDtoAppResponse(DtoAppResponse<T> dtoAppResponse) {
		this.dtoAppResponse = dtoAppResponse;
	}

	public DtoAppResponse<T> getDtoAppResponse() {
		return dtoAppResponse;
	}

	@NonNull
	@Override
	public String toString() {
		return "DtoResponse{" +
				"dtoAppResponse=" + dtoAppResponse +
				'}';
	}
}
