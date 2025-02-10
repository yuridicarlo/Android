package it.bancomatpay.sdkui;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.listener.BCMFullStackKeepAliveListener;

public class BCMKeepAliveCallback {

    private static final String TAG = BCMKeepAliveCallback.class.getSimpleName();

    private static BCMKeepAliveCallback instance;
    private BCMFullStackKeepAliveListener mListener;

    public static BCMKeepAliveCallback getInstance() {
        if (instance == null) {
            instance = new BCMKeepAliveCallback();
        }
        return  instance;
    }

    void setAuthenticationListener(BCMFullStackKeepAliveListener listener) {
        mListener = listener;
    }

    public BCMFullStackKeepAliveListener getAuthenticationListener() {
        if (mListener != null) {
            return mListener;
        } else {
            return activityName -> CustomLogger.e(TAG, "No BCMKeepAliveCallback specified, please set a listener");
        }
    }

}
