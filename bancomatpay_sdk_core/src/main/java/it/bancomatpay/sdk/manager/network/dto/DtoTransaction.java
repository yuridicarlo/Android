package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoTransaction implements Serializable {

    protected String transactionId;
    protected String displayName;
    protected String msisdn;
    protected String amount;
    protected String totalAmount;
    protected String cashbackAmount;
    protected String tag;

    protected TransactionStatusEnum transactionStatus;
    protected String transactionType;
    protected String requestDate;
    protected String paymentDate;
    protected String causal;
    protected String idSct;
    protected String fee;

    protected DtoShop dtoShop;

    protected String iban;

    public DtoShop getDtoShop() {
        return dtoShop;
    }

    public void setDtoShop(DtoShop dtoShop) {
        this.dtoShop = dtoShop;
    }

    /**
     * Gets the value of the transactionId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the displayName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the msisdn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the amount property.
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Gets the value of the transactionStatus property.
     *
     * @return possible object is
     * {@link TransactionStatusEnum }
     */
    public TransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * Sets the value of the transactionStatus property.
     *
     * @param value allowed object is
     *              {@link TransactionStatusEnum }
     */
    public void setTransactionStatus(TransactionStatusEnum value) {
        this.transactionStatus = value;
    }

    /**
     * Gets the value of the transactionType property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTransactionType(String value) {
        this.transactionType = value;
    }

    /**
     * Gets the value of the requestDate property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the value of the requestDate property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRequestDate(String value) {
        this.requestDate = value;
    }

    /**
     * Gets the value of the paymentDate property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPaymentDate(String value) {
        this.paymentDate = value;
    }

    /**
     * Gets the value of the causal property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCausal() {
        return causal;
    }

    /**
     * Sets the value of the causal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCausal(String value) {
        this.causal = value;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getIdSct() {
        return idSct;
    }

    public void setIdSct(String idSct) {
        this.idSct = idSct;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(String cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }
}
