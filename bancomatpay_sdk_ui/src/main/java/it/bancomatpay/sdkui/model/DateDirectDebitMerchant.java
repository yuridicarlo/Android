package it.bancomatpay.sdkui.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdk.manager.task.model.DirectDebitHistoryElement;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class DateDirectDebitMerchant implements DateDisplayData {

    private String dateName;
    private String shortDateName;
    private DirectDebit directDebit;

    public DateDirectDebitMerchant(DirectDebitHistoryElement directDebitHistoryElement) {
        this.directDebit = new DirectDebit(directDebitHistoryElement);
        dateName = StringUtils.getDateStringFormatted(this.directDebit.getDisplayDate(), "dd MMMM");
        shortDateName = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(this.directDebit.getDisplayDate());
    }

    public DirectDebit getDirectDebit(){
        return directDebit;
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
