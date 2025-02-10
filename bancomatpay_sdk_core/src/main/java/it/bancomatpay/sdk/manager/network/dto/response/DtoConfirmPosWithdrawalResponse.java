package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.PosWithdrawalStateType;

public class DtoConfirmPosWithdrawalResponse implements Serializable {

	private PosWithdrawalStateType posWithdrawalState;

	public PosWithdrawalStateType getPosWithdrawalState() {
		return posWithdrawalState;
	}

	public void setPosWithdrawalState(PosWithdrawalStateType posWithdrawalState) {
		this.posWithdrawalState = posWithdrawalState;
	}

}
