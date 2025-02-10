package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoDeleteLoyaltyCardRequest implements Serializable {

	private String loyaltyCardId;
	private String barCodeNumber;

	public String getLoyaltyCardId() {
		return loyaltyCardId;
	}

	public void setLoyaltyCardId(String loyaltyCardId) {
		this.loyaltyCardId = loyaltyCardId;
	}

	public String getBarCodeNumber() {
		return barCodeNumber;
	}

	public void setBarCodeNumber(String barCodeNumber) {
		this.barCodeNumber = barCodeNumber;
	}

}
