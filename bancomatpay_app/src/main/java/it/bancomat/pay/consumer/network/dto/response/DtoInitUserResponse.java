package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;

public class DtoInitUserResponse implements Serializable {


    protected String userAccountId;

    /**
     * Gets the value of the userAccountId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUserAccountId() {
        return userAccountId;
    }

    /**
     * Sets the value of the userAccountId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUserAccountId(String value) {
        this.userAccountId = value;
    }

}
