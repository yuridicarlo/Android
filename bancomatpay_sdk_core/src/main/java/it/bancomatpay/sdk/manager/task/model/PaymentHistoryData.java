package it.bancomatpay.sdk.manager.task.model;

import java.util.ArrayList;

public class PaymentHistoryData {

    private ArrayList<TransactionData> transactionDatas;

    public ArrayList<TransactionData> getTransactionDatas() {
        return transactionDatas;
    }

    public void setTransactionDatas(ArrayList<TransactionData> transactionDatas) {
        this.transactionDatas = transactionDatas;
    }

}
