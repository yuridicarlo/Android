package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

public class VerifyPinData implements Serializable {

    private int lastAttempts;
    transient private byte[] fingerprintData;

    public int getLastAttempts() {
        return lastAttempts;
    }

    public void setLastAttempts(int lastAttempts) {
        this.lastAttempts = lastAttempts;
    }

    public byte[] getFingerprintData() {
        return fingerprintData;
    }

    public void setFingerprintData(byte[] fingerprintData) {
        this.fingerprintData = fingerprintData;
    }

}
