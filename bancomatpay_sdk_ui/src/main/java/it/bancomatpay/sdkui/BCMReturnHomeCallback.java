package it.bancomatpay.sdkui;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.utilities.GoToHomeInterface;

public class BCMReturnHomeCallback {

    private static final String TAG = BCMReturnHomeCallback.class.getSimpleName();

    private static BCMReturnHomeCallback instance;
    private GoToHomeInterface mListener;

    public static BCMReturnHomeCallback getInstance() {
        if (instance == null) {
            instance = new BCMReturnHomeCallback();
        }
        return instance;
    }

    public void setReturnHomeListener(GoToHomeInterface listener) {
        mListener = listener;
    }

    public GoToHomeInterface getReturnHomeListener() {
        if (mListener != null) {
            return mListener;
        } else {
            return (activity, finishSdkFlow, pinBlocked, returnToIntro) -> CustomLogger.e(TAG, "No BCMReturnHomeCallback specified, please set a listener");
        }
    }

}
