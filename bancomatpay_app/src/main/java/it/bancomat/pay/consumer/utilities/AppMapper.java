package it.bancomat.pay.consumer.utilities;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.network.dto.PendingPayment;
import it.bancomat.pay.consumer.network.dto.PendingPaymentsData;
import it.bancomat.pay.consumer.network.dto.response.DtoPendingPayment;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyPendingPaymentsResponse;
import it.bancomat.pay.consumer.utilities.statuscode.AppStatusCodeWrapper;
import it.bancomatpay.sdk.manager.network.dto.DtoBankData;
import it.bancomatpay.sdk.manager.network.dto.DtoStatus;
import it.bancomatpay.sdk.manager.task.model.BankDataMultiIban;
import it.bancomatpay.sdk.manager.utilities.Mapper;

public class AppMapper {

    public static ArrayList<String> getBankIdList(List<DtoBankData> dtoBankDataList){
        ArrayList<String> bankDataList = new ArrayList<>();
        if(dtoBankDataList != null){
            for (DtoBankData dtoBankData : dtoBankDataList){
                bankDataList.add(dtoBankData.getBankUUID());
            }
        }
        return bankDataList;
    }

    public static ArrayList<BankDataMultiIban> getBankDataMultiIban(List<DtoBankData> dtoBankDataList){
        ArrayList<BankDataMultiIban> bankDataList = new ArrayList<>();
        if(dtoBankDataList != null){
            for (DtoBankData dtoBankData : dtoBankDataList){
                BankDataMultiIban bankDataMultiIban = new BankDataMultiIban();
                bankDataMultiIban.setBankUUID(dtoBankData.getBankUUID());
                bankDataMultiIban.setMultiIban(dtoBankData.isMultiIban());
                bankDataMultiIban.setInstruments(dtoBankData.getInstruments());

                bankDataList.add(bankDataMultiIban);
            }
        }
        return bankDataList;
    }

    public static DataBank getDataBank(DtoBankData dtoBankData){
        DataBank dataBank = new DataBank();
        dataBank.setBankUUID(dtoBankData.getBankUUID());
        dataBank.setMultiIban(dtoBankData.isMultiIban());
        dataBank.setInstrument(Mapper.getInstruments(dtoBankData.getInstruments()));
        dataBank.setLinkStore(dtoBankData.getUrlAppAndroid());
        return dataBank;
    }

    public static PendingPaymentsData getPendingPaymentList(DtoVerifyPendingPaymentsResponse response) {
        PendingPaymentsData data = new PendingPaymentsData();
        data.setLastAttempts(response.getLastAttempts());
        data.setInstrumentId(response.getInstrumentId());
        ArrayList<PendingPayment> payments = new ArrayList<>();
        for(DtoPendingPayment payment : response.getPendingPayments()) {
            payments.add(getPendingPayment(payment));
        }
        data.setPendingPayments(payments);

        return data;
    }

    private static PendingPayment getPendingPayment(DtoPendingPayment dtoPayment) {
        PendingPayment payment = new PendingPayment();
        payment.setPaymentId(dtoPayment.getPaymentId());
        payment.setCausal(dtoPayment.getCausal());
        payment.setMsisdnSender(dtoPayment.getMsisdnSender());
        payment.setAmount(dtoPayment.getAmount());
        payment.setDtoPendingPayment(dtoPayment);
        return payment;
    }

    public static AppStatusCodeWrapper getStatusCodeWrapper(DtoStatus dtoStatus) {
        AppStatusCodeWrapper codeServer;
        try {
            if (!TextUtils.isEmpty(dtoStatus.getStatusDetailCode())) {
                codeServer = AppStatusCodeWrapper.valueOf("SCS_" + dtoStatus.getStatusCode() + "_" + dtoStatus.getStatusDetailCode());
            } else {
                codeServer = AppStatusCodeWrapper.valueOf("SCS_" + dtoStatus.getStatusCode());
            }
        } catch (Exception e) {
            codeServer = AppStatusCodeWrapper.SCS_9999_9999;
        }
        return codeServer;
    }

}
