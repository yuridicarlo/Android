package it.bancomatpay.sdk.manager.network.dto;

import androidx.annotation.NonNull;

public enum EDenyReason {

    THERE_WASNT_ME("3869648945"),
    MUST_DO_CHANGES("5279798294"),
    ENCOUNTERED_PROBLEMS("6165590606"),
    CHANGED_IDEA("4559126255");

    private String reasonUUID;

    EDenyReason(String reasonUUID) {
        this.reasonUUID = reasonUUID;
    }

    @NonNull
    @Override
    public String toString() {
        return this.reasonUUID;
    }

}
