package it.bancomatpay.sdkui.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdk.manager.task.model.TransactionDataOutgoing;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class DateTransactionOutgoing implements DateDisplayData {

    private String dateName;
    private String shortDateName;
    private TransactionOutgoing transaction;

    public DateTransactionOutgoing(TransactionDataOutgoing transaction) {
        this.transaction = new TransactionOutgoing(transaction);
        dateName = StringUtils.getDateStringFormatted(this.transaction.getDisplayDate(), "dd MMMM");
        shortDateName = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(this.transaction.getDisplayDate());
    }

    public TransactionOutgoing getTransaction() {
        return transaction;
    }

    @Override
    public String getDateName() {
        return dateName;
    }

    @Override
    public String getShortDateName() {
        return shortDateName;
    }

}
