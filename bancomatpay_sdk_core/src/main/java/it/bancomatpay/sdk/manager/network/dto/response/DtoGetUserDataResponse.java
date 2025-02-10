package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoCustomerJourneyConsents;
import it.bancomatpay.sdk.manager.network.dto.DtoInstrument;
import it.bancomatpay.sdk.manager.network.dto.DtoThreshold;
import it.bancomatpay.sdk.manager.network.dto.DtoTransaction;

public class DtoGetUserDataResponse implements Serializable {

    protected Float balance;
    protected List<DtoTransaction> transactions;
    protected boolean isDefaultReceiver;
    protected boolean isDefaultReceiverOtherBank;
    protected List<DtoThreshold> p2PThresholds;
    protected List<DtoThreshold> p2BThresholds;
    protected String iban;
    protected String name;
    protected String surname;
    protected String msisdn;
    protected Integer paymentRequestNumber;
    protected String cipheredIban;
    protected String banksConfigurationVersion;
    protected DtoCustomerJourneyConsents consents;

    protected boolean multiIban;
    protected List<DtoInstrument> instruments;

    /**
     * Gets the value of the balance property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Float getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBalance(Float value) {
        this.balance = value;
    }

    /**
     * Gets the value of the transactions property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactions property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactions().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoTransaction }
     *
     *
     */
    public List<DtoTransaction> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        return this.transactions;
    }

    /**
     * Gets the value of the p2PThresholds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the p2PThresholds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getP2PThresholds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoThreshold }
     *
     *
     */
    public List<DtoThreshold> getP2PThresholds() {
        if (p2PThresholds == null) {
            p2PThresholds = new ArrayList<DtoThreshold>();
        }
        return this.p2PThresholds;
    }

    /**
     * Gets the value of the p2BThresholds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the p2BThresholds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getP2BThresholds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoThreshold }
     *
     *
     */
    public List<DtoThreshold> getP2BThresholds() {
        if (p2BThresholds == null) {
            p2BThresholds = new ArrayList<DtoThreshold>();
        }
        return this.p2BThresholds;
    }

    /**
     * Gets the value of the iban property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets the value of the iban property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIban(String value) {
        this.iban = value;
    }

    /**
     * Gets the value of the isDefaultReceiver property.
     *
     */
    public boolean isIsDefaultReceiver() {
        return isDefaultReceiver;
    }

    /**
     * Sets the value of the isDefaultReceiver property.
     *
     */
    public void setIsDefaultReceiver(boolean value) {
        this.isDefaultReceiver = value;
    }

    public boolean isDefaultReceiverOtherBank() {
        return isDefaultReceiverOtherBank;
    }

    public void setDefaultReceiverOtherBank(boolean defaultReceiverOtherBank) {
        isDefaultReceiverOtherBank = defaultReceiverOtherBank;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the surname property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSurname(String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the msisdn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    public Integer getPaymentRequestNumber() {
        return paymentRequestNumber;
    }

    public void setPaymentRequestNumber(Integer paymentRequestNumber) {
        this.paymentRequestNumber = paymentRequestNumber;
    }

    public String getCipheredIban() {
        return cipheredIban;
    }

    public void setCipheredIban(String cipheredIban) {
        this.cipheredIban = cipheredIban;
    }

    public String getBanksConfigurationVersion() {
        return banksConfigurationVersion;
    }

    public void setBanksConfigurationVersion(String banksConfigurationVersion) {
        this.banksConfigurationVersion = banksConfigurationVersion;
    }

    public boolean isMultiIban() {
        return multiIban;
    }

    public void setMultiIban(boolean multiIban) {
        this.multiIban = multiIban;
    }

    public List<DtoInstrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<DtoInstrument> instruments) {
        this.instruments = instruments;
    }

    public DtoCustomerJourneyConsents getConsents() {
        return consents;
    }

    public void setConsents(DtoCustomerJourneyConsents consents) {
        this.consents = consents;
    }

}
