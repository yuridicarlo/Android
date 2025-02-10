package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoLoyaltyCard;

public class DtoModifyLoyaltyCardResponse implements Serializable {

	private DtoLoyaltyCard dtoLoyaltyCard;

	public DtoLoyaltyCard getDtoLoyaltyCard() {
		return dtoLoyaltyCard;
	}

	public void setDtoLoyaltyCard(DtoLoyaltyCard dtoLoyaltyCard) {
		this.dtoLoyaltyCard = dtoLoyaltyCard;
	}

}
