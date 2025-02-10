package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.AtmWithdrawalStateType;

public class DtoConfirmAtmWithdrawalResponse implements Serializable {

	private AtmWithdrawalStateType atmWithdrawalState;

	public AtmWithdrawalStateType getAtmWithdrawalState() {
		return atmWithdrawalState;
	}

	public void setAtmWithdrawalState(AtmWithdrawalStateType atmWithdrawalState) {
		this.atmWithdrawalState = atmWithdrawalState;
	}
}
