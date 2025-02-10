package it.bancomatpay.sdk.manager.utilities;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;

public class PhoneNumber {

    private static String countryCode;
    // prefisso default dell'app

    static {
        countryCode = "IT";
        updatePrefix();
    }

    public static void updatePrefix() {
        if (!TextUtils.isEmpty(BancomatDataManager.getInstance().getDefaultCountryCode())) {
            countryCode = BancomatDataManager.getInstance().getDefaultCountryCode();
        }
    }

    public static String getPhoneNumberToString(String number) {
        return getE164Number(getPhoneNumber(number));
    }

    public static Phonenumber.PhoneNumber getPhoneNumber(String number) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            return phoneUtil.parse(number, countryCode);
        } catch (NumberParseException e) {
            return null; // o number?
        }
    }

    public static String getE164Number(Phonenumber.PhoneNumber number) {
        if (number != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        }
        return null;
    }

    public static boolean isMobileNumber(Phonenumber.PhoneNumber number) {
        if (number != null) {
            if (number.hasItalianLeadingZero()) {
                //e' un fisso
                return false;
            } else {
                String numberString = Long.toString(number.getNationalNumber());
                return numberString.charAt(0) == '3';
            }

        }
        return false;
    }

    public static boolean isValidMobileNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone, countryCode);
            PhoneNumberUtil.PhoneNumberType phoneNumberType = phoneNumberUtil.getNumberType(phoneNumber);
            return phoneNumberType == PhoneNumberUtil.PhoneNumberType.MOBILE;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isValidNumber(Phonenumber.PhoneNumber number) {
        //if(isMobileNumber(number)){ check non più valido
        if (number != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            return phoneUtil.isValidNumber(number);
        }
        return false;
    }

    public static String getE164Number(String number) {
        return getE164Number(getPhoneNumber(number));
    }

    public static boolean isValidNumber(String number) {
        return isValidNumber(getPhoneNumber(number));
    }

}
