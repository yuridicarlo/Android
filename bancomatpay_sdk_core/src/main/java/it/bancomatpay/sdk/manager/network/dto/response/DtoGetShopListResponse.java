package it.bancomatpay.sdk.manager.network.dto.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoShop;

public class DtoGetShopListResponse implements Serializable {

    @SerializedName("shops")
    protected List<DtoShop> shops;
    @SerializedName("listaMerchantCompleta")
    private boolean isListComplete;
    @SerializedName("prossimaPaginaDaMostrare")
    private int nextPageToShow;

    /**
     * Gets the value of the shops property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shops property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShops().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoShop }
     */
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
