package it.bancomatpay.sdk.manager.network.dto.request;

public class DtoDenyBankIdRequestRequest {

    protected String requestId;
    protected boolean blockMerchant;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isBlockMerchant() {
        return blockMerchant;
    }

    public void setBlockMerchant(boolean blockMerchant) {
        this.blockMerchant = blockMerchant;
    }

}
