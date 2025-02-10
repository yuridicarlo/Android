package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;

public class DtoGetMerchantDetailsRequest implements Serializable {

    protected String msisdn;
    protected String tag;
    protected String shopId;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

}
