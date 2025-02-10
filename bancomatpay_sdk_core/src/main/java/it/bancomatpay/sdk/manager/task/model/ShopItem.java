package it.bancomatpay.sdk.manager.task.model;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ShopItem implements Serializable, ItemInterface {

    public enum EMerchantType {
        STANDARD, PREAUTHORIZED
    }

    protected long shopId;
    protected Address address;
    protected double latitude;
    protected double longitude;
    protected BigDecimal distance;
    protected String msisdn;
    protected String mail;
    protected String insignia;
    protected String tag;
    protected String name;
    protected String mccImageName;
    protected String holderName;
    protected EMerchantType merchantType;
    protected String paymentCategory;
    protected boolean tillManagement;
    protected List<Till> tillList;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long value) {
        this.shopId = value;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address value) {
        this.address = value;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double value) {
        this.latitude = value;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double value) {
        this.longitude = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String value) {
        this.mail = value;
    }

    public String getInsignia() {
        return insignia;
    }

    public void setInsignia(String value) {
        this.insignia = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String value) {
        this.tag = value;
    }

    public void setName(String value) {
        this.name = value;
    }
    public void setMccImageName(String value) {
        this.mccImageName = value;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String value) {
        this.holderName = value;
    }

    public EMerchantType getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(EMerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public boolean isTillManagement() {
        return tillManagement;
    }

    public void setTillManagement(boolean tillManagement) {
        this.tillManagement = tillManagement;
    }

    public List<Till> getTillList() {
        return tillList;
    }

    public void setTillList(List<Till> tillList) {
        this.tillList = tillList;
    }

    @Override
    public String getLetter() {
        return "";
    }

    @Override
    public Type getType() {
        return Type.MERCHANT;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getDescription() {
        if (address != null && checkAddress(address.getStreet(), address.getCity())) {
            return address.getStreet() + ", " + address.getCity();
        } else {
            return "";
        }
    }

    @Override
    public String getImage() {
        return insignia;
    }

    @Override
    public String getPhoneNumber() {
        return msisdn;
    }

    @Override
    public long getId() {
        return shopId;
    }

    public String getName() {
        return name;
    }

    public String getMccImageName() {
        return mccImageName;
    }

    public String getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(String paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    @Override
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private boolean checkAddress(String address, String city) {

        boolean isValid = false;

        if (!TextUtils.isEmpty(address) && !address.equalsIgnoreCase("null")
                && !TextUtils.isEmpty(city) && !city.equalsIgnoreCase("null")) {
            isValid = true;
        }

        return isValid;
    }

}
