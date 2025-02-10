package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DtoShop implements Serializable {

    protected long shopId;
    protected DtoAddress address;
    protected double latitude;
    protected double longitude;
    protected String distance;
    protected String msisdn;
    protected String mail;
    protected String insignia;
    protected String tag;
    protected String merchantName;
    protected String holderName;
    protected String mccImageName;
    protected String merchantType;
    protected boolean tillManagement;
    protected List<DtoTill> tills;

    /**
     * Gets the value of the shopId property.
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * Sets the value of the shopId property.
     */
    public void setShopId(long value) {
        this.shopId = value;
    }

    /**
     * Gets the value of the address property.
     *
     * @return possible object is
     * {@link DtoAddress }
     */
    public DtoAddress getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value allowed object is
     *              {@link DtoAddress }
     */
    public void setAddress(DtoAddress value) {
        this.address = value;
    }

    /**
     * Gets the value of the latitude property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setLatitude(double value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setLongitude(double value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the distance property.
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Sets the value of the distance property.
     */
    public void setDistance(String value) {
        this.distance = value;
    }

    /**
     * Gets the value of the msisdn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the mail property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets the value of the mail property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMail(String value) {
        this.mail = value;
    }

    /**
     * Gets the value of the insignia property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsignia() {
        return insignia;
    }

    /**
     * Sets the value of the insignia property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsignia(String value) {
        this.insignia = value;
    }

    /**
     * Gets the value of the tag property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the value of the tag property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTag(String value) {
        this.tag = value;
    }

    /**
     * Gets the value of the causal property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Sets the value of the causal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMerchantName(String value) {
        this.merchantName = value;
    }

    /**
     * Gets the value of the holderName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getHolderName() {
        return holderName;
    }

    /**
     * Sets the value of the holderName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setHolderName(String value) {
        this.holderName = value;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

    public boolean isTillManagement() {
        return tillManagement;
    }

    public void setTillManagement(boolean tillManagement) {
        this.tillManagement = tillManagement;
    }

    public List<DtoTill> getTills() {
        return tills;
    }

    public void setTills(List<DtoTill> tills) {
        this.tills = tills;
    }

    public String getMccImageName() {
        return mccImageName;
    }

    public void setMccImageName(String mccImageName) {
        this.mccImageName = mccImageName;
    }
}
