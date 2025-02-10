package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.Date;

public class DtoGetCashbackDataResponse implements Serializable {

    private String cashback;
    private String transactionsNumber;
    private String minTransactionsNumber;
    private String maxTransactionsNumber;
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

    public String getTransactionsNumber() {
        return transactionsNumber;
    }

    public void setTransactionsNumber(String transactionsNumber) {
        this.transactionsNumber = transactionsNumber;
    }

    public String getMinTransactionsNumber() {
        return minTransactionsNumber;
    }

    public void setMinTransactionsNumber(String minTransactionsNumber) {
        this.minTransactionsNumber = minTransactionsNumber;
    }

    public String getMaxTransactionsNumber() {
        return maxTransactionsNumber;
    }

    public void setMaxTransactionsNumber(String maxTransactionsNumber) {
        this.maxTransactionsNumber = maxTransactionsNumber;
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
