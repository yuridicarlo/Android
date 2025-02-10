package it.bancomat.pay.consumer;

import it.bancomat.pay.consumer.utilities.BCMAuthenticationResultListener;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackActivationResult;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class AppAuthenticationResultCallback {

    private static final String TAG = AppAuthenticationResultCallback.class.getSimpleName();

    private static AppAuthenticationResultCallback instance;
    private BCMAuthenticationResultListener mListener;

    public static AppAuthenticationResultCallback getInstance() {
        if (instance == null) {
            instance = new AppAuthenticationResultCallback();
        }
        return instance;
    }

    public void setAuthenticationResultListener(BCMAuthenticationResultListener listener) {
        mListener = listener;
    }

    public BCMAuthenticationResultListener getAuthenticationResultListener() {
        if (mListener != null) {
            return mListener;
        } else {
            return new BCMAuthenticationResultListener() {
                @Override
                public void onOperationAuthenticationResult(boolean authenticated, BCMOperationAuthorization bcmOperation, String loyaltyToken, String authorizationToken) {
                    CustomLogger.e(TAG, "No AppAuthenticationResultCallback specified, please set a listener");
                }

                @Override
                public void onProviderAccessAuthenticationResult(boolean authenticated, String authorizationToken) {
                    CustomLogger.e(TAG, "No AppAuthenticationResultCallback specified, please set a listener");
                }

                @Override
                public void onWithdrawalAuthenticationResult(boolean authenticated, String authorizationToken) {
                    CustomLogger.e(TAG, "No AppAuthenticationResultCallback specified, please set a listener");
                }

                @Override
                public void onDirectDebitAuthenticationResult(boolean authenticated, String authorizationToken) {
                    CustomLogger.e(TAG, "No AppAuthenticationResultCallback specified, please set a listener");
                }

                @Override
                public void onCashbackAuthenticationResult(boolean authenticated, ECashbackActivationResult result) {
                    CustomLogger.e(TAG, "No AppAuthenticationResultCallback specified, please set a listener");
                }

            };
        }

    }
}
