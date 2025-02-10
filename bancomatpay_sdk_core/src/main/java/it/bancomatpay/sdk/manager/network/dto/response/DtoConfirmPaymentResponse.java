package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;

public class DtoConfirmPaymentResponse implements Serializable {

    protected PaymentStateType paymentState;
    protected String lastAttempts;
    protected String pollingMaxTimeSeconds;
    protected String pollingRangeTimeSeconds;

    /**
     * Gets the value of the paymentState property.
     *
     * @return possible object is
     * {@link PaymentStateType }
     */
    public PaymentStateType getPaymentState() {
        return paymentState;
    }

    /**
     * Sets the value of the paymentState property.
     *
     * @param value allowed object is
     *              {@link PaymentStateType }
     */
    public void setPaymentState(PaymentStateType value) {
        this.paymentState = value;
    }

    /**
     * Gets the value of the lastAttempts property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLastAttempts() {
        return lastAttempts;
    }

    /**
     * Sets the value of the lastAttempts property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLastAttempts(String value) {
        this.lastAttempts = value;
    }

    /**
     * Gets the value of the pollingMaxTimeSeconds property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPollingMaxTimeSeconds() {
        return pollingMaxTimeSeconds;
    }

    /**
     * Sets the value of the pollingMaxTimeSeconds property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPollingMaxTimeSeconds(String value) {
        this.pollingMaxTimeSeconds = value;
    }

    /**
     * Gets the value of the pollingRangeTimeSeconds property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPollingRangeTimeSeconds() {
        return pollingRangeTimeSeconds;
    }

    /**
     * Sets the value of the pollingRangeTimeSeconds property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPollingRangeTimeSeconds(String value) {
        this.pollingRangeTimeSeconds = value;
    }

}
