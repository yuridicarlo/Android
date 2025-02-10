package it.bancomatpay.sdk.manager.task.model;

public class ConfirmPetrolPaymentData {

	public enum EPetrolPaymentState {
		EXECUTED, FAILED
	}

	private EPetrolPaymentState petrolPaymentState;

	public EPetrolPaymentState getPetrolPaymentState() {
		return petrolPaymentState;
	}

	public void setPetrolPaymentState(EPetrolPaymentState petrolPaymentState) {
		this.petrolPaymentState = petrolPaymentState;
	}

}
