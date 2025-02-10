package it.bancomat.pay.consumer.network.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoBankData;

public class DtoBankListResponse implements Serializable {

    protected List<DtoBankData> dtoBankDatas;

    /**
     * Gets the value of the dtoBankDatas property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dtoBankDatas property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDtoBankDatas().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoBankData }
     */
    public List<DtoBankData> getDtoBankDatas() {
        if (dtoBankDatas == null) {
            dtoBankDatas = new ArrayList<DtoBankData>();
        }
        return this.dtoBankDatas;
    }

}
