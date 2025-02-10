package it.bancomatpay.sdk.manager.task.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class SplitBeneficiary{
    public enum Status {
        NO_BPAY,
        SENT,
        ACCEPTED,
        FAILED,
        EXPIRED
    }

    private ContactItem beneficiary;
    private String amount;
    private Status splitBillState;

    public ContactItem getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(ContactItem beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Status getSplitBillState() {
        return splitBillState;
    }

    public void setSplitBillState(Status splitBillState) {
        this.splitBillState = splitBillState;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SplitBeneficiary) || ((SplitBeneficiary) obj).getBeneficiary() == null)
            return false;

        return Objects.equals(beneficiary.getPhoneNumber(), ((SplitBeneficiary) obj).getBeneficiary().getPhoneNumber());
    }
}
