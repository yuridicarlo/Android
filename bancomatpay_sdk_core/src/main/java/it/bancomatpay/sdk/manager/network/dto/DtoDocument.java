package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoDocument implements Serializable {

    public enum DocumentTypeEnum {
		PAPER_IDENTITY_CARD,
		ELECTRONIC_IDENTITY_CARD,
		HEALTH_INSURANCE_CARD,
		DRIVING_LICENSE,
		PASSPORT,
		OTHER
	}

	private String documentUuid;
	private String documentType;
	private String documentNumber;
	private String surname;
	private String name;
	private String fiscalCode;
	private String issuingInstitution;
	private String issuingDate;
	private String expirationDate;
	private String note;
	private String documentName;

	public String getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(String documentUuid) {
		this.documentUuid = documentUuid;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(String fiscalCode) {
		this.fiscalCode = fiscalCode;
	}

	public String getIssuingInstitution() {
		return issuingInstitution;
	}

	public void setIssuingInstitution(String issuingInstitution) {
		this.issuingInstitution = issuingInstitution;
	}

	public String getIssuingDate() {
		return issuingDate;
	}

	public void setIssuingDate(String issuingDate) {
		this.issuingDate = issuingDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

}
