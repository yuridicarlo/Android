package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoTill implements Serializable {

	protected boolean automatic;
	protected String tillId;
	protected String alternativeTillId;
	protected String identifier;
	protected boolean enabled;

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

	public String getAlternativeTillId() {
		return alternativeTillId;
	}

	public void setAlternativeTillId(String alternativeTillId) {
		this.alternativeTillId = alternativeTillId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
