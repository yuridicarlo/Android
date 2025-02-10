package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

import it.bancomat.pay.consumer.network.dto.response.DtoPendingPayment;

public class DtoConfirmPendingPaymentRequest implements Serializable {

    protected String msisdn;
    protected String name;
    protected String surname;
    protected String mail;
    protected String otp;
    protected String iban;

    protected DtoPendingPayment pendingPayment;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }


    public DtoPendingPayment getPendingPayment() {
        return pendingPayment;
    }

    public void setPendingPayment(DtoPendingPayment pendingPayment) {
        this.pendingPayment = pendingPayment;
    }

}
