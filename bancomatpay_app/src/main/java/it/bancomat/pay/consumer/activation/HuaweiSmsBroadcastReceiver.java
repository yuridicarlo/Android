package it.bancomat.pay.consumer.activation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huawei.hms.common.api.CommonStatusCodes;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.sms.common.ReadSmsConstant;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class HuaweiSmsBroadcastReceiver extends BroadcastReceiver {

	private static OtpReceiveListener otpReceiver;

	public static void setOtpListener(OtpReceiveListener receiver) {
		otpReceiver = receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null && ReadSmsConstant.READ_SMS_BROADCAST_ACTION.equals(intent.getAction())) {
			Status status = bundle.getParcelable(ReadSmsConstant.EXTRA_STATUS);
			if (status != null) {
				if (status.getStatusCode() == CommonStatusCodes.TIMEOUT) {
					// The service has timed out and no SMS message that meets the requirement is read. The service process ends.
					if (otpReceiver != null) {
						otpReceiver.onOtpTimeOut();
					}
				} else if (status.getStatusCode() == CommonStatusCodes.SUCCESS) {
					if (bundle.containsKey(ReadSmsConstant.EXTRA_SMS_MESSAGE)) {
						// An SMS message that meets the requirement is read. The service process ends.

						String otp = bundle.getString(ReadSmsConstant.EXTRA_SMS_MESSAGE);
						CustomLogger.d("OTP_Message", otp);
						// Extract one-time code from the message and complete verification
						if (otpReceiver != null && otp != null) {
							//otp = otp.replaceAll("[^\\d]", "").substring(0, 5);
							otp = otp.substring(otp.lastIndexOf("BANCOMAT Pay") + "BANCOMAT Pay".length())
									.replaceAll("[^\\d]", "")
									.substring(0, 6);
							otpReceiver.onOtpReceived(otp);
						}
					}
				}
			}
		}
	}

}
