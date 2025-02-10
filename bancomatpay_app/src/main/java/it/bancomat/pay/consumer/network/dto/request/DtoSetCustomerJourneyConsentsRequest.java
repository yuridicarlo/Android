package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoCustomerJourneyConsents;

public class DtoSetCustomerJourneyConsentsRequest implements Serializable {

	protected DtoCustomerJourneyConsents consents;

	public DtoCustomerJourneyConsents getConsents() {
		return consents;
	}

	public void setConsents(DtoCustomerJourneyConsents consents) {
		this.consents = consents;
	}



}
