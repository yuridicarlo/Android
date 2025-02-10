package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoDenyDirectDebitRequest implements Serializable {
    protected String requestId;
    protected String tag;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
