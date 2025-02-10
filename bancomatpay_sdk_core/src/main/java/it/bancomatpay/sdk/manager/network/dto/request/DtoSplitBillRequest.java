package it.bancomatpay.sdk.manager.network.dto.request;

import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.Beneficiary;

public class DtoSplitBillRequest {

    private List<Beneficiary> beneficiaries;
    private String amount;
    private String causal;
    private String description;

    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
