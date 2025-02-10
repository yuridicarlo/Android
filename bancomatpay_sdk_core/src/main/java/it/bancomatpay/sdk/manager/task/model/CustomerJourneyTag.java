package it.bancomatpay.sdk.manager.task.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CustomerJourneyTag implements Serializable {

	private String rowId;
	private String tagTimestamp;
	private String tagExecutionId;
	private String tagKey;
	private String tagJsonData;
	private String cuid;

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

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

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	@NonNull
	@Override
	public String toString() {
		return this.tagKey + " - " + this.getCuid() + " - " + this.tagJsonData;
	}

}
