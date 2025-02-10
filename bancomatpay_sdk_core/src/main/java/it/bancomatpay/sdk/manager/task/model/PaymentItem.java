package it.bancomatpay.sdk.manager.task.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class PaymentItem implements Serializable {

    public enum EPaymentRequestType {
        PREAUTHORIZATION, PAYMENT_REQUEST
    }

    public enum EPaymentCategory {
        billPayment, prepaidRecharge, atmWithdrawal, posWithdrawal;

        public static EPaymentCategory fromValue(String s){
            if(TextUtils.isEmpty(s)){
                return null;
            }
            switch (s){
                case "billPayment":
                    return billPayment;
                case "prepaidRegharge":
                    return prepaidRecharge;
                case "posWithdrawal":
                    return posWithdrawal;
                case "atmWithdrawal":
                    return atmWithdrawal;
                default:
                    return null;
            }
        }
    }

    protected String qrCodeId;
    protected String shopName;
    protected Address address;
    protected String msisdn;
    protected String insignia;
    protected BigDecimal fee;
    protected BigDecimal amount;
    protected BigDecimal totalAmount;
    protected int centsAmount;
    protected int centsTotalAmount;
    protected String paymentId;
    protected String tag;
    protected long shopId;
    protected BigInteger tillId;
    protected String causal;
    protected EPaymentCategory category;
    protected String latitude;
    protected String longitude;
    protected Date paymentDate;
    protected Date expirationDate;
    protected EPaymentRequestType type;
    protected String localCurrency;
    protected BigDecimal localAmount;
    protected boolean qrCodeEmpsa;

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getCentsAmount() {
        return centsAmount;
    }

    public void setCentsAmount(int centsAmount) {
        this.centsAmount = centsAmount;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getInsignia() {
        return insignia;
    }

    public void setInsignia(String insignia) {
        this.insignia = insignia;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public BigInteger getTillId() {
        return tillId;
    }

    public void setTillId(BigInteger tillId) {
        this.tillId = tillId;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public EPaymentRequestType getType() {
        return type;
    }

    public EPaymentCategory getCategory() {
        return category;
    }

    public void setType(EPaymentRequestType type) {
        this.type = type;
    }

    public void setCategory(EPaymentCategory category) {
        this.category = category;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getCentsTotalAmount() {
        return centsTotalAmount;
    }

    public void setCentsTotalAmount(int centsTotalAmount) {
        this.centsTotalAmount = centsTotalAmount;
    }

    public String getLocalCurrency() {
        return localCurrency;
    }

    public void setLocalCurrency(String localCurrency) {
        this.localCurrency = localCurrency;
    }

    public BigDecimal getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(BigDecimal localAmount) {
        this.localAmount = localAmount;
    }

    public boolean isQrCodeEmpsa() {
        return qrCodeEmpsa;
    }

    public void setQrCodeEmpsa(boolean qrCodeEmpsa) {
        this.qrCodeEmpsa = qrCodeEmpsa;
    }
}
