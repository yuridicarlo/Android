package it.bancomat.pay.consumer.biometric.exception;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class BiometryException extends Exception implements Serializable {

	private final int errorCode;
	private final CharSequence errString;

	public BiometryException(int errorCode, CharSequence errString) {
		this.errorCode = errorCode;
		this.errString = errString;
	}

	public int getErrorCode() {
		return errorCode;
	}

	@Nullable
	@Override
	public String getMessage() {
		if (errString == null) {
			return "errorCode " + errorCode;
		} else {
			return errString.toString();
		}
	}

}
