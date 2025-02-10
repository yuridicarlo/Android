package it.bancomatpay.sdk.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public abstract class BaseDataManager extends SQLiteOpenHelper {

    protected static final String TAG = BaseDataManager.class.getSimpleName();

    protected byte[] seed;
    protected SecurityStorageHelperInterface securityHelperInterface;

    protected static final String DATABASE_NAME = "data_sdk";

    protected static final String TABLE_DATA = "table_data";

    protected static final String COLUMN_ID = "id";
    protected static final String COLUMN_KEY = "key";
    protected static final String COLUMN_DATA = "data";

    public BaseDataManager(byte[] seed, SecurityStorageHelperInterface securityHelperInterface, int version) {
        super(PayCore.getAppContext(), DATABASE_NAME, null, version);
        this.seed = seed;
        this.securityHelperInterface = securityHelperInterface;
    }

    public BaseDataManager(int version) {
        this(new byte[]{}, new SecurityHelperDummy(), version);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATA_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY,%s TEXT,%s TEXT)", TABLE_DATA, COLUMN_ID, COLUMN_KEY, COLUMN_DATA);
        db.execSQL(CREATE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Drop all old tables and recreate them
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_DATA));
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Drop all old tables and recreate them
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_DATA));
            onCreate(db);
        }
    }

    public synchronized boolean migrateFromVersion(SQLiteDatabase db, SecurityStorageHelperInterface securityHelperInterface, byte[] seed ){
        Cursor cursor = db.query(TABLE_DATA, null, null, null, null, null, null, null);
        boolean migration = true;
        try {
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(cursor.getColumnIndex(COLUMN_KEY));
                    String value = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));
                    value = securityHelperInterface.decrypt(seed, value);
                    String chiperValue = this.securityHelperInterface.encrypt(this.seed, value);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_KEY, key);
                    values.put(COLUMN_DATA, chiperValue);
                    db.update(TABLE_DATA, values, String.format("%s= ?", COLUMN_KEY), new String[]{key});

                } while(cursor.moveToNext());
            }
        } catch(Exception e) {
            migration = false;
            CustomLogger.e(TAG, e.getMessage());

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return migration;
    }


    public synchronized boolean putString(String key, String value) {

        boolean bRet;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            String chiperValue = securityHelperInterface.encrypt(seed, value);
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY, key);
            values.put(COLUMN_DATA, chiperValue);

            int rows = db.update(TABLE_DATA, values, String.format("%s= ?", COLUMN_KEY), new String[]{key});

            if (rows == 1) {

                db.setTransactionSuccessful();
                bRet = true;

            } else {
                // data with this key did not already exist, so insert new data
                db.insertOrThrow(TABLE_DATA, null, values);
                db.setTransactionSuccessful();
                bRet = true;
            }
        } catch(Exception e) {
            bRet = false;
            CustomLogger.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        return bRet;
    }

    protected synchronized String getString(String key, String defaultValue) {

        String sRet = defaultValue;

        String POSTS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_DATA, COLUMN_KEY);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, new String[]{String.valueOf(key)});
        try {
            if (cursor.moveToFirst()) {
                do {

                    String chiperValue = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));
                    if(!TextUtils.isEmpty(chiperValue)) {
                        sRet = securityHelperInterface.decrypt(seed, chiperValue);
                    }

                } while(cursor.moveToNext());
            }
        } catch(Exception e) {
            CustomLogger.e(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return sRet;

    }

    protected synchronized boolean removeKey(String key){

        boolean bRet;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_DATA, String.format("%s=?", COLUMN_KEY), new String[]{key});
            db.setTransactionSuccessful();

            bRet = true;

        } catch(Exception e) {
            bRet = false;
            CustomLogger.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        return bRet;
    }

    protected synchronized boolean clear() {

        boolean bRet;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_DATA, null, null);
            db.setTransactionSuccessful();

            bRet = true;

        } catch(Exception e) {
            bRet = false;
            CustomLogger.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        return  bRet;
    }

}

