package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoLoyaltyCard;

public class DtoGetLoyaltyCardsResponse implements Serializable {

	private List<DtoLoyaltyCard> dtoLoyaltyCards;

	public List<DtoLoyaltyCard> getDtoLoyaltyCards() {
		return dtoLoyaltyCards;
	}

	public void setDtoLoyaltyCards(List<DtoLoyaltyCard> dtoLoyaltyCards) {
		this.dtoLoyaltyCards = dtoLoyaltyCards;
	}

}
