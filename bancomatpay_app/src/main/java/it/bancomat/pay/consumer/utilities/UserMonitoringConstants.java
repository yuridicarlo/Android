package it.bancomat.pay.consumer.utilities;

public class UserMonitoringConstants {

    //TAG
    public static final String ACTIVATION_TAG = "ACTIVATION";

    public static final String ECOMMERCE_TAP_ON_PUSH = "TAP_ON_PUSH";

    public static final String WRONG_FINGERPRINT_LOGIN = "LOGIN_AUTHORIZATION_FAILED";
    public static final String WRONG_FINGERPRINT_PAYMENT = "PAYMENT_AUTHORIZATION_FAILED";

    //Eventi in app per flusso attivazione
    public static final String ACTIVATION_BANK_SELECTED = "BANK_SELECTED";
    public static final String ACTIVATION_OTP_AUTOFILL = "OTP_AUTOFILL";
    public static final String ACTIVATION_THROUGH_QRCODE = "ACTIVATION_CODE_QRCODE";
    public static final String ACTIVATION_TROUGH_CODE = "ACTIVATION_CODE_MANUAL";


    //Eventi in app per pagamento con fingerprint
    public static final String WRONG_FINGERPRINT_PAYMENT_EVENT = "FINGERPRINT_NOT_VALID";
    public static final String WRONG_FINGERPRINT_LOGIN_EVENT = "FINGERPRINT_NOT_VALID";
    public static final String ECOMMERCE_TAP_ON_PUSH_EVENT = "ECOMMERCE_PUSH";



}


