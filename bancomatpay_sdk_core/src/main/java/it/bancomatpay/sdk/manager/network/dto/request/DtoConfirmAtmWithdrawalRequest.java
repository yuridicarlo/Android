package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoConfirmAtmWithdrawalRequest implements Serializable {

	private String msisdn;
	private String tag;
	private Long shopId;
	private String amount;
	private String authorizationToken;
	private String qrCodeId;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAuthorizationToken() {
		return authorizationToken;
	}

	public void setAuthorizationToken(String authorizationToken) {
		this.authorizationToken = authorizationToken;
	}

	public String getQrCodeId() {
		return qrCodeId;
	}

	public void setQrCodeId(String qrCodeId) {
		this.qrCodeId = qrCodeId;
	}

}
