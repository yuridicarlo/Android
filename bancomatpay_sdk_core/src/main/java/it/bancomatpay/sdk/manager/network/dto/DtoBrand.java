package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoBrand implements Serializable {


	public enum LoyaltyCardTypeEnum {
		KNOWN_BRAND, UNKNOWN_BRAND
	}

	private LoyaltyCardTypeEnum type;
	private String brandUuid;
	private String hexColor;
	private String brandLogoUrl;
	private String brandName;
	private String brandLogoImage;
	private boolean light;

	public LoyaltyCardTypeEnum getType() {
		return type;
	}

	public void setType(LoyaltyCardTypeEnum type) {
		this.type = type;
	}

	public String getBrandUuid() {
		return brandUuid;
	}

	public void setBrandUuid(String brandUuid) {
		this.brandUuid = brandUuid;
	}

	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}

	public String getBrandLogoUrl() {
		return brandLogoUrl;
	}

	public void setBrandLogoUrl(String brandLogoUrl) {
		this.brandLogoUrl = brandLogoUrl;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandLogoImage() {
		return brandLogoImage;
	}

	public void setBrandLogoImage(String brandLogoImage) {
		this.brandLogoImage = brandLogoImage;
	}

	public boolean isLight() {
		return light;
	}

	public void setLight(boolean light) {
		this.light = light;
	}

}
