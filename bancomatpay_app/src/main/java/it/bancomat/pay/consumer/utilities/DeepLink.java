package it.bancomat.pay.consumer.utilities;

public class DeepLink {

    private String qrDataId;

    public DeepLink(String qrDataId) {
        this.qrDataId = qrDataId;
    }

    public String getQrDataId() {
        return qrDataId;
    }
}
