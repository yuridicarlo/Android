package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoPassport implements Serializable {

	private String documentNumber;
	private String surname;
	private String name;
	private String gender;
	private String birthDate;
	private String nationality;
	private String issuingInstitution;
	private String issuingDate;
	private String expirationDate;
	private String frontImage;
	private String note;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
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

	public String getFrontImage() {
		return frontImage;
	}

	public void setFrontImage(String frontImage) {
		this.frontImage = frontImage;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
