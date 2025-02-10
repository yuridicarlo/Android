package it.bancomatpay.sdk.manager.network.dto;

public class Beneficiary {

    private String msisdn;
    private boolean bpayEnabled;
    private String amount;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public boolean isBpayEnabled() {
        return bpayEnabled;
    }

    public void setBpayEnabled(boolean bpayEnabled) {
        this.bpayEnabled = bpayEnabled;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
