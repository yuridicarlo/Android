package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class UserData implements Serializable {

    private BigDecimal balance;
    protected String iban;
    private boolean isDefaultReceiver;
    private boolean isDefaultReceiverOtherBank;
    protected String name;
    private String surname;
    protected String msisdn;
    private Thresholds p2PThresholds;
    private Thresholds p2BThresholds;
    private BigInteger paymentRequestNumber;
    private CustomerJourneyConsents customerJourneyConsents;

    private boolean multiIban;
    private List<InstrumentData> instruments;

    public Thresholds getP2PThresholds() {
        return p2PThresholds;
    }

    public void setP2PThresholds(Thresholds p2PThresholds) {
        this.p2PThresholds = p2PThresholds;
    }

    public Thresholds getP2BThresholds() {
        return p2BThresholds;
    }

    public void setP2BThresholds(Thresholds p2BThresholds) {
        this.p2BThresholds = p2BThresholds;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public boolean isDefaultReceiver() {
        return isDefaultReceiver;
    }

    public void setDefaultReceiver(boolean defaultReceiver) {
        isDefaultReceiver = defaultReceiver;
    }

    public boolean isDefaultReceiverOtherBank() {
        return isDefaultReceiverOtherBank;
    }

    public void setDefaultReceiverOtherBank(boolean defaultReceiverOtherBank) {
        isDefaultReceiverOtherBank = defaultReceiverOtherBank;
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

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public BigInteger getPaymentRequestNumber() {
        return paymentRequestNumber;
    }

    public void setPaymentRequestNumber(BigInteger paymentRequestNumber) {
        this.paymentRequestNumber = paymentRequestNumber;
    }

    public boolean isMultiIban() {
        return multiIban;
    }

    public void setMultiIban(boolean multiIban) {
        this.multiIban = multiIban;
    }

    public List<InstrumentData> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<InstrumentData> instruments) {
        this.instruments = instruments;
    }

    public CustomerJourneyConsents getCustomerJourneyConsents() {
        return customerJourneyConsents;
    }

    public void setCustomerJourneyConsents(CustomerJourneyConsents customerJourneyConsents) {
        this.customerJourneyConsents = customerJourneyConsents;
    }

}
