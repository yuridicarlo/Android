package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoBankIdMerchant implements Serializable {

	private String merchantTag;
	private String businessName;

	public String getMerchantTag() {
		return merchantTag;
	}

	public void setMerchantTag(String merchantTag) {
		this.merchantTag = merchantTag;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

}
