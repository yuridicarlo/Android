package it.bancomatpay.sdkui.model;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import it.bancomatpay.sdk.manager.task.model.BankIdRequest;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class DateAccess implements DateDisplayData {

    private String dateName;
    private String shortDateName;
    private BankIdRequest access;

    public DateAccess(BankIdRequest access) {
        this.access = access;
        dateName = StringUtils.getDateStringFormatted(this.access.getRequestDateTime(), "dd MMMM");
        shortDateName = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(this.access.getRequestDateTime());
    }

    public BankIdRequest getBankIdRequest() {
        return access;
    }

    public String getLetter() {
        if (getBankIdRequest() != null
                && getBankIdRequest().getBankIdMerchantData() != null
                && !TextUtils.isEmpty(getBankIdRequest().getBankIdMerchantData().getMerchantName())) {
            return getBankIdRequest().getBankIdMerchantData().getMerchantName().substring(0, 1).toUpperCase();
        }
        return "#";
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
