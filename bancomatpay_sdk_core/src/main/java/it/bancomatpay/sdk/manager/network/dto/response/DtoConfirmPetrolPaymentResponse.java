package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoConfirmPetrolPaymentResponse implements Serializable {

	protected String petrolPaymentState;

	public String getPetrolPaymentState() {
		return petrolPaymentState;
	}

	public void setPetrolPaymentState(String petrolPaymentState) {
		this.petrolPaymentState = petrolPaymentState;
	}

}
