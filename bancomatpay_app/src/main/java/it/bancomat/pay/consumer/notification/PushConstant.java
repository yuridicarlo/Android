package it.bancomat.pay.consumer.notification;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomatpay.consumer.R;

public class PushConstant {

    public static final String EXTRA_PUSH = "EXTRA_PUSH";

    public static final String MAIN_CHANNEL_ID = "main_channel_id";
    public static final String NOTIFICATION_CHANNEL_TITLE =
            BancomatApplication.getAppContext().getString(R.string.notification_channel_main);
    public static final String TYPE_KEY = "TYPE";
    public static final String MESSAGE_KEY = "MESSAGE";
    public static final String PAYMENT_ID_KEY = "PAYMENT_REQUEST_ID";
    public static final String TITLE_KEY = "TITLE";
    public static final String PAYMENT_REQUEST_TYPE = "PAYMENT_REQUEST_TYPE";
}
