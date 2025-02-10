package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoBankIdMerchant;

public class DtoDeleteBankIdBlacklistRequest implements Serializable {

	private DtoBankIdMerchant dtoBankIdMerchant;

	public DtoBankIdMerchant getDtoBankIdMerchant() {
		return dtoBankIdMerchant;
	}

	public void setDtoBankIdMerchant(DtoBankIdMerchant dtoBankIdMerchant) {
		this.dtoBankIdMerchant = dtoBankIdMerchant;
	}

}
