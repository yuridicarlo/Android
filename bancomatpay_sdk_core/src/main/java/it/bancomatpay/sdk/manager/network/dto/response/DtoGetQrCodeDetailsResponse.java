package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.network.dto.DtoPayment;
import it.bancomatpay.sdk.manager.network.dto.DtoShop;

public class DtoGetQrCodeDetailsResponse implements Serializable {

    protected DtoShop dtoShop;
    protected DtoPayment dtoPayment;

    public DtoShop getDtoShop() {
        return dtoShop;
    }

    public void setDtoShop(DtoShop dtoShop) {
        this.dtoShop = dtoShop;
    }

    public DtoPayment getDtoPayment() {
        return dtoPayment;
    }

    public void setDtoPayment(DtoPayment dtoPayment) {
        this.dtoPayment = dtoPayment;
    }

}
