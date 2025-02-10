package it.bancomatpay.sdkui.listener;

import android.app.Activity;

import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;

public interface BCMFullStackAuthenticationListener {
    void authenticationNeededForDispositiveOperation(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackOperationHandlerListener listener);
    void authenticationNeededForProviderAccess(Activity activity, BCMFullStackProviderAccessListener listener, String requestId, String requestTag);
    void authenticationNeededForWithdrawalOperation(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackAtmCardlessListener listener);
    void authenticationNeededForDirectDebits(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackDirectDebitsListener listener);
    void authenticationNeededForCashback(Activity activity, BCMOperationAuthorization bcmPayment, ECashbackAuthorizationTypeRequest cashbackAuthorizationTypeRequest, BCMFullStackCashbackListener listener);
}