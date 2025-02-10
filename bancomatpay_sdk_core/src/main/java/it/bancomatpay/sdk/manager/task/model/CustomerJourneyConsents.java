package it.bancomatpay.sdk.manager.task.model;

public class CustomerJourneyConsents {

	private boolean profiling;
	private boolean marketing;
	private boolean dataToThirdParties;

	public boolean isProfiling() {
		return profiling;
	}

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}

	public boolean isMarketing() {
		return marketing;
	}

	public void setMarketing(boolean marketing) {
		this.marketing = marketing;
	}

	public boolean isDataToThirdParties() {
		return dataToThirdParties;
	}

	public void setDataToThirdParties(boolean dataToThirdParties) {
		this.dataToThirdParties = dataToThirdParties;
	}

}
