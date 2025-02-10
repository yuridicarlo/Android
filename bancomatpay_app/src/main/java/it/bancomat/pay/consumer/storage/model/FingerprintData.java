package it.bancomat.pay.consumer.storage.model;

public class FingerprintData {

    private byte[] seed;
    private byte[] hmacKey;

    public byte[] getSeed() {
        return seed;
    }

    public void setSeed(byte[] seed) {
        this.seed = seed;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(byte[] hmacKey) {
        this.hmacKey = hmacKey;
    }
}
