package it.bancomat.pay.consumer.network.dto;

import java.io.Serializable;
import java.util.ArrayList;

import it.bancomatpay.sdk.manager.task.model.BankDataMultiIban;

public class OtpData implements Serializable {

    private ArrayList<String> bankUuidList;
    private ArrayList<BankDataMultiIban> bankDataList;
    private String token;

    public ArrayList<String> getBankUuidList() {
        return bankUuidList;
    }

    public void setBankUuidList(ArrayList<String> bankUuidList) {
        this.bankUuidList = bankUuidList;
    }

    public ArrayList<BankDataMultiIban> getBankDataList() {
        return bankDataList;
    }

    public void setBankDataList(ArrayList<BankDataMultiIban> bankDataList) {
        this.bankDataList = bankDataList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
