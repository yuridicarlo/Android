package it.bancomatpay.sdk.manager.network.dto;

public class DtoBankIdRequest {

	protected String requestId;
	protected String requestDateTime;
	protected DtoBankIdMerchant dtoBankIdMerchant;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestDateTime() {
		return requestDateTime;
	}

	public void setRequestDateTime(String requestDateTime) {
		this.requestDateTime = requestDateTime;
	}

	public DtoBankIdMerchant getDtoBankIdMerchant() {
		return dtoBankIdMerchant;
	}

	public void setDtoBankIdMerchant(DtoBankIdMerchant dtoBankIdMerchant) {
		this.dtoBankIdMerchant = dtoBankIdMerchant;
	}

}
