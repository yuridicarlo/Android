package it.bancomatpay.sdkui;

import android.app.Activity;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.listener.BCMFullStackAbortListener;
import it.bancomatpay.sdkui.listener.BCMSessionRefreshListener;

public class BCMAbortCallback {

    private static final String TAG = BCMAbortCallback.class.getSimpleName();

    private static BCMAbortCallback instance;
    private BCMFullStackAbortListener mListener;
    private BCMSessionRefreshListener mSessionRefreshListener;

    public static BCMAbortCallback getInstance() {
        if (instance == null) {
            instance = new BCMAbortCallback();
        }
        return  instance;
    }

    void setAbortListener(BCMFullStackAbortListener listener) {
        mListener = listener;
    }

    public BCMFullStackAbortListener getAuthenticationListener() {
        if (mListener != null) {
            return mListener;
        } else {
            return new BCMFullStackAbortListener() {
                @Override
                public void onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes statusCode) {
                    CustomLogger.e(TAG, "No BCMAbortCallback specified, please set a listener");
                }

                @Override
                public void onAbortSession(Activity activity, BCMSessionRefreshListener listener) {
                    CustomLogger.e(TAG, "No BCMAbortCallback specified, please set a listener");
                }
            };
        }
    }

    public void setSessionRefreshListener(BCMSessionRefreshListener listener) {
        this.mSessionRefreshListener = listener;
    }

    public BCMSessionRefreshListener getSessionRefreshListener() {
        if (mSessionRefreshListener != null) {
            return mSessionRefreshListener;
        } else {
            return refreshedToken -> CustomLogger.e(TAG, "No BCMSessionRefreshListener specified, please set a listener");
        }
    }

}
