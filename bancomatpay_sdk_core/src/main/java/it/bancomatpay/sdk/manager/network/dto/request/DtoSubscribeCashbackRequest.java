package it.bancomatpay.sdk.manager.network.dto.request;

public class DtoSubscribeCashbackRequest {

	private String authorizationToken;

	public String getAuthorizationToken() {
		return authorizationToken;
	}

	public void setAuthorizationToken(String authorizationToken) {
		this.authorizationToken = authorizationToken;
	}

}
