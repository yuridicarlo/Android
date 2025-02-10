package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoLoyaltyCard implements Serializable {

	private String loyaltyCardId;
	private String barCodeNumber;
	private String barCodeType;
	private DtoBrand dtoBrand;

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

	public String getBarCodeType() {
		return barCodeType;
	}

	public void setBarCodeType(String barCodeType) {
		this.barCodeType = barCodeType;
	}

	public DtoBrand getDtoBrand() {
		return dtoBrand;
	}

	public void setDtoBrand(DtoBrand dtoBrand) {
		this.dtoBrand = dtoBrand;
	}

}
