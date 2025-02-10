package it.bancomatpay.sdkui;

import android.app.Activity;

import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.ECashbackAuthorizationTypeRequest;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.listener.BCMFullStackAtmCardlessListener;
import it.bancomatpay.sdkui.listener.BCMFullStackAuthenticationListener;
import it.bancomatpay.sdkui.listener.BCMFullStackCashbackListener;
import it.bancomatpay.sdkui.listener.BCMFullStackDirectDebitsListener;
import it.bancomatpay.sdkui.listener.BCMFullStackOperationHandlerListener;
import it.bancomatpay.sdkui.listener.BCMFullStackProviderAccessListener;

public class BCMAuthenticationCallback {

    private static final String TAG = BCMAuthenticationCallback.class.getSimpleName();

    private static BCMAuthenticationCallback instance;
    private BCMFullStackAuthenticationListener mListener;

    public static BCMAuthenticationCallback getInstance() {
        if (instance == null) {
            instance = new BCMAuthenticationCallback();
        }
        return instance;
    }

    void setAuthenticationListener(BCMFullStackAuthenticationListener listener) {
        mListener = listener;
    }

    public BCMFullStackAuthenticationListener getAuthenticationListener() {
        if (mListener != null) {
            return mListener;
        } else {
            return new BCMFullStackAuthenticationListener() {
                @Override
                public void authenticationNeededForDispositiveOperation(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackOperationHandlerListener listener) {
                    CustomLogger.e(TAG, "No BCMAuthenticationCallback specified, please set a listener");
                }

                @Override
                public void authenticationNeededForProviderAccess(Activity activity, BCMFullStackProviderAccessListener listener, String requestId, String requestTag) {
                    CustomLogger.e(TAG, "No BCMAuthenticationCallback specified, please set a listener");
                }

                @Override
                public void authenticationNeededForWithdrawalOperation(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackAtmCardlessListener listener) {
                    CustomLogger.e(TAG, "No BCMAuthenticationCallback specified, please set a listener");
                }

                @Override
                public void authenticationNeededForDirectDebits(Activity activity, BCMOperationAuthorization bcmPayment, BCMFullStackDirectDebitsListener listener) {
                    CustomLogger.e(TAG, "No BCMAuthenticationCallback specified, please set a listener");
                }

                @Override
                public void authenticationNeededForCashback(Activity activity, BCMOperationAuthorization bcmPayment, ECashbackAuthorizationTypeRequest cashbackAuthorizationTypeRequest, BCMFullStackCashbackListener listener) {
                    CustomLogger.e(TAG, "No BCMAuthenticationCallback specified, please set a listener");
                }
            };
        }
    }

}
