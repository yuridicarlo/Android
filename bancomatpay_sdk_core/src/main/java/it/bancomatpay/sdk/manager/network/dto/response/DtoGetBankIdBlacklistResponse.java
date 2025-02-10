package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoBankIdMerchant;

public class DtoGetBankIdBlacklistResponse implements Serializable {

	private List<DtoBankIdMerchant> dtoBankIdMerchants;

	public List<DtoBankIdMerchant> getDtoBankIdMerchants() {
		return dtoBankIdMerchants;
	}

	public void setDtoBankIdMerchants(List<DtoBankIdMerchant> dtoBankIdMerchants) {
		this.dtoBankIdMerchants = dtoBankIdMerchants;
	}

}
