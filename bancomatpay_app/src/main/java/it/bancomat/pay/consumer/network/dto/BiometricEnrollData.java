package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;

import it.bancomat.pay.consumer.storage.model.Pskc;

public class BiometricEnrollData implements Serializable {

    private String abiCode;
    private String groupCode;
    private byte[] biometricData;
    private Pskc pskc;


    public byte[] getBiometricData() {
        return biometricData;
    }

    public void setBiometricData(byte[] biometricData) {
        this.biometricData = biometricData;
    }

    public String getAbiCode() {
        return abiCode;
    }

    public void setAbiCode(String abiCode) {
        this.abiCode = abiCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Pskc getPskc() {
        return pskc;
    }

    public void setPskc(Pskc pskc) {
        this.pskc = pskc;
    }
}
