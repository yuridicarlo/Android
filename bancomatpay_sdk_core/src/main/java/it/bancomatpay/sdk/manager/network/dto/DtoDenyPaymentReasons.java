package it.bancomatpay.sdk.manager.network.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DtoDenyPaymentReasons implements Serializable {

    protected List<String> reasonUUIDs;

    /**
     * Gets the value of the reasonUUIDs property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reasonUUIDs property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReasonUUIDs().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getReasonUUIDs() {
        if (reasonUUIDs == null) {
            reasonUUIDs = new ArrayList<String>();
        }
        return this.reasonUUIDs;
    }

}
