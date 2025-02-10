package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoBankIdAddress implements Serializable {

	private String careOf;
	private DtoAddress dtoAddress;
	private String country;
	private boolean billingAddress;
	private boolean shippingAddress;
	private boolean defaultBillingAddress;
	private boolean defaultShippingAddress;

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public DtoAddress getDtoAddress() {
		return dtoAddress;
	}

	public void setDtoAddress(DtoAddress dtoAddress) {
		this.dtoAddress = dtoAddress;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(boolean billingAddress) {
		this.billingAddress = billingAddress;
	}

	public boolean isShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(boolean shippingAddress) {
		this.shippingAddress = shippingAddress;
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

}
