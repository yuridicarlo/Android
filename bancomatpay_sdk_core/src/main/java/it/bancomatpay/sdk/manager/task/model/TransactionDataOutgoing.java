package it.bancomatpay.sdk.manager.task.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TransactionDataOutgoing implements Serializable {

    public enum Status{
        SENT,
        //ACCEPTED,
        EXPIRED,
        FAILED,
        UNKNOWN
    }

    protected String displayName;
    protected String imageResource; //base64 in caso di insegna merchant o uri (interna) in caso di consumer
    protected String letter; //lettera da mostrare in caso di immagine assente
    protected TransactionType transactionType;
    protected String transactionId;
    protected String msisdn;
    protected BigDecimal amount;
    protected TransactionDataOutgoing.Status transactionStatus;
    protected Date requestDate;
    protected Date paymentDate;
    protected String causal;
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

    public String getDisplayName() {
        if(!TextUtils.isEmpty(itemInterface.getTitle())) {
            return itemInterface.getTitle();
        } else {
            return displayName;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TransactionType getTransactionType() {
        return TransactionType.P2P;
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

    public TransactionDataOutgoing.Status getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionDataOutgoing.Status transactionStatus) {
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

}
