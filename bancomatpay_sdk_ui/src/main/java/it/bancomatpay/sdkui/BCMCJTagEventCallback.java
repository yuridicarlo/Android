package it.bancomatpay.sdkui;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.listener.BCMFullStackCJEventListener;

public class BCMCJTagEventCallback {

    private static final String TAG = BCMCJTagEventCallback.class.getSimpleName();

    private static BCMCJTagEventCallback instance;
    private BCMFullStackCJEventListener mCJEventListener;

    public static BCMCJTagEventCallback getInstance() {
        if (instance == null) {
            instance = new BCMCJTagEventCallback();
        }
        return  instance;
    }

    public void setCJEventListener(BCMFullStackCJEventListener mCJEventListener) {
        this.mCJEventListener = mCJEventListener;
    }

    public BCMFullStackCJEventListener getCJEventListener() {
        if (mCJEventListener != null) {
            return mCJEventListener;
        } else {
            return (context, tagEvent) -> CustomLogger.e(TAG, "No BCMFullStackCJEventListener specified, please set a listener");
        }
    }

}
