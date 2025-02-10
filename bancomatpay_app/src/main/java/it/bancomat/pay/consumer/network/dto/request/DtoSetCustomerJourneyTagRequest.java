package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoCustomerJourneyTag;

public class DtoSetCustomerJourneyTagRequest implements Serializable {

	private List<DtoCustomerJourneyTag> dtoCustomerJourneyTags;

	public List<DtoCustomerJourneyTag> getDtoCustomerJourneyTags() {
		return dtoCustomerJourneyTags;
	}

	public void setDtoCustomerJourneyTags(List<DtoCustomerJourneyTag> dtoCustomerJourneyTags) {
		this.dtoCustomerJourneyTags = dtoCustomerJourneyTags;
	}

}
