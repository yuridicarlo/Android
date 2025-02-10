package it.bancomat.pay.consumer.activation.databank;

import androidx.annotation.NonNull;

import java.util.List;

class BankInfoDataListJson {

    public String version;
    List<Bank> subscribedBanks;
    List<Bank> unsubscribedBanks;

    @NonNull
    @Override
    public String toString() {
        return "BankInfoDataListJson{" +
                "version='" + version + '\'' +
                ", subscribedBanks=" + subscribedBanks +
                ", unsubscribedBanks=" + unsubscribedBanks +
                '}';
    }

}
