package it.bancomatpay.sdk.manager.db;

import android.provider.BaseColumns;

public class UserRegistered {


    public static class Model {

        String phone;
        int type;  //0 consumer 1 merchant 2 entrambi 3 consumer-paymentRequestEnabled

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }

    private UserRegistered() {}

    /* Inner class that defines the table contents */
    public static class EntryV1 implements BaseColumns {
        public static final String TABLE_NAME = "registered";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_TYPE = "type";
    }
}
