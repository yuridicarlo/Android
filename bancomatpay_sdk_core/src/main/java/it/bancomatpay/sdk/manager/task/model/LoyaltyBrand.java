package it.bancomatpay.sdk.manager.task.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoBrand;

public class LoyaltyBrand implements Serializable, Comparable<LoyaltyBrand> {

	public static final String BRAND_UUID_ADD_CARD = "BRAND_UUID_ADD_CARD";

	private DtoBrand.LoyaltyCardTypeEnum cardType;
	private String brandUuid;
	private int cardColor;
	private String cardLogoUrl;
	private String brandName;
	private String cardImage;
	private boolean light;

	public DtoBrand.LoyaltyCardTypeEnum getCardType() {
		return cardType;
	}

	public void setCardType(DtoBrand.LoyaltyCardTypeEnum cardType) {
		this.cardType = cardType;
	}

	public String getBrandUuid() {
		return brandUuid;
	}

	public void setBrandUuid(String brandUuid) {
		this.brandUuid = brandUuid;
	}

	public int getCardColor() {
		return cardColor;
	}

	public void setCardColor(int cardColor) {
		this.cardColor = cardColor;
	}

	public String getCardLogoUrl() {
		return cardLogoUrl;
	}

	public void setCardLogoUrl(String cardLogoUrl) {
		this.cardLogoUrl = cardLogoUrl;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getCardImage() {
		return cardImage;
	}

	public void setCardImage(String cardImage) {
		this.cardImage = cardImage;
	}

	public boolean isLight() {
		return light;
	}

	public void setLight(boolean light) {
		this.light = light;
	}
	@Override
	public int compareTo(LoyaltyBrand brand) {
		return getBrandUuid().compareTo(brand.getBrandUuid());
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof LoyaltyBrand) {
			LoyaltyBrand brand = (LoyaltyBrand) obj;
			return getBrandName().equals(brand.getBrandName())
					&& getBrandUuid().equals(brand.getBrandUuid())
					&& getCardColor() == brand.getCardColor()
					&& getCardImage().equals(brand.getCardImage())
					&& getCardLogoUrl().equals(brand.getCardLogoUrl())
					&& getCardType() == brand.getCardType();
		} else {
			return super.equals(obj);
		}
	}


}
