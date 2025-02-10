package it.bancomatpay.sdk.manager.utilities;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public final static BigDecimal ZERO = BigDecimal.ZERO;
    public final static BigDecimal MINUS_ONE = new BigDecimal(-1);
    public final static BigDecimal HUNDRED = new BigDecimal(100);
    public final static BigDecimal ONE_THOUSAND = new BigDecimal(1000);

    public static BigDecimal getBigDecimalFromCents(int cents) {
        BigDecimal value = new BigDecimal(cents);
        return value.divide(HUNDRED);
    }

    @Nullable
    public static BigDecimal getBigDecimalFromCents(String cents) {
        if (TextUtils.isEmpty(cents)) {
            return null;
        } else {
            BigDecimal value = new BigDecimal(cents);
            return value.divide(HUNDRED);
        }
    }

    @Nullable
    static BigDecimal getBigDecimalFromThousandths(String thousandths) {
        if (TextUtils.isEmpty(thousandths)) {
            return null;
        } else {
            BigDecimal value = new BigDecimal(thousandths);
            return value.divide(ONE_THOUSAND);
        }
    }
}
