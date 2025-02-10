package it.bancomatpay.sdk.manager.task.model;

public class ProfileData {

    private String name;
    private String letter;
    private String image;
    private Thresholds p2PThresholds;
    private Thresholds p2BThresholds;
    private String iban;
    private String msisdn;
    private boolean isDefaultReceiver;

    public boolean isDefaultReceiver() {
        return isDefaultReceiver;
    }

    public void setDefaultReceiver(boolean defaultReceiver) {
        isDefaultReceiver = defaultReceiver;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getName() {
        return name;
    }

    public String getNameTwoLines() {
        String sRet = name;
        if (name.contains(" ")) {
            String[] split = name.split(" ");
            if (split.length == 2) {
                sRet = split[0] + "\n" + split[1];
            }
        }
        return sRet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Thresholds getP2PThresholds() {
        return p2PThresholds;
    }

    public void setP2PThresholds(Thresholds p2PThresholds) {
        this.p2PThresholds = p2PThresholds;
    }

    public Thresholds getP2BThresholds() {
        return p2BThresholds;
    }

    public void setP2BThresholds(Thresholds p2BThresholds) {
        this.p2BThresholds = p2BThresholds;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
