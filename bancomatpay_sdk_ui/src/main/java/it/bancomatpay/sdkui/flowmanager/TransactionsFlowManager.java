package it.bancomatpay.sdkui.flowmanager;

import android.app.Activity;
import android.content.Intent;

import it.bancomatpay.sdkui.activities.SaveContactNumberActivity;
import it.bancomatpay.sdkui.activities.TransactionDetailActivity;
import it.bancomatpay.sdkui.activities.TransactionReceiptActivity;
import it.bancomatpay.sdkui.model.Transaction;
import it.bancomatpay.sdkui.model.TransactionOutgoing;

public class TransactionsFlowManager {

    public static final String TRANSACTION_DATA = "transactionData";
    public static final String TRANSACTION_DATA_OUTGOING = "transactionDataOutgoing";
    public static final String PHONE_NUMBER = "phoneNumber";
    //public static final String TRANSACTIONS_MSISDN = "transactions_msisdn";

    public static void goToDetailTransaction(Activity activity, Transaction item) {
        Intent i = new Intent(activity, TransactionDetailActivity.class);
        i.putExtra(TRANSACTION_DATA, item);
        activity.startActivity(i);
    }

    public static void goToDetailTransactionOutgoing(Activity activity, TransactionOutgoing item) {
        Intent i = new Intent(activity, TransactionDetailActivity.class);
        i.putExtra(TRANSACTION_DATA_OUTGOING, item);
        activity.startActivity(i);
    }

    /*public static void goToListTransaction(Activity activity) {
        Intent i = new Intent(activity, TransactionListActivity.class);
        activity.startActivity(i);
    }*/

    /*public static void goToShowAllUserTransactions(Activity activity, String msisdn) {
        Intent i = new Intent(activity, AllUserTransactionsActivity.class);
        i.putExtra(TRANSACTIONS_MSISDN, msisdn);
        activity.startActivity(i);
    }*/

    public static void goToTransactionReceipt(Activity activity, Transaction item) {
        Intent i = new Intent(activity, TransactionReceiptActivity.class);
        i.putExtra(TRANSACTION_DATA, item);
        activity.startActivity(i);
    }

    public static void goToSaveContact(Activity activity, String phoneNumber) {
        Intent i = new Intent(activity, SaveContactNumberActivity.class);
        i.putExtra(PHONE_NUMBER, phoneNumber);
        activity.startActivity(i);
        activity.finish();
    }
}
