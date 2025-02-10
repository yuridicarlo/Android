package it.bancomatpay.sdk.manager.task.model;

import it.bancomatpay.sdk.manager.network.dto.AtmWithdrawalStateType;

public class AtmConfirmWithdrawalData {

	private AtmWithdrawalStateType withdrawalState;

	public AtmWithdrawalStateType getWithdrawalState() {
		return withdrawalState;
	}

	public void setWithdrawalState(AtmWithdrawalStateType withdrawalState) {
		this.withdrawalState = withdrawalState;
	}

}
