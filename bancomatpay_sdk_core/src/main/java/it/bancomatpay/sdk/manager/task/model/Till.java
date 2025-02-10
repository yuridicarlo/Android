package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class Till implements Serializable {

	private boolean automatic;
	private String tillId;
	private String identifier;
	private String name;
	private boolean enabled;
	private boolean selected;

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
