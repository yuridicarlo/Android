package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DtoSynchPhoneBookRequest implements Serializable {

    protected List<String> msisdns;

    public DtoSynchPhoneBookRequest() {
    }

    public DtoSynchPhoneBookRequest(DtoSynchPhoneBookRequest another) {
        this.msisdns = another.getMsisdns();
    }

    public List<String> getMsisdns() {
        if (msisdns == null) {
            msisdns = new ArrayList<>();
        }
        return this.msisdns;
    }

    public void setMsisdns(List<String> msisdns) {
        this.msisdns = msisdns;
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof DtoSynchPhoneBookRequest) {
            return ((DtoSynchPhoneBookRequest) another).getMsisdns().equals(this.getMsisdns());
        } else {
            return super.equals(another);
        }
    }

}
