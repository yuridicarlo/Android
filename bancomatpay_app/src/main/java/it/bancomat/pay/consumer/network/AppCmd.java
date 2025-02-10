package it.bancomat.pay.consumer.network;

import it.bancomat.pay.consumer.network.security.encryption.AppRsaMessageDefaultDecryption;
import it.bancomat.pay.consumer.network.security.encryption.AppRsaMessageEncryption;
import it.bancomat.pay.consumer.network.security.signature.AppRsaMessageDefaultSignature;
import it.bancomat.pay.consumer.network.security.signature.AppRsaMessageSignature;
import it.bancomatpay.sdk.manager.security.encryption.RsaMessageEncryption;
import it.bancomatpay.sdk.manager.security.signature.RsaMessageSignature;

public enum AppCmd {

    INIT("init", AppRsaMessageDefaultDecryption.class, AppRsaMessageEncryption.class, AppRsaMessageDefaultSignature.class, AppRsaMessageSignature.class),
    INIT_USER("initUser", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_BANKS_CONFIGURATION_FILE("getBanksConfigurationFile", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    VERIFY_OTP("verifyOTP", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    VERIFY_ACTIVATION_CODE("verifyActivationCode", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    VERIFY_ACTIVATION_CODE_V2("verifyActivationCodeV2", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    VERIFY_OTP_V2("verifyOtpV2", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GENERATE_NEW_OTP("generateNewOtp", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    SET_PIN("setPin", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    SET_PIN_V2("setPinV2", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_USER_BANK_LIST("getUserBankList", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    LOGIN("login", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    REFRESH_TOKEN("refreshToken", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    VERIFY_PIN("verifyPin", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    MODIFY_PIN("modifyPin", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    MODIFY_PIN_V2("modifyPinV2", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),

    GET_AUTHORIZATION_TOKEN("getAuthorizationToken", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    PUSH_REGISTRATION("pushRegistration", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    CHECK_PENDING_PAYMENTS("checkPendingPayments", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    VERIFY_PENDING_PAYMENTS("verifyPendingPayments", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    CONFIRM_PENDING_PAYMENT("confirmPendingPayment", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    SET_CUSTOMER_JOURNEY_TAG("setCustomerJourneyTag", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    SET_CUSTOMER_JOURNEY_CONSENTS("setCustomerJourneyConsents", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),

    //Flow user monitoring
    USER_MONITORING("userMonitoring", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),

    //store locator
    GET_STORE_LOCATOR("getStoreLocator", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_STORE_LOCATOR_SEARCH_REQUEST("getStoreLocatorSearchRequest", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_STORE_LOCATOR_MCC("getStoreLocatorMcc", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_STORE_LOCATOR_PRE("getStoreLocatorPre", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_STORE_LOCATOR_SEARCH_REQUEST_PRE("getStoreLocatorPreSearchRequest", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    GET_STORE_LOCATOR_MCC_PRE("getStoreLocatorPreMcc", AppRsaMessageEncryption.class, AppRsaMessageEncryption.class, AppRsaMessageSignature.class, AppRsaMessageSignature.class),
    ;



    public String cmdName;
    public Class<?> messageEncryptionClass;
    public Class<?> messageSignatureClass;
    public Class<?> messageDecryptionClass;
    public Class<?> messageSignatureVerifyClass;

    AppCmd(String cmdName, Class<?> messageDecryptionClass, Class<?> messageEncryptionClass, Class<?> messageSignatureClass, Class<?> messageSignatureVerifyClass) {
        this.cmdName = cmdName;
        this.messageEncryptionClass = messageEncryptionClass;
        this.messageSignatureClass = messageSignatureClass;
        this.messageSignatureVerifyClass = messageSignatureVerifyClass;
        this.messageDecryptionClass = messageDecryptionClass;
    }

    @Override
    public String toString() {
        return "AppCmd{" +
                "cmdName='" + cmdName + '\'' +
                ", messageEncryptionClass=" + messageEncryptionClass +
                ", messageSignatureClass=" + messageSignatureClass +
                ", messageDecryptionClass=" + messageDecryptionClass +
                ", messageSignatureVerifyClass=" + messageSignatureVerifyClass +
                '}';
    }

}
