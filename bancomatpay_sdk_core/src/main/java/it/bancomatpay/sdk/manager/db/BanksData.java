package it.bancomatpay.sdk.manager.db;

import android.provider.BaseColumns;

import java.io.Serializable;

public class BanksData {

    public static class Model implements Serializable {

        String version;
        String file;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private BanksData() {}

    /* Inner class that defines the table contents */
    public static class EntryV1 implements BaseColumns {
        public static final String TABLE_NAME = "banksData";
        public static final String COLUMN_NAME_JSON_VERSION = "version";
        public static final String COLUMN_NAME_JSON_STRING = "file";
    }
}
