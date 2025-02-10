package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoBankIdAddress;

public class DtoGetBankIdContactsResponse implements Serializable {

	private String email;
	private List<DtoBankIdAddress> dtoBankIdAddresses;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<DtoBankIdAddress> getDtoBankIdAddresses() {
		return dtoBankIdAddresses;
	}

	public void setDtoBankIdAddresses(List<DtoBankIdAddress> dtoBankIdAddresses) {
		this.dtoBankIdAddresses = dtoBankIdAddresses;
	}

}
