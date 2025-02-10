package it.bancomatpay.sdk.manager.db;

import android.provider.BaseColumns;

import java.io.Serializable;

public class LoyaltyCardFrequent {

	public static class Model implements Serializable {

		String loyaltyCardId;
		int operationCounter;

		public String getLoyaltyCardId() {
			return loyaltyCardId;
		}

		public void setLoyaltyCardId(String loyaltyCardId) {
			this.loyaltyCardId = loyaltyCardId;
		}

		public int getOperationCounter() {
			return operationCounter;
		}

		public void setOperationCounter(int operationCounter) {
			this.operationCounter = operationCounter;
		}

	}

	// To prevent someone from accidentally instantiating the contract class,
	// make the constructor private.
	private LoyaltyCardFrequent() {
	}

	/* Inner class that defines the table contents */
	static class EntryV1 implements BaseColumns {
		static final String TABLE_NAME = "loyaltyCardFrequent";
		static final String COLUMN_NAME_LOYALTY_CARD_ID = "loyaltyCardId";
		static final String COLUMN_NAME_OPERATION_COUNTER = "operationCounter";
	}

}
