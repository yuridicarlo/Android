package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.List;

public class LoyaltyJwtData implements Serializable {

	private String jwt;
	private String callbackUrl;
	private String bplayLoyaltyUrl;
	private List<String> whitelistUrls;

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getBplayLoyaltyUrl() {
		return bplayLoyaltyUrl;
	}

	public void setBplayLoyaltyUrl(String bplayLoyaltyUrl) {
		this.bplayLoyaltyUrl = bplayLoyaltyUrl;
	}

	public List<String> getWhitelistUrls() {
		return whitelistUrls;
	}

	public void setWhitelistUrls(List<String> whitelistUrls) {
		this.whitelistUrls = whitelistUrls;
	}

}

