package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DtoPayment implements Serializable {

    protected String shopName;
    protected DtoAddress address;
    protected String msisdn;
    protected String insignia;
    protected String fee;
    protected String amount;
    protected String totalAmount;
    protected String paymentId;
    protected String tag;
    protected long shopId;
    protected BigInteger tillId;
    protected String causal;
    protected String category;
    protected String latitude;
    protected String longitude;
    protected String localCurrency;
    protected String localAmount;
    protected boolean qrCodeEmpsa;

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the value of the shopName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * Sets the value of the shopName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setShopName(String value) {
        this.shopName = value;
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
     * Gets the value of the fee property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public String getFee() {
        return fee;
    }

    /**
     * Sets the value of the fee property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setFee(String value) {
        this.fee = value;
    }

    /**
     * Gets the value of the amount property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the paymentId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the value of the paymentId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPaymentId(String value) {
        this.paymentId = value;
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
     * Gets the value of the tillId property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getTillId() {
        return tillId;
    }

    /**
     * Sets the value of the tillId property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setTillId(BigInteger value) {
        this.tillId = value;
    }

    /**
     * Gets the value of the causal property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCausal() {
        return causal;
    }

    /**
     * Sets the value of the causal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCausal(String value) {
        this.causal = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the value of the localCurrency property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLocalCurrency() {
        return localCurrency;
    }

    /**
     * Sets the value of the localCurrency property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLocalCurrency(String value) {
        this.localCurrency = value;
    }

    /**
     * Gets the value of the localAmount property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLocalAmount() {
        return localAmount;
    }

    /**
     * Sets the value of the localAmount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLocalAmount(String value) {
        this.localAmount = value;
    }

    /**
     * Gets the value of the qrCodeEmpsa property.
     *
     * @return possible object is
     * {@link boolean }
     */
    public boolean isQrCodeEmpsa() {
        return qrCodeEmpsa;
    }

    /**
     * Sets the value of the qrCodeEmpsa property.
     *
     * @param value allowed object is
     *              {@link boolean }
     */
    public void setQrCodeEmpsa(boolean value) {
        this.qrCodeEmpsa = value;
    }
}
