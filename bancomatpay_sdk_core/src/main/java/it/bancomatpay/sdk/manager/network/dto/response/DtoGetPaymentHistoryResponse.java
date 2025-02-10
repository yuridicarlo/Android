package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoTransaction;

public class DtoGetPaymentHistoryResponse implements Serializable {

    protected List<DtoTransaction> transactions;

    /**
     * Gets the value of the transactions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoTransaction }
     * 
     * 
     */
    public List<DtoTransaction> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<DtoTransaction>();
        }
        return this.transactions;
    }

}
