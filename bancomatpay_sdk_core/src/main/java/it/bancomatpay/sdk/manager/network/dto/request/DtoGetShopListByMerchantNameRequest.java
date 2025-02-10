package it.bancomatpay.sdk.manager.network.dto.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.task.model.ShopType;

public class DtoGetShopListByMerchantNameRequest implements Serializable {

    protected String merchantName;
    protected double latitude;
    protected double longitude;
    @SerializedName("etichetta")
    private String shopName;
    @SerializedName("tipoNegozio")
    private ShopType shopType;
    @SerializedName("mcc")
    private String category;
    @SerializedName("numeroMerchantResponse")
    private int numberOfMerchantsToShow;
    @SerializedName("prossimaPaginaDaMostrare")
    private int nextPageToShow;


    /**
     * Gets the value of the merchantName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Sets the value of the merchantName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMerchantName(String value) {
        this.merchantName = value;
    }

    /**
     * Gets the value of the latitude property.
     *
     * @return possible object is
     * {@link String }
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLatitude(double value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     *
     * @return possible object is
     * {@link String }
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     *
     * @param value allowed object is
     *              {@link String }
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumberOfMerchantsToShow() {
        return numberOfMerchantsToShow;
    }

    public void setNumberOfMerchantsToShow(int numberOfMerchantsToShow) {
        this.numberOfMerchantsToShow = numberOfMerchantsToShow;
    }

    public int getNextPageToShow() {
        return nextPageToShow;
    }

    public void setNextPageToShow(int nextPageToShow) {
        this.nextPageToShow = nextPageToShow;
    }
}
