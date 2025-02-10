package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SyncPhoneBookData implements Serializable {

	private ArrayList<ContactItem> contactItems;
	private boolean isContactsSynced;

	public ArrayList<ContactItem> getContactItems() {
		return contactItems;
	}

	public void setContactItems(ArrayList<ContactItem> contactItems) {
		this.contactItems = contactItems;
	}

	public boolean isContactsSynced() {
		return isContactsSynced;
	}

	public void setContactsSynced(boolean contactsSynced) {
		isContactsSynced = contactsSynced;
	}

}
