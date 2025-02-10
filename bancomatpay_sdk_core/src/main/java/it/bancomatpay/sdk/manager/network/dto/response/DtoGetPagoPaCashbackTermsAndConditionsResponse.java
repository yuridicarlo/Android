package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

public class DtoGetPagoPaCashbackTermsAndConditionsResponse implements Serializable {

    private String termsAndConditions;

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
