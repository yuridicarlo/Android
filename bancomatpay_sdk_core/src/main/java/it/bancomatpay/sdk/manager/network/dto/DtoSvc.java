package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoSvc implements Serializable {

	protected String s;
	protected String b;
	
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "DtoSvc{" +
				"s='" + s + '\'' +
				", b='" + b + '\'' +
				'}';
	}
}
