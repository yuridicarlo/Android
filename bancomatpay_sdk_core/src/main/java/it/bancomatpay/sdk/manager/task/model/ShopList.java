package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.List;

public class ShopList implements Serializable {
    private List<ShopItem> shops;
    private boolean isListComplete;
    private int nextPageToShow;

    public List<ShopItem> getShops() {
        return shops;
    }

    public void setShops(List<ShopItem> shops) {
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
