package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TransactionData implements Serializable {

    public enum Status {

        //status P2P
        INV, //freccina inviato
        ANN_P2P, //icona assente
        PND, //clessidra
        RIC, //freccina ricevuto

        //stutus P2B
        PAG, //freccina inviato
        STR, //freccina ricevuto
        ANN_P2B, //icona assente
        ADD, //icona calendario
        ATM,
        ANN_ATM,
        POS,
        ANN_POS,
        CASHBACK

    }

    private String displayName;
    private String imageResource; //base64 in caso di insegna merchant o uri (interna) in caso di consumer
    protected String letter; //lettera da mostrare in caso di immagine assente
    protected String tag;
    private TransactionType transactionType;
    private String transactionId;
    protected String msisdn;
    protected BigDecimal amount;
    protected BigDecimal totalAmount;
    protected BigDecimal cashbackAmount;
    private Status transactionStatus;
    private Date requestDate;
    private Date paymentDate;
    protected String causal;
    protected String idSct;
    protected String iban;
    protected String fee;
    protected ItemInterface itemInterface;

    public ItemInterface getItemInterface() {
        return itemInterface;
    }

    public void setItemInterface(ItemInterface itemInterface) {
        this.itemInterface = itemInterface;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Status getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Status transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(BigDecimal cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }
}
