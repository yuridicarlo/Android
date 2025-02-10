package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;

public class DtoThreshold implements Serializable {

    protected String thresholdType;
    protected String thresholdValue;
    protected String thresholdMinValue;
    protected String thresholdMaxValue;

    /**
     * Gets the value of the thresholdType property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getThresholdType() {
        return thresholdType;
    }

    /**
     * Sets the value of the thresholdType property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setThresholdType(String value) {
        this.thresholdType = value;
    }

    /**
     * Gets the value of the thresholdValue property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getThresholdValue() {
        return thresholdValue;
    }

    /**
     * Sets the value of the thresholdValue property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setThresholdValue(String value) {
        this.thresholdValue = value;
    }

    /**
     * Gets the value of the thresholdMinValue property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getThresholdMinValue() {
        return thresholdMinValue;
    }

    /**
     * Sets the value of the thresholdMinValue property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setThresholdMinValue(String value) {
        this.thresholdMinValue = value;
    }

    /**
     * Gets the value of the thresholdMaxValue property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getThresholdMaxValue() {
        return thresholdMaxValue;
    }

    /**
     * Sets the value of the thresholdMaxValue property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setThresholdMaxValue(String value) {
        this.thresholdMaxValue = value;
    }

}
