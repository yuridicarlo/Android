package it.bancomat.pay.consumer.network.security.signature;

import android.content.Context;

public class AppNoMessageSignature implements AppMessageSignature, AppMessageSignatureVerify {

	@Override
	public String getMessageSignature(String message, Context context) {
		return "";
	}

	@Override
	public boolean checkMessageSignature(String message, String signature) {
		return true;
	}

}
