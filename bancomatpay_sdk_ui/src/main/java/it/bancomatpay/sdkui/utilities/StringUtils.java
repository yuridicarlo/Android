package it.bancomatpay.sdkui.utilities;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class StringUtils {

    private static DecimalFormat mDecimalFormat;

    static {
        mDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.ITALY);
        DecimalFormatSymbols symbols = mDecimalFormat.getDecimalFormatSymbols();
        symbols.setCurrencySymbol(""); // Don't show symbol.
        mDecimalFormat.setDecimalFormatSymbols(symbols);
        mDecimalFormat.setDecimalSeparatorAlwaysShown(true);
        mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);

    }

    public static String getAmountFormatted(long amount, int precision) {
        BigDecimal pow = new BigDecimal(Math.pow(10, precision));
        BigDecimal cents = new BigDecimal(amount);
        return mDecimalFormat.format(cents.divide(pow));
    }

    public static String getFormattedValue(BigDecimal amount) {
        try {
            DecimalFormat mDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.ITALY);
            mDecimalFormat.setDecimalSeparatorAlwaysShown(true);
            mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            return mDecimalFormat.format(amount);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFormattedValue(BigDecimal amount, String currency) {
        try {
            DecimalFormat mDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
            mDecimalFormat.setDecimalSeparatorAlwaysShown(true);
            mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);

            DecimalFormatSymbols symbols = mDecimalFormat.getDecimalFormatSymbols();
            symbols.setCurrencySymbol(currency);
            mDecimalFormat.setDecimalFormatSymbols(symbols);

            return mDecimalFormat.format(amount);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFormattedValueInteger(BigDecimal amount) {
        try {
            DecimalFormat mDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.ITALY);
            mDecimalFormat.setDecimalSeparatorAlwaysShown(true);
            mDecimalFormat.setMaximumFractionDigits(0);
            mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            return mDecimalFormat.format(amount).replaceAll(",", "");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDateLongFormatted(String sDate) {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date date = df.parse(sDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Date d1 = df.parse(sDate);
            Calendar c1 = GregorianCalendar.getInstance(Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            c1.setTime(d1);
            String our = sdf.format(c1.getTime());

            String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
            String month = getMonthUppercaseForInt(calendar.get(Calendar.MONTH));
            String year = String.valueOf(calendar.get(Calendar.YEAR));

            return day + " " + month + " " + year + " - " + our;

        } catch (ParseException e) {
            return "";
        }
    }

    private static String getMonthUppercaseForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month.toUpperCase();
    }

    public static String capitalizeString(String str) {
        String capitalized = str;
        try {
            // Prevent index out of bound exception if the string is null
            capitalized = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }
        catch (Exception e) {

        }

        return capitalized;
    }

    public static boolean contains(String input, String filter) {
        if (TextUtils.isEmpty(filter)) {
            return true;
        }
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        input = input.toUpperCase();
        filter = filter.toUpperCase();
        String[] tokens = filter.split(" ");
        for (String token : tokens) {
            if (!input.contains(token)) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(String[] input, String filter) {
        for (String s : input) {
            if (contains(s, filter)) {
                return true;
            }
        }
        return false;
    }

    public static String getDateStringFormatted(Date date, String pattern) {
        String dateFormatted = new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
        dateFormatted = dateFormatted.substring(0,3) + dateFormatted.substring(3,4).toUpperCase()+dateFormatted.substring(4);
        return dateFormatted;
    }

    public static String getNumberThousandsFormatted(String numberString) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        long number = Long.parseLong(numberString);
        return formatter.format(number);
    }

    public static boolean isNullOrEmpty(String string) {
        if (string == null || string.isEmpty())
            return true;
        return false;
    }

}

