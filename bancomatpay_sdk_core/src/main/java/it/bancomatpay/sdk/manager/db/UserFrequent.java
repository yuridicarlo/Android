package it.bancomatpay.sdk.manager.db;

import android.provider.BaseColumns;

import java.io.Serializable;

public class UserFrequent {

	public static class Model implements Serializable {

		int type;  //0 consumer 1 merchant 3 consumer-paymentRequestEnabled
		String userFrequentId;
		String jsonObject;
		int operationCounter;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getJsonObject() {
			return jsonObject;
		}

		public void setJsonObject(String jsonObject) {
			this.jsonObject = jsonObject;
		}

		public String getUserFrequentId() {
			return userFrequentId;
		}

		public void setUserFrequentId(String userFrequentId) {
			this.userFrequentId = userFrequentId;
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
	private UserFrequent() {
	}

	/* Inner class that defines the table contents */
	static class EntryV1 implements BaseColumns {
		static final String TABLE_NAME = "recent";
		static final String COLUMN_NAME_TYPE = "type";
		static final String COLUMN_NAME_USER_FREQUENT_ID = "userFrequentId";
		static final String COLUMN_NAME_JSON_OBJECT = "jsonObject";
		static final String COLUMN_NAME_OPERATION_COUNTER = "operationCounter";
	}

}
