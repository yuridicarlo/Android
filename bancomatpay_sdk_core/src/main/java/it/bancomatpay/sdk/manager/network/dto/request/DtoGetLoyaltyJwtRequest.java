package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoGetLoyaltyJwtRequest implements Serializable {

	private String loyaltyToken;

	public String getLoyaltyToken() {
		return loyaltyToken;
	}

	public void setLoyaltyToken(String loyaltyToken) {
		this.loyaltyToken = loyaltyToken;
	}

}

