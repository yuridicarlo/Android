package it.bancomatpay.sdkui.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class DateTransaction implements DateDisplayData {

    private String dateName;
    private String shortDateName;
    private Transaction transaction;

    public DateTransaction(TransactionData transaction) {
        this.transaction = new Transaction(transaction);
        dateName = StringUtils.getDateStringFormatted(this.transaction.getDisplayDate(), "dd MMMM");
        shortDateName = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(this.transaction.getDisplayDate());
    }

    public Transaction getTransaction() {
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
