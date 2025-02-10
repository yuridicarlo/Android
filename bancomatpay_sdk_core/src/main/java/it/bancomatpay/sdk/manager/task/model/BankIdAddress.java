package it.bancomatpay.sdk.manager.task.model;

import android.text.TextUtils;

import java.io.Serializable;

public class BankIdAddress implements Serializable {

	public enum EBankIdAddressType {
		BILLING, SHIPPING, BOTH
	}

	private String careOf;
	private Address address;
	private String country;
	private EBankIdAddressType addressType;
	private boolean defaultBillingAddress;
	private boolean defaultShippingAddress;

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public EBankIdAddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(EBankIdAddressType addressType) {
		this.addressType = addressType;
	}

	public boolean isDefaultBillingAddress() {
		return defaultBillingAddress;
	}

	public void setDefaultBillingAddress(boolean defaultBillingAddress) {
		this.defaultBillingAddress = defaultBillingAddress;
	}

	public boolean isDefaultShippingAddress() {
		return defaultShippingAddress;
	}

	public void setDefaultShippingAddress(boolean defaultShippingAddress) {
		this.defaultShippingAddress = defaultShippingAddress;
	}

	public String getAddressId() {
		String sRet = "";
		if (getAddress() != null) {
			String completeAddress = getAddress().getCity() + getAddress().getProvince() + getAddress().getPostalCode() + getAddress().getStreet();
			sRet += completeAddress;
		}
		if (!TextUtils.isEmpty(getCareOf())) {
			StringBuilder builder = new StringBuilder(sRet);
			builder.insert(0, sRet);
			sRet = builder.toString();
		}
		return sRet;
	}

}
