package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoAddress implements Serializable {

    protected String street;
    protected String city;
    protected String postalCode;
    protected String province;

    /**
     * Gets the value of the street property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the city property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the postalCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the province property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the value of the province property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProvince(String value) {
        this.province = value;
    }

}
