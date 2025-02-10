package it.bancomatpay.sdkui;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.listener.BCMFullStackAbortListener;
import it.bancomatpay.sdkui.listener.BCMFullStackAuthenticationListener;
import it.bancomatpay.sdkui.listener.BCMFullStackCompleteListener;
import it.bancomatpay.sdkui.listener.BCMFullStackKeepAliveListener;

public interface BancomatFullStackSdkInterface {

    void initBancomatSDKWithCheckRoot(@NonNull Activity activity, String abiCode, String groupCode, String phoneNumber, String phonePrefix, String iban, String sessionToken, BCMFullStackCompleteListener listener);

    void startBancomatPayFlow(@NonNull Activity activity, Drawable logoBank, String sessionToken);

    void showBancomatPayHistory(@NonNull Activity activity, String sessionToken);

    void startPaymentFromNotification(@NonNull Activity activity, String paymentRequestId, String paymentRequestType, String sessionToken, @NonNull BCMFullStackCompleteListener listener);

    void startPaymentFromQrCode(@NonNull Activity activity, String qrCodeString, String sessionToken, @NonNull BCMFullStackCompleteListener listener);

    void setAuthenticationListener(@NonNull BCMFullStackAuthenticationListener listener);

    void setAbortListener(@NonNull BCMFullStackAbortListener listener);

    void setKeepAliveListener(@NonNull BCMFullStackKeepAliveListener listener);

    void resetBancomatSDK();

    class Factory {
        public static BancomatFullStackSdkInterface getInstance() {
            return BancomatFullStackSdk.getInstance();
        }
    }

    enum EBCMFullStackStatusCodes implements StatusCodeInterface {

        SDKAbortType_GENERIC,
        SDKAbortType_USER_EXIT,
        SDKAbortType_EXCEPTION,
        SDKAbortType_SESSION_EXPIRED,
        SDKAbortType_USER_DELETED;

        @Override
        public boolean isSuccess() {
            return false;
        }

    }

}
