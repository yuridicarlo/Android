package it.bancomatpay.sdk.manager.task.model;

import it.bancomatpay.sdk.manager.network.dto.PosWithdrawalStateType;

public class PosConfirmWithdrawalData {

	private PosWithdrawalStateType withdrawalState;

	public PosWithdrawalStateType getWithdrawalState() {
		return withdrawalState;
	}

	public void setWithdrawalState(PosWithdrawalStateType withdrawalState) {
		this.withdrawalState = withdrawalState;
	}

}
