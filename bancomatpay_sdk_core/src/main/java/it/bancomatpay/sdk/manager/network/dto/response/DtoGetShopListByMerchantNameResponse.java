package it.bancomatpay.sdk.manager.network.dto.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoShop;

public class DtoGetShopListByMerchantNameResponse implements Serializable {

    protected List<DtoShop> shops;
    @SerializedName("listaMerchantCompleta")
    private boolean isListComplete;
    @SerializedName("prossimaPaginaDaMostrare")
    private int nextPageToShow;

    public List<DtoShop> getShops() {
        if (shops == null) {
            shops = new ArrayList<>();
        }
        return this.shops;
    }

    public void setShops(List<DtoShop> shops) {
        this.shops = shops;
    }

    public boolean isListComplete() {
        return isListComplete;
    }

    public void setListComplete(boolean listComplete) {
        isListComplete = listComplete;
    }

    public int getNextPageToShow() {
        return nextPageToShow;
    }

    public void setNextPageToShow(int nextPageToShow) {
        this.nextPageToShow = nextPageToShow;
    }
}
