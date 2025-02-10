package it.bancomatpay.sdk.manager.security.signature;

import android.content.Context;

public class NoMessageSignature implements MessageSignature, MessageSignatureVerify {

	@Override
	public String getMessageSignature(String message, Context context) {
		return "";
	}

	@Override
	public boolean checkMessageSignature(String message, String signature) {
		return true;
	}

}
