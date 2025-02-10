package it.bancomatpay.sdk.manager.db;

import android.provider.BaseColumns;

import java.io.Serializable;

public class UserContact implements Serializable {

    public static class Model implements Serializable {

        String letter;
        String displayName;
        String number;
        String photo;
        long pinningTime;

        public String getLetter() {
            return letter;
        }

        public void setLetter(String letter) {
            this.letter = letter;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public long getPinningTime() {
            return pinningTime;
        }

        public void setPinningTime(long pinningTime) {
            this.pinningTime = pinningTime;
        }

    }

    private UserContact() {}

    /* Inner class that defines the table contents */
    static class EntryV1 implements BaseColumns {
        static final String TABLE_NAME = "contactTable";
        static final String COLUMN_NAME_LETTER = "letter";
        static final String COLUMN_NAME_DISPLAY_NAME = "displayName";
        static final String COLUMN_NAME_PHONE_NUMBER = "number";
        static final String COLUMN_NAME_PHOTO = "photo";
        static final String COLUMN_NAME_PINNING_TIME = "pinningTime";
    }

}
