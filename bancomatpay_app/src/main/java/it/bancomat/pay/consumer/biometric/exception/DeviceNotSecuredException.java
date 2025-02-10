package it.bancomat.pay.consumer.biometric.exception;

import java.io.Serializable;

public class DeviceNotSecuredException extends Exception implements Serializable {

	public DeviceNotSecuredException(String message) {
		super(message);
	}

}
