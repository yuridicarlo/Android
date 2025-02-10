package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.util.List;

public class DtoSendPaymentRequestRequest implements Serializable {

    protected List<String> msisdnBeneficiaries;
    protected String amount;
    protected String causal;

    public List<String> getMsisdnBeneficiaries() {
        return msisdnBeneficiaries;
    }

    public void setMsisdnBeneficiaries(List<String> msisdnBeneficiaries) {
        this.msisdnBeneficiaries = msisdnBeneficiaries;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

}
