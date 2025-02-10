package it.bancomatpay.sdk.manager.network.dto.request;

public class DtoConfirmDirectDebitRequestRequest {

    protected String requestId;
    protected String tag;
    protected String authorizationToken;

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

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

}
