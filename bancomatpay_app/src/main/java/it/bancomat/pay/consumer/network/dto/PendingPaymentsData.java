package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;
import java.util.ArrayList;


public class PendingPaymentsData implements Serializable {

    protected String lastAttempts;
    protected String instrumentId;
    protected ArrayList<PendingPayment> pendingPayments;

    public String getLastAttempts() {
        return lastAttempts;
    }

    public void setLastAttempts(String lastAttempts) {
        this.lastAttempts = lastAttempts;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public ArrayList<PendingPayment> getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(ArrayList<PendingPayment> pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

}
