package it.bancomatpay.sdkui.listener;

import android.app.Activity;

import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;

public interface BCMFullStackAbortListener {
    void onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes statusCode);
    void onAbortSession(Activity activity, BCMSessionRefreshListener listener);
}
