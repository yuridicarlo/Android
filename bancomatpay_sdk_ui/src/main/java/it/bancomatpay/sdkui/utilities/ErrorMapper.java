package it.bancomatpay.sdkui.utilities;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.R;


public class ErrorMapper {

    public static int getStringFromStatusCode(StatusCodeInterface statusCodeInterface) {
        if (statusCodeInterface instanceof StatusCode.Mobile) {
            StatusCode.Mobile mobile = (StatusCode.Mobile) statusCodeInterface;
            switch (mobile) {
                case INVALID_FILE_PROVIDER_AUTHORITY:
                    return R.string.invalid_file_provider_authority_error_message;
                default:
                    return R.string.generic_error_message;
            }
        } else if (statusCodeInterface instanceof StatusCode.Http) {
            /*StatusCode.Http http = (StatusCode.Http) statusCodeInterface;
            switch (http){
                default:
                    return R.string.network_error_message;
            }*/
        } else if (statusCodeInterface instanceof StatusCode.Server) {
            StatusCode.Server server = (StatusCode.Server) statusCodeInterface;
            switch (server) {

                case P2B_QRCODE_IS_NOT_VALID:
                    return R.string.p2b_qrcode_is_not_valid;
                case QRCODE_WRONG_ACTIVATION_CODE:
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
                case P2P_VERIFY_PAYMENT_FAILED:
                    return R.string.p2p_verify_payment_failed;
                case P2P_FAILED_AMOUNT_NOT_VALID:
                    return R.string.request_money_error_max_money_limit_reached;
                case P2B_AMOUNT_NOT_VALID:
                    return R.string.common_p2p_error_amount_not_valid;
                case P2P_FAILED_RECEIVER_NOT_VALID:
                case P2P_RECEIVER_NOT_VALID:
                    return R.string.common_p2p_error_receiver_not_valid;
                /*case PAYMENT_NOT_AVAILABLE_MORE_THAN_ONE_SHOP:
                    return R.string.payment_not_available_more_than_one_shop;*/
                case WRONG_APP_VERSION:
                    return R.string.wrong_app_error_message;
                case EXIT_APP:
                case REFRESH_TOKEN_EXPIRED:
                    return R.string.exit_app_error_message;
                case P2P_USER_NOT_ENABLED_ON_BCM_PAY:
                    return R.string.deactivation_message;
                case GENERIC_SERVER_GENERIC:
                    return R.string.generic_error_message;
                case GENERIC_SERVER_WRONG_MSISDN_FORMAT:
                case EXT_SERVER_WRONG_MSISDN:
                case P2B_SEARCH_SHOP_MSISDN_NOT_VALID:
                    return R.string.activation_insert_phone_fragment_error_wrong;
                case USER_BLOCK_FAILED:
                    return R.string.acceptance_refuse_success_block_failed;
                case P2P_REQUEST_DENY_FAILED:
                    return R.string.money_request_error_in_refusing;
                case P2P_FAILED_INVIA_RICHIESTA_DENARO_ONE_BENEFICIARY_NOT_VALID:
                    return R.string.request_money_error_failed;
                case SDK_BANKID_REQUEST_NOT_VALID:
                    return R.string.sdk_bank_request_not_valid;
                case ATM_QR_CODE_EXPIRED:
                    return R.string.atm_cardless_qr_code_expired;
                case P2P_GENERIC_ERROR:
                case EXT_SERVER_GENERIC:
                case OTHER_GENERIC_SERVER_ERROR:
                case NOT_FOUND_BANK:
                    return R.string.generic_error_message;
                case CASHBACK_UNDERAGE_USER:
                    return R.string.cashback_underage_error_message;
                case P2B_POS_WITHDRAWAL_NOT_ACTIVATED_BY_BANK:
                    return R.string.withdraw_bank_not_enabled_error;
                case POS_QR_CODE_EXPIRED:
                    return R.string.withdrawal_pos_qr_code_expired;
                case POS_DAILY_THRESHOLD_REACHED:
                    return R.string.withdraw_result_daily_limit_overcome;
                case P2B_FAILED_VERIFICA_PAGAMENTO:
                    return R.string.p2p_verify_failed_message;
                case P2B_AMOUNT_IS_NOT_VALID:
                    return R.string.p2p_amount_not_valid_message;
                case P2P_FAILED_INVIA_RICHIESTA_DENARO_BANK_SERVICE_NOT_AVAILABLE:
                    return R.string.request_money_error_not_bmcpay_receiver;
                case P2P_FAILED_INVIA_RICHIESTA_CONTACT_NOT_FOUND:
                    return R.string.request_money_error_for_technical_reasons;
                case P2P_FAILED_INVIA_RICHIESTA_NO_VALID_SERVICE_FOUND:
                    return R.string.request_money_error_no_active_instrument;
                case P2P_FAILED_INVIA_RICHIESTA_BENEFICIARY_NOT_ENABLED_TO_PAYMENT_REQUEST:
                    return R.string.request_money_error_receiving_instrument_disabled;
                case P2P_FAILED_INVIA_RICHIESTA_DENARO_NOT_VALID_BENEFICIARY:
                    return R.string.request_money_error_no_receiving_account;
                case P2P_FAILED_INVIA_RICHIESTA_DENARO:
                    return R.string.get_money_result_ko;
                case P2P_FAILED_SPLIT_BILL_GROUP_TOO_LARGE:
                    return R.string.split_bill_group_too_large_error;
            }
        }
        if (statusCodeInterface instanceof StatusCode.Http) {
            if (statusCodeInterface == StatusCode.Http.NO_RETE) {
                return R.string.network_error_message;
            }
        }
        return R.string.generic_error_message;
    }


}
