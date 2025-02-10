package it.bancomatpay.sdk.core;

public class HttpError extends Error {

	private int code;

	public HttpError(int code) {
		super("Http Error: " + code);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
