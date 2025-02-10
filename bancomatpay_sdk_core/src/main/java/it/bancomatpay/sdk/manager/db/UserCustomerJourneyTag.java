package it.bancomatpay.sdk.manager.db;

import android.provider.BaseColumns;

import java.io.Serializable;

public class UserCustomerJourneyTag {

	public static class Model implements Serializable {

		String tagTimestamp;
		String tagExecutionId;
		String tagKey;
		String tagJsonData;
		String tagCuidAdvertising;

		public String getTagTimestamp() {
			return tagTimestamp;
		}

		public void setTagTimestamp(String tagTimestamp) {
			this.tagTimestamp = tagTimestamp;
		}

		public String getTagExecutionId() {
			return tagExecutionId;
		}

		public void setTagExecutionId(String tagExecutionId) {
			this.tagExecutionId = tagExecutionId;
		}

		public String getTagKey() {
			return tagKey;
		}

		public void setTagKey(String tagKey) {
			this.tagKey = tagKey;
		}

		public String getTagJsonData() {
			return tagJsonData;
		}

		public void setTagJsonData(String tagJsonData) {
			this.tagJsonData = tagJsonData;
		}
		
		public String getTagCuidAdvertising() {
			return tagCuidAdvertising;
		}

		public void setTagCuidAdvertising(String tagCuidAdvertising) {
			this.tagCuidAdvertising = tagCuidAdvertising;
		}

	}

	// To prevent someone from accidentally instantiating the contract class,
	// make the constructor private.
	private UserCustomerJourneyTag() {
	}

	/* Inner class that defines the table contents */
	public static class  EntryV1 implements BaseColumns {
		public static final String TABLE_NAME = "customerJourneyTags";
		public static final String COLUMN_NAME_TAG_TIMESTAMP = "tagTimestamp";
		public static final String COLUMN_NAME_TAG_EXECUTION_ID = "tagExecutionId";
		public static final String COLUMN_NAME_TAG_KEY = "tagKey";
		public static final String COLUMN_NAME_TAG_JSON_DATA = "tagJsonData";
		public static final String COLUMN_NAME_TAG_CUID_ADVERTISING = "tagCuidAdvertising";
	}

}
