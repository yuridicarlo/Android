package it.bancomatpay.sdk.manager.utilities;

import it.bancomatpay.sdk.manager.security.encryption.RsaMessageDefaultDecryption;
import it.bancomatpay.sdk.manager.security.encryption.RsaMessageEncryption;
import it.bancomatpay.sdk.manager.security.signature.RsaMessageDefaultSignature;
import it.bancomatpay.sdk.manager.security.signature.RsaMessageSignature;

public enum Cmd {

    INIT("initSDK", RsaMessageDefaultDecryption.class, RsaMessageEncryption.class, RsaMessageDefaultSignature.class, RsaMessageSignature.class),
    ENABLE_USER("enableUser", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_USER_DATA("getUserData", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SYNCH_PHONE_BOOK("synchPhoneBook", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_SHOP_LIST("getShopList", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_QR_CODE_DETAILS("getQrCodeDetails", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    CANCEL_PAYMENT("cancelPayment", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_PAYMENTS_HISTORY("getPaymentsHistory", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    VERIFY_PAYMENT("verifyPayment", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    CONFIRM_PAYMENT("confirmPaymentUnencrypted", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_MERCHANT_DETAILS("getMerchantDetails", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_SHOP_LIST_BY_MERCHANT_NAME("getShopListByMerchantName", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    ALLOW_PAYMENT_REQUESTS("allowPaymentRequests", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SET_INCOMING_DEFAULT_INSTRUMENT("setIncomingDefaultInstrument", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SET_OUTGOING_DEFAULT_INSTRUMENT("setOutgoingDefaultInstrument", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    CONFIRM_PAYMENT_REQUEST("confirmPaymentRequestUnencrypted", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    DENY_PAYMENT_REQUEST("denyPaymentRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_PAYMENT_REQUEST("getPaymentRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    VERIFY_PAYMENT_STATE("verifyPaymentState", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SEND_PAYMENT_REQUEST("sendPaymentRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_OUTGOING_PAYMENT_REQUEST("getOutgoingPaymentRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_BLACKLIST_CONTACTS("getBlackListContacts", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    CANCEL_PREAUTHORIZATION("cancelPreauthorization", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_TRANSACTION_DETAILS("getTransactionDetails", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_THRESHOLDS("getThresholds", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    DISABLE_USER("disableUser", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Flow loyalty cards
    GET_LOYALTY_CARDS("getLoyaltyCards", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SET_LOYALTY_CARD("setLoyaltyCard", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    DELETE_LOYALTY_CARD("deleteLoyaltyCard", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    MODIFY_LOYALTY_CARD("modifyLoyaltyCard", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_LOYALTY_CARD_BRANDS("getLoyaltyCardBrands", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

	//Flow documents
	GET_DOCUMENTS("getDocuments", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	GET_DOCUMENT_IMAGES("getDocumentImages", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	SET_DOCUMENT("setDocument", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	SET_DOCUMENT_IMAGES("setDocumentImages", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	MODIFY_DOCUMENT("modifyDocument", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	DELETE_DOCUMENT("deleteDocument", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

	//Flow Bank Id
	GET_BANK_ID_BLACKLIST("getBankIdBlacklist", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	DELETE_BANK_ID_BLACKLIST("deleteBankIdBlacklist", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	GET_BANK_ID_CONTACTS("getBankIdContacts", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	SET_BANK_ID_CONTACTS("setBankIdContacts", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
	GET_BANK_ID_REQUESTS("getBankIdRequests", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    CONFIRM_BANK_ID_REQUEST("confirmBankIdRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    DENY_BANK_ID_REQUEST("denyBankIdRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_BANK_ID_STATUS("getBankIdStatus", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SET_BANK_ID_STATUS("setBankIdStatus", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Flow ATM Cardless
    CONFIRM_ATM_WITHDRAWAL("confirmAtmWithdrawal", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Flow POS
    CONFIRM_POS_WITHDRAWAL("confirmPosWithdrawal", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Petrol
    CONFIRM_PETROL_PAYMENT("confirmPetrolPayment", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Flusso loyalty
    GET_LOYALTY_JWT("getLoyaltyJwt", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Addebiti diretti
    DENY_DIRECT_DEBIT_REQUEST("denyDirectDebitRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    CONFIRM_DIRECT_DEBIT_REQUEST("confirmDirectDebitRequest", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_DIRECT_DEBITS_HISTORY("getDirectDebitsHistory", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),

    //Cashback pagamenti digitali
    GET_CASHBACK_STATUS("getCashbackStatus", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_BPAY_CASHBACK_TERMS_AND_CONDITIONS("getBpayCashbackTermsAndConditions", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_PAGO_PA_TERMS_AND_CONDITIONS("getPagoPaCashbackTermsAndConditions", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    SUBSCRIBE_CASHBACK("subscribeCashback", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    UNSUBSCRIBE_CASHBACK("unsubscribeCashback", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    DISABLE_CASHBACK("disableCashback", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_CASHBACK_DATA("getCashbackData", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    ACCEPT_CASHBACK_BPAY_TERMS_AND_CONDITIONS("acceptCashbackBpayTermsAndConditions", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    //split bill
    SPLIT_BILL("splitBill", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_SPLIT_BILL_HISTORY("getSplitBillHistory", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    GET_SPLIT_BILL_HISTORY_DETAIL("getSplitBillDetail", RsaMessageEncryption.class, RsaMessageEncryption.class, RsaMessageSignature.class, RsaMessageSignature.class),
    ;

    public String cmdName;
    public Class<?> messageEncryptionClass;
    public Class<?> messageSignatureClass;
    public Class<?> messageDecryptionClass;
    public Class<?> messageSignatureVerifyClass;

    Cmd(String cmdName, Class<?> messageDecryptionClass, Class<?> messageEncryptionClass, Class<?> messageSignatureClass, Class<?> messageSignatureVerifyClass) {
        this.cmdName = cmdName;
        this.messageEncryptionClass = messageEncryptionClass;
        this.messageSignatureClass = messageSignatureClass;
        this.messageSignatureVerifyClass = messageSignatureVerifyClass;
        this.messageDecryptionClass = messageDecryptionClass;
    }

    public static boolean isCmdCrossService(Cmd cmd) {
        boolean bRet = false;
        switch (cmd) {
            case CONFIRM_PAYMENT:
            case VERIFY_PAYMENT:
            case ALLOW_PAYMENT_REQUESTS:
                bRet = true;
                break;
        }
        return bRet;
    }

    @Override
    public String toString() {
        return "Cmd{" +
                "cmdName='" + cmdName + '\'' +
                ", messageEncryptionClass=" + messageEncryptionClass +
                ", messageSignatureClass=" + messageSignatureClass +
                ", messageDecryptionClass=" + messageDecryptionClass +
                ", messageSignatureVerifyClass=" + messageSignatureVerifyClass +
                '}';
    }

}
