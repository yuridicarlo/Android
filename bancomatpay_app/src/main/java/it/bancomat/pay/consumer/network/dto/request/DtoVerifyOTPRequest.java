package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoVerifyOTPRequest implements Serializable {

    protected String otp;
    protected String token;

    /**
     * Gets the value of the otp property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOtp() {
        return otp;
    }

    /**
     * Sets the value of the otp property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOtp(String value) {
        this.otp = value;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
