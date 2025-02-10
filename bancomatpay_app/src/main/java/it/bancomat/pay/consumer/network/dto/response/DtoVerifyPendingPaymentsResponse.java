package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;
import java.util.List;

public class DtoVerifyPendingPaymentsResponse implements Serializable {

    protected String lastAttempts;
    protected String instrumentId;
    protected List<DtoPendingPayment> pendingPayments;

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

    public List<DtoPendingPayment> getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(List<DtoPendingPayment> pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

}
