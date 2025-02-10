package it.bancomat.pay.consumer.activation;

public interface OtpReceiveListener {
	void onOtpReceived(String otp);

	void onOtpTimeOut();
}
