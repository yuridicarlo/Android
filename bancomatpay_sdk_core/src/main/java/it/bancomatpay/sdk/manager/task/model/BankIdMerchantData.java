package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class BankIdMerchantData implements Serializable {

	private String merchantTag;
	private String merchantName;

	public String getMerchantTag() {
		return merchantTag;
	}

	public void setMerchantTag(String merchantTag) {
		this.merchantTag = merchantTag;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

}
