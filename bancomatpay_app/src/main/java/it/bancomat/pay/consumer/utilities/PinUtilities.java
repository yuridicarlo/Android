package it.bancomat.pay.consumer.utilities;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class PinUtilities {

    private static final int MAX_PIN_LENGTH = 5;
    private static final String PIN_REGEX_INCREASING = "^(01234|12345|23456|34567|45678|56789)$";
    private static final String PIN_REGEX_DECREASING = "^(98765|87654|76543|65432|54321|43210)$";
    private static final String PIN_REGEX_EQUALS = "^([0-9])\\1*$";

    public static boolean checkPinFromClient(String pin) {
        return pin != null && !pin.isEmpty() && TextUtils.isDigitsOnly(pin) && pin.length() == MAX_PIN_LENGTH
                && !Pattern.matches(PIN_REGEX_INCREASING, pin)
                && !Pattern.matches(PIN_REGEX_DECREASING, pin) && !Pattern.matches(PIN_REGEX_EQUALS, pin);
    }

}
