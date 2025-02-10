package it.bancomat.pay.consumer.activation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class GoogleSmsBroadcastReceiver extends BroadcastReceiver {

    private static OtpReceiveListener otpReceiver;

    public static void setOtpListener(OtpReceiveListener receiver) {
        GoogleSmsBroadcastReceiver.otpReceiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {

            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String otp = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);
                            CustomLogger.d("OTP_Message", otp);
                            // Extract one-time code from the message and complete verification
                            if (otpReceiver != null && otp != null) {
                                //otp = otp.replaceAll("[^\\d]", "").substring(0, 5);
                                otp = otp.substring(otp.lastIndexOf("BANCOMAT Pay") + "BANCOMAT Pay".length())
                                        .replaceAll("[^\\d]", "")
                                        .substring(0, 6);
                                otpReceiver.onOtpReceived(otp);
                            }
                            break;

                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            // Handle the error ...
                            if (otpReceiver != null) {
                                otpReceiver.onOtpTimeOut();
                            }
                            break;
                    }
                }
            }
        }
    }

}
