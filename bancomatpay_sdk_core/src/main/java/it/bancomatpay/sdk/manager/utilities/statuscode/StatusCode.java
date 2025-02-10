package it.bancomatpay.sdk.manager.utilities.statuscode;

public class StatusCode {

    public enum Http implements StatusCodeInterface {
        GENERIC_ERROR,
        UNAUTHORIZED,
        CANCELLED,
        NO_RETE;

        @Override
        public String toString() {
            return "Http{" +
                    "enum='" + super.toString() + '\'' +
                    '}';
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }

    public enum Mobile implements StatusCodeInterface {

        OK,
        GENERIC_ERROR,
        ROOTED,
        FINGERPRINT_MODIFIED,
        DECODE_BITMAP_ERROR,
        ENCODE_BASE_64_ERROR,
        SECURITY,
        NOT_INITIALIZED,
        INVALID_PARAMETER,

        /*new Mobile Status ErrorCode*/
        CANNOT_RETRIEVE_INFO_WITH_TOUCH_ID,
        CANNOT_SAVE_DATA,
        PERMISSION_REFUSED,
        USER_FAILED_AUTH,
        USER_TOUCH_ID_BLOCKED,
        USER_TOUCH_ID_POPUP_CLOSED,
        PRSESSION_ERROR_HANDLER,
        INVALID_FILE_PROVIDER_AUTHORITY;

        @Override
        public String toString() {
            return "Mobile{" +
                    "enum='" + super.toString() + '\'' +
                    '}';
        }

        @Override
        public boolean isSuccess() {
            return this == OK;
        }
    }


    public enum Server implements StatusCodeInterface {
        GENERIC_SERVER_GENERIC,
        GENERIC_SERVER_WRONG_MSISDN_FORMAT,
        GENERIC_SERVER_QRCODE,
        EXIT_APP,
        NOT_FOUND_BANK,
        WRONG_APP_VERSION,
        USER_BLOCK_FAILED,
        P2P_GENERIC_ERROR,
        P2P_VERIFY_PAYMENT_FAILED,
        P2P_RECEIVER_NOT_VALID,
        P2P_USER_NOT_ENABLED_ON_BCM_PAY,
        P2P_FAILED_AMOUNT_NOT_VALID,
        P2P_FAILED_RECEIVER_NOT_VALID,
        P2P_DESCRIPTION_NOT_VALID,
        P2P_FAILED_INVIA_RICHIESTA_DENARO_ONE_BENEFICIARY_NOT_VALID,
        P2P_REQUEST_DENY_FAILED,
        EXT_SERVER_WRONG_MSISDN,
        EXT_SERVER_WRONG_OTP,
        EXT_SERVER_EXPIRED_OTP,
        EXT_SERVER_MAX_OTP_NUMBER_REACHED,
        EXT_SERVER_GENERIC,
        QRCODE_WRONG_ACTIVATION_CODE,
        QRCODE_GENERIC,
        P2B_FAILED_VERIFICA_PAGAMENTO,
        P2B_MERCHANT_NOT_FOUND,
        P2B_AMOUNT_NOT_VALID,
        P2B_AMOUNT_IS_NOT_VALID,
        P2B_DAILY_THRESHOLD_REACHED,
        P2B_MONTHLY_THRESHOLD_REACHED,
        P2B_QRCODE_IS_NOT_VALID,
        P2B_RETRIEVE_PAYMENT_FAILED,
        P2B_SEARCH_SHOP_FAILED,
        P2B_SEARCH_SHOP_MSISDN_NOT_VALID,
        REFRESH_TOKEN_EXPIRED,
        OTHER_GENERIC_SERVER_ERROR,
        LOYALTY_CARD_ALREADY_REGISTERED,
        SDK_BANKID_REQUEST_NOT_VALID,
        BANK_ID_DISABLED,
        ATM_QR_CODE_EXPIRED,
      //  PAYMENT_NOT_AVAILABLE_MORE_THAN_ONE_SHOP,
        P2P_FAILED_REQUEST_COLLECTION_OFFLINE_NO_PENDING_PAYMENTS,
        P2P_FAILED_VERIFY_ONE_SHOT_OFFLINE_WRONG_CODE,
        P2P_FAILED_VERIFY_ONE_SHOT_OFFLINE,
        P2P_FAILED_VERIFY_ONE_SHOT_OFFLINE_MAX_ATTEMPTS_REACHED,
        P2P_FAILED_CONFIRM_ONE_SHOT_OFFLINE,
        P2P_FAILED_REQUEST_ONE_SHOT_OFFLINE,
        NO_ACTIVE_IBAN,
        LOYALTY_TOKEN_EXPIRED,
        UNABLE_TO_COMMUNICATE_WITH_WS,
        CASHBACK_FAILED_ENROLLMENT,
        CASHBACK_UNDEFINED_ENROLLMENT,
        CASHBACK_UNDERAGE_USER,
        CASHBACK_DISABLING_FAILED,
        CASHBACK_DISABLING_FAILED_DUE_TO_OTHER_INSTRUMENTS,
        P2B_POS_WITHDRAWAL_NOT_ACTIVATED_BY_BANK,
        POS_QR_CODE_EXPIRED,
        POS_DAILY_THRESHOLD_REACHED,
        P2P_FAILED_INVIA_RICHIESTA_DENARO_BANK_SERVICE_NOT_AVAILABLE,
        P2P_FAILED_INVIA_RICHIESTA_CONTACT_NOT_FOUND,
        P2P_FAILED_INVIA_RICHIESTA_NO_VALID_SERVICE_FOUND,
        P2P_FAILED_INVIA_RICHIESTA_BENEFICIARY_NOT_ENABLED_TO_PAYMENT_REQUEST,
        P2P_FAILED_INVIA_RICHIESTA_DENARO,
        P2P_FAILED_INVIA_RICHIESTA_DENARO_NOT_VALID_BENEFICIARY,
        P2P_FAILED_SPLIT_BILL_GROUP_TOO_LARGE,
        GENERIC_SERVER_ERROR;

        @Override
        public boolean isSuccess() {
            return false;
        }
    }

}

