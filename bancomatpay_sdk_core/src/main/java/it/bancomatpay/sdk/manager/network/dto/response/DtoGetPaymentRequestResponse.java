package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.manager.network.dto.DtoBankIdRequest;
import it.bancomatpay.sdk.manager.network.dto.DtoDenyPaymentReasons;
import it.bancomatpay.sdk.manager.network.dto.request.DtoPaymentRequest;

public class DtoGetPaymentRequestResponse implements Serializable {

    protected List<DtoPaymentRequest> paymentRequests;
    protected List<DtoBankIdRequest> bankIdRequests;
    protected DtoDenyPaymentReasons denyReason;
    protected List<DtoDirectDebitRequest> directDebitRequests;

    /**
     * Gets the value of the paymentRequests property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentRequests property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentRequests().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtoPaymentRequest }
     */
    public List<DtoPaymentRequest> getPaymentRequests() {
        if (paymentRequests == null) {
            paymentRequests = new ArrayList<>();
        }
        return this.paymentRequests;
    }

    public List<DtoDirectDebitRequest> getDirectDebitRequests() {
        if (directDebitRequests == null) {
            directDebitRequests = new ArrayList<>();
        }
        return this.directDebitRequests;
    }

    /**
     * Gets the value of the denyReason property.
     *
     * @return possible object is
     * {@link DtoDenyPaymentReasons }
     */
    public DtoDenyPaymentReasons getDenyReason() {
        return denyReason;
    }

    /**
     * Sets the value of the denyReason property.
     *
     * @param value allowed object is
     *              {@link DtoDenyPaymentReasons }
     */
    public void setDenyReason(DtoDenyPaymentReasons value) {
        this.denyReason = value;
    }

    public List<DtoBankIdRequest> getBankIdRequests() {
        return bankIdRequests;
    }

    public void setBankIdRequests(List<DtoBankIdRequest> bankIdRequests) {
        this.bankIdRequests = bankIdRequests;
    }

}
