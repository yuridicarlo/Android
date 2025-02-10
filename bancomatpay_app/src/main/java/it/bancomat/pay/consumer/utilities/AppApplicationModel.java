package it.bancomat.pay.consumer.utilities;

import it.bancomat.pay.consumer.storage.AppUserDbHelper;

public class AppApplicationModel {

	private static AppApplicationModel instance;

	private int customerJourneySavedTagRowCount;

	private AppApplicationModel() {
	}

	public void resetApplicationModel() {
		customerJourneySavedTagRowCount = 0;
	}

	public static AppApplicationModel getInstance() {
		if (instance == null) {
			instance = new AppApplicationModel();
		}
		return instance;
	}

	public void updateCustomerJourneyRowCount() {
		setCustomerJourneySavedTagRowCount(AppUserDbHelper.getInstance().getCustomerJourneyTagsList().size());
	}

	public int getCustomerJourneySavedTagRowCount() {
		return customerJourneySavedTagRowCount;
	}

	public void setCustomerJourneySavedTagRowCount(int customerJourneySavedTagRowCount) {
		this.customerJourneySavedTagRowCount = customerJourneySavedTagRowCount;
	}

	public void incrementCustomerJourneyRowCount() {
		this.customerJourneySavedTagRowCount++;
	}

	public void decrementCustomerJourneyRowCount() {
		if (this.customerJourneySavedTagRowCount > 0) {
			this.customerJourneySavedTagRowCount--;
		}
	}

}
