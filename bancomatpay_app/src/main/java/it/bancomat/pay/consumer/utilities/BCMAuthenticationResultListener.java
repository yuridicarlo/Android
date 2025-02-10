package it.bancomat.pay.consumer.utilities;

import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;

public interface BCMAuthenticationResultListener {
    void onOperationAuthenticationResult(boolean authenticated, BCMOperationAuthorization bcmOperation, String authorizationToken, String loyaltyToken);
    void onProviderAccessAuthenticationResult(boolean authenticated, String authorizationToken);
    void onWithdrawalAuthenticationResult(boolean authenticated, String authorizationToken);
    void onDirectDebitAuthenticationResult(boolean authenticated, String authorizationToken);
    void onCashbackAuthenticationResult(boolean authenticated, ECashbackActivationResult result);
}
