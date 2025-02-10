package it.bancomatpay.sdk.manager.task.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class LoyaltyCard implements Serializable, Comparable<LoyaltyCard> {

	private String loyaltyCardId;
	private String barCodeNumber;
	private String barCodeType;
	private LoyaltyBrand brand;

	private int operationCounter;

	public String getLoyaltyCardId() {
		return loyaltyCardId;
	}

	public void setLoyaltyCardId(String loyaltyCardId) {
		this.loyaltyCardId = loyaltyCardId;
	}

	public String getBarCodeNumber() {
		return barCodeNumber;
	}

	public void setBarCodeNumber(String barCodeNumber) {
		this.barCodeNumber = barCodeNumber;
	}

	public String getBarCodeType() {
		return barCodeType;
	}

	public void setBarCodeType(String barCodeType) {
		this.barCodeType = barCodeType;
	}

	public LoyaltyBrand getBrand() {
		return brand;
	}

	public void setBrand(LoyaltyBrand brand) {
		this.brand = brand;
	}

	public int getOperationCounter() {
		return operationCounter;
	}

	public void setOperationCounter(int operationCounter) {
		this.operationCounter = operationCounter;
	}

	@Override
	public int compareTo(LoyaltyCard card) {
		return getLoyaltyCardId().compareTo(card.getLoyaltyCardId());
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof LoyaltyCard) {
			LoyaltyCard card = (LoyaltyCard) obj;
			return getBarCodeNumber().equals(card.getBarCodeNumber())
					&& getBarCodeType().equals(card.getBarCodeType())
					&& getLoyaltyCardId().equals(card.getLoyaltyCardId())
					&& getOperationCounter() == card.getOperationCounter()
					&& getBrand().equals(card.getBrand());
		} else {
			return false;
		}
	}
}
