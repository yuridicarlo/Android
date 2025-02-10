package it.bancomat.pay.consumer.utilities;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

public class AppErrorMapper {

    public static int getStringFromStatusCode(StatusCodeInterface statusCodeInterface) {
        if (statusCodeInterface instanceof StatusCode.Mobile) {
            StatusCode.Mobile mobile = (StatusCode.Mobile) statusCodeInterface;
            switch (mobile){
                case ROOTED:
                    return R.string.root_error_message;
                case FINGERPRINT_MODIFIED:
                    return R.string.fingerprint_modified_error_message;
            }
        }
        if (statusCodeInterface instanceof StatusCode.Http) {
            StatusCode.Http http = (StatusCode.Http) statusCodeInterface;
            switch (http){
                default:
                    return R.string.network_error_message;
            }
        }
        if (statusCodeInterface instanceof StatusCode.Server) {
            StatusCode.Server server = (StatusCode.Server) statusCodeInterface;
            switch (server) {

                case P2B_QRCODE_IS_NOT_VALID:
                    return R.string.p2b_qrcode_is_not_valid;
                case QRCODE_WRONG_ACTIVATION_CODE:
                    return R.string.activation_home_banking_code_activation_error;
                case GENERIC_SERVER_QRCODE:
                case QRCODE_GENERIC:
                    return R.string.wrong_or_not_valid_qr_code;
                case P2B_MERCHANT_NOT_FOUND:
                    return R.string.common_p2b_qr_error_merchant_not_found;
                case P2B_DAILY_THRESHOLD_REACHED:
                    return R.string.common_p2b_error_daily_threshold_reached;
                case P2B_MONTHLY_THRESHOLD_REACHED:
                    return R.string.common_p2b_error_month_threshold_reached;
                case P2B_RETRIEVE_PAYMENT_FAILED:
                    return R.string.p2b_failed_get_payment_msg;
                case P2P_DESCRIPTION_NOT_VALID:
                    return R.string.common_p2p_error_description_not_valid;
                case P2P_FAILED_AMOUNT_NOT_VALID:
                    return R.string.request_money_error_max_money_limit_reached;
                case P2B_AMOUNT_NOT_VALID:
                    return R.string.common_p2p_error_amount_not_valid;
                case P2P_FAILED_RECEIVER_NOT_VALID:
                    return R.string.common_p2b_error_receiver_not_valid;
                case P2P_RECEIVER_NOT_VALID:
                    return R.string.common_p2p_error_receiver_not_valid;
                /*case PAYMENT_NOT_AVAILABLE_MORE_THAN_ONE_SHOP:
                    return R.string.payment_not_available_more_than_one_shop;*/
                case P2P_USER_NOT_ENABLED_ON_BCM_PAY:
                    return R.string.deactivation_message;
                case WRONG_APP_VERSION:
                    return R.string.wrong_app_error_message;
                case EXIT_APP:
                case REFRESH_TOKEN_EXPIRED:
                    return R.string.exit_app_error_message;
                case GENERIC_SERVER_GENERIC:
                    return R.string.generic_error_message;
                case GENERIC_SERVER_WRONG_MSISDN_FORMAT:
                case EXT_SERVER_WRONG_MSISDN:
                case P2B_SEARCH_SHOP_MSISDN_NOT_VALID:
                    return R.string.activation_insert_phone_fragment_error_wrong;
                case USER_BLOCK_FAILED:
                    return R.string.acceptance_refuse_success_block_failed;
                case P2P_REQUEST_DENY_FAILED:
                case P2P_FAILED_INVIA_RICHIESTA_DENARO_ONE_BENEFICIARY_NOT_VALID:
                    return R.string.request_money_error_failed;
                case P2P_GENERIC_ERROR:
                case EXT_SERVER_GENERIC:
                case OTHER_GENERIC_SERVER_ERROR:
                case NOT_FOUND_BANK:
                    return R.string.generic_error_message;
                case EXT_SERVER_WRONG_OTP:
                    return R.string.activation_insert_otp_fragment_error_wrong;
                case EXT_SERVER_EXPIRED_OTP:
                    return R.string.activation_insert_otp_fragment_error_expired;
                case EXT_SERVER_MAX_OTP_NUMBER_REACHED:
                    return R.string.activation_insert_otp_fragment_error_max_otp;
                case P2P_FAILED_VERIFY_ONE_SHOT_OFFLINE:
                    return R.string.one_shot_generic_error;
                case P2P_FAILED_VERIFY_ONE_SHOT_OFFLINE_WRONG_CODE:
                    return R.string.one_shot_insert_otp_fragment_error_wrong;/*
                case CASHBACK_TIMEOUT_EXCEPTION:
                    return R.string.cashback_activation_timeout_description;*/
            }
        }
        return R.string.generic_error_message;
    }


}
