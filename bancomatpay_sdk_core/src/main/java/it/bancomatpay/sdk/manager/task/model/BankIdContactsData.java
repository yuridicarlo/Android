package it.bancomatpay.sdk.manager.task.model;

import java.util.List;

public class BankIdContactsData {

	private String email;
	private List<BankIdAddress> bankIdAddresses;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<BankIdAddress> getBankIdAddresses() {
		return bankIdAddresses;
	}

	public void setBankIdAddress(List<BankIdAddress> bankIdAddresses) {
		this.bankIdAddresses = bankIdAddresses;
	}

}
