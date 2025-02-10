package it.bancomat.pay.consumer.activation;

public class OtpReceivedEvent {

    private String otp;

    public OtpReceivedEvent(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
