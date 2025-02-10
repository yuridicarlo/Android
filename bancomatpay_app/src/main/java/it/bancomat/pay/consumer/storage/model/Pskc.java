package it.bancomat.pay.consumer.storage.model;

import androidx.annotation.NonNull;
public class Pskc {

    private String macMethodCipValue;
    private String keyPackageCipherValue;
    private String expiredTimestamp;
    private int intervalInSeconds;
    private int iteratorCounter;
    private int keyLength;
    private int requiredDigit;
    private String salt;
    private String version;

    public String getMacMethodCipValue() {
        return macMethodCipValue;
    }

    public void setMacMethodCipValue(String macMethodCipValue) {
        this.macMethodCipValue = macMethodCipValue;
    }

    public String getKeyPackageCipherValue() {
        return keyPackageCipherValue;
    }

    public void setKeyPackageCipherValue(String keyPackageCipherValue) {
        this.keyPackageCipherValue = keyPackageCipherValue;
    }

    public String getExpiredTimestamp() {
        return expiredTimestamp;
    }

    public void setExpiredTimestamp(String expiredTimestamp) {
        this.expiredTimestamp = expiredTimestamp;
    }

    public int getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void setIntervalInSeconds(int intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }

    public int getIteratorCounter() {
        return iteratorCounter;
    }

    public void setIteratorCounter(int iteratorCounter) {
        this.iteratorCounter = iteratorCounter;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public int getRequiredDigit() {
        return requiredDigit;
    }

    public void setRequiredDigit(int requiredDigit) {
        this.requiredDigit = requiredDigit;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @NonNull
    @Override
    public String toString() {
        return "Pskc{" +
                "macMethodCipValue='" + macMethodCipValue + '\'' +
                ", keyPackageCipherValue='" + keyPackageCipherValue + '\'' +
                ", expiredTimestamp='" + expiredTimestamp + '\'' +
                ", intervalInSeconds=" + intervalInSeconds +
                ", iteratorCounter=" + iteratorCounter +
                ", keyLength=" + keyLength +
                ", requiredDigit=" + requiredDigit +
                ", salt='" + salt + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

}
