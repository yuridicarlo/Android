package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class BpayCashbackTermsAndConditionsData implements Serializable {

	private String termsAndConditions;

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

}
