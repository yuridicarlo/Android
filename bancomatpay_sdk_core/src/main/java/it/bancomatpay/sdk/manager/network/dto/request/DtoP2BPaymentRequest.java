
package it.bancomatpay.sdk.manager.network.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import it.bancomatpay.sdk.manager.network.dto.DtoShop;


public class DtoP2BPaymentRequest implements Serializable {

	protected String amount;
	protected String expirationDate;
	protected String fee;
	protected DtoShop dtoShop;
	protected BigInteger tillId;
	protected String paymentRequestId;
	protected String causal;
	protected String type;

	/**
	 * Gets the value of the amount property.
	 *
	 * @return possible object is
	 * {@link BigDecimal }
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * Sets the value of the amount property.
	 *
	 * @param value allowed object is
	 *              {@link BigDecimal }
	 */
	public void setAmount(String value) {
		this.amount = value;
	}

	/**
	 * Gets the value of the expirationDate property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * Sets the value of the expirationDate property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setExpirationDate(String value) {
		this.expirationDate = value;
	}

	/**
	 * Gets the value of the shopId property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public DtoShop getDtoShop() {
		return dtoShop;
	}

	/**
	 * Sets the value of the shopId property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setDtoShop(DtoShop value) {
		this.dtoShop = value;
	}

	/**
	 * Gets the value of the paymentRequestId property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getPaymentRequestId() {
		return paymentRequestId;
	}

	/**
	 * Sets the value of the paymentRequestId property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPaymentRequestId(String value) {
		this.paymentRequestId = value;
	}

	/**
	 * Gets the value of the causal property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getCausal() {
		return causal;
	}

	/**
	 * Sets the value of the causal property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setCausal(String value) {
		this.causal = value;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public BigInteger getTillId() {
		return tillId;
	}

	public void setTillId(BigInteger tillId) {
		this.tillId = tillId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
