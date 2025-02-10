package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoBrand;

public class DtoGetLoyaltyCardBrandsResponse implements Serializable {

	private List<DtoBrand> dtoBrands;

	public List<DtoBrand> getDtoBrands() {
		return dtoBrands;
	}

	public void setDtoBrands(List<DtoBrand> dtoBrands) {
		this.dtoBrands = dtoBrands;
	}

}
