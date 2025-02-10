package it.bancomatpay.sdk.manager.network.dto.request;

import java.math.BigInteger;

public class DtoConfirmPetrolPaymentRequest {

	private String tag;
	private String shopId;
	private BigInteger tillId;
	private String amount;
	private String authorizationToken;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public BigInteger getTillId() {
		return tillId;
	}

	public void setTillId(BigInteger tillId) {
		this.tillId = tillId;
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

}
