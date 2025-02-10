package it.bancomat.pay.consumer.network.totp;

import javax.crypto.SecretKey;

public class DerivedKeyInfoContainer {

    private SecretKey key;
    private String saltB64;
    private int itCounter;

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public String getSaltB64() {
        return saltB64;
    }

    public void setSaltB64(String saltB64) {
        this.saltB64 = saltB64;
    }

    public int getItCounter() {
        return itCounter;
    }

    public void setItCounter(int itCounter) {
        this.itCounter = itCounter;
    }

}
