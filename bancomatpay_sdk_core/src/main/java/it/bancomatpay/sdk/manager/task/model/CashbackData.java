package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.Date;

public class CashbackData implements Serializable {

    private String cashback;
    private String transactionNumber;
    private String minTransactionNumber;
    private String maxTransactionNumber;
    private String ranking;
    private String participantsNumber;
    private Date periodStartDate;
    private Date periodEndDate;

    public String getCashback() {
        return cashback;
    }

    public void setCashback(String cashback) {
        this.cashback = cashback;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getMinTransactionNumber() {
        return minTransactionNumber;
    }

    public void setMinTransactionNumber(String minTransactionNumber) {
        this.minTransactionNumber = minTransactionNumber;
    }

    public String getMaxTransactionNumber() {
        return maxTransactionNumber;
    }

    public void setMaxTransactionNumber(String maxTransactionNumber) {
        this.maxTransactionNumber = maxTransactionNumber;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getParticipantsNumber() {
        return participantsNumber;
    }

    public void setParticipantsNumber(String participantsNumber) {
        this.participantsNumber = participantsNumber;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }
}
