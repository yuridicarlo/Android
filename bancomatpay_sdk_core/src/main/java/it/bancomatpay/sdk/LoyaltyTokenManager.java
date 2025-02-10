package it.bancomatpay.sdk;

import android.text.TextUtils;

public class LoyaltyTokenManager {

	private static LoyaltyTokenManager instance;

	private String mLoyaltyToken;

	public static LoyaltyTokenManager getInstance() {
		if (instance == null) {
			instance = new LoyaltyTokenManager();
		}
		return instance;
	}

	public String getLoyaltyToken() {
		return mLoyaltyToken;
	}

	public void setLoyaltyToken(String sessionToken) {
		if (!TextUtils.isEmpty(sessionToken)) {
			this.mLoyaltyToken = sessionToken;
		}
	}

}
