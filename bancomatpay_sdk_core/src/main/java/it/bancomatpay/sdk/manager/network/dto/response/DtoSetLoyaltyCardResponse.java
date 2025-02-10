package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoSetLoyaltyCardResponse implements Serializable {

	private String loyaltyCardId;

	public String getLoyaltyCardId() {
		return loyaltyCardId;
	}

	public void setLoyaltyCardId(String loyaltyCardId) {
		this.loyaltyCardId = loyaltyCardId;
	}

}
