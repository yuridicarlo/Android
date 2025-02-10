package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DtoSynchPhoneBookResponse implements Serializable {

    protected List<String> consumerMsisdns;
    protected List<String> consumerMsisdnPRs;

    public List<String> getConsumerMsisdns() {
        if (consumerMsisdns == null) {
            consumerMsisdns = new ArrayList<>();
        }
        return this.consumerMsisdns;
    }

    public List<String> getConsumerMsisdnPRs() {
        if (consumerMsisdnPRs == null) {
            consumerMsisdnPRs = new ArrayList<>();
        }
        return this.consumerMsisdnPRs;
    }

}
