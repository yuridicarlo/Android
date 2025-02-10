package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoBrand;

public class DtoSetLoyaltyCardRequest implements Serializable {

	private String barCodeNumber;
	private String barCodeType; //Valori riconosciuti CODE_39, CODE_128, EAN_8, EAN_13, UPC_A
	private DtoBrand dtoBrand;

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
