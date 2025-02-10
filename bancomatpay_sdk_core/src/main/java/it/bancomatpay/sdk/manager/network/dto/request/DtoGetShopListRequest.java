package it.bancomatpay.sdk.manager.network.dto.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.task.model.ShopType;

public class DtoGetShopListRequest implements Serializable {

    protected Double latitude;
    protected Double longitude;
    @SerializedName("etichetta")
    private String shopName;

    @SerializedName("tipoNegozio")
    private ShopType shopType;

    @SerializedName("mcc")
    private String categoryUuid;

    @SerializedName("numeroMerchantResponse")
    private Integer pageSize;
    @SerializedName("prossimaPaginaDaMostrare")
    private Integer nextPageToShow = 0;

    /**
     * Gets the value of the latitude property.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     */
    public void setLatitude(double value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     */
    public void setLongitude(double value) {
        this.longitude = value;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setNextPageToShow(Integer nextPageToShow) {
        this.nextPageToShow = nextPageToShow;
    }

    public int getNextPageToShow() {
        return nextPageToShow;
    }

    public void setNextPageToShow(int nextPageToShow) {
        this.nextPageToShow = nextPageToShow;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }
}
