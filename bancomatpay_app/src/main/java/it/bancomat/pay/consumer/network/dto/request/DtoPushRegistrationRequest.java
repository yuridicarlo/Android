
package it.bancomat.pay.consumer.network.dto.request;

import java.io.Serializable;

public class DtoPushRegistrationRequest implements Serializable {

    protected String token;
    protected String pushEnvironment;

    public String getPushEnvironment() {
        return pushEnvironment;
    }

    public void setPushEnvironment(String pushEnvironment) {
        this.pushEnvironment = pushEnvironment;
    }

    /**
     * Gets the value of the token property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }

}
