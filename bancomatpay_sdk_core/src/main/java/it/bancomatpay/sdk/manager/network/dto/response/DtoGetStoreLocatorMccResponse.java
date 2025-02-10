package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoShopCategory;

public class DtoGetStoreLocatorMccResponse implements Serializable {

    private List<DtoShopCategory> categories;

    public List<DtoShopCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<DtoShopCategory> categories) {
        this.categories = categories;
    }
}
