package it.bancomatpay.sdk.manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_FREQUENT =
            "CREATE TABLE " + UserFrequent.EntryV1.TABLE_NAME + " (" +
                    UserFrequent.EntryV1._ID + " INTEGER PRIMARY KEY," +
                    UserFrequent.EntryV1.COLUMN_NAME_JSON_OBJECT + " TEXT," +
                    UserFrequent.EntryV1.COLUMN_NAME_TYPE + " INT," +
                    UserFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER + " INT," +
                    UserFrequent.EntryV1.COLUMN_NAME_USER_FREQUENT_ID + " TEXT)";

    private static final String SQL_CREATE_LOYALTY_CARD_FREQUENT =
            "CREATE TABLE " + LoyaltyCardFrequent.EntryV1.TABLE_NAME + " (" +
                    LoyaltyCardFrequent.EntryV1._ID + " INTEGER PRIMARY KEY," +
                    LoyaltyCardFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER + " INT," +
                    LoyaltyCardFrequent.EntryV1.COLUMN_NAME_LOYALTY_CARD_ID + " TEXT)";

    private static final String SQL_CREATE_REGISTERED =
            "CREATE TABLE " + UserRegistered.EntryV1.TABLE_NAME + " (" +
                    UserRegistered.EntryV1._ID + " INTEGER PRIMARY KEY," +
                    UserRegistered.EntryV1.COLUMN_NAME_PHONE + " TEXT," +
                    UserRegistered.EntryV1.COLUMN_NAME_TYPE + " INT)";

    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE " + UserContact.EntryV1.TABLE_NAME + " (" +
                    UserContact.EntryV1._ID + " INTEGER PRIMARY KEY," +
                    UserContact.EntryV1.COLUMN_NAME_LETTER + " TEXT," +
                    UserContact.EntryV1.COLUMN_NAME_DISPLAY_NAME + " TEXT," +
                    UserContact.EntryV1.COLUMN_NAME_PHONE_NUMBER + " TEXT," +
                    UserContact.EntryV1.COLUMN_NAME_PINNING_TIME + " LONG DEFAULT 0," +
                    UserContact.EntryV1.COLUMN_NAME_PHOTO + " TEXT)";

    private static final String SQL_DELETE_LOYALTY_CARD_FREQUENT =
            "DROP TABLE IF EXISTS " + LoyaltyCardFrequent.EntryV1.TABLE_NAME;

    private static final String SQL_DELETE_REGISTERED =
            "DROP TABLE IF EXISTS " + UserRegistered.EntryV1.TABLE_NAME;

    private static final String SQL_DELETE_CONTACTS =
            "DROP TABLE IF EXISTS " + UserContact.EntryV1.TABLE_NAME;

    private static final String SQL_DELETE_FREQUENT =
            "DROP TABLE IF EXISTS " + UserFrequent.EntryV1.TABLE_NAME;

    private static final String SQL_DELETE_RECORDS_REGISTERED =
            "DELETE FROM " + UserRegistered.EntryV1.TABLE_NAME;

    private static final String SQL_DELETE_RECORDS_CONTACTS =
            "DELETE FROM " + UserContact.EntryV1.TABLE_NAME;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "User.db";

    private static UserDbHelper mInstance;

    public static UserDbHelper getInstance() {
        if (mInstance == null) {
            mInstance = new UserDbHelper(PayCore.getAppContext());
        }
        return mInstance;
    }

    private UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FREQUENT);
        db.execSQL(SQL_CREATE_REGISTERED);
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_LOYALTY_CARD_FREQUENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_REGISTERED);
        db.execSQL(SQL_DELETE_FREQUENT);
        db.execSQL(SQL_DELETE_CONTACTS);
        db.execSQL(SQL_DELETE_LOYALTY_CARD_FREQUENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public synchronized void updateUserOperationCounter(UserFrequent.Model userFrequent) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selection = UserFrequent.EntryV1.COLUMN_NAME_USER_FREQUENT_ID + " = ? and " + UserFrequent.EntryV1.COLUMN_NAME_TYPE + " = ?";
            String[] selectionArgs = {userFrequent.getUserFrequentId(), Integer.toString(userFrequent.getType())};

            ContentValues values = new ContentValues();
            values.put(UserFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER, userFrequent.getOperationCounter() + 1);
            values.put(UserFrequent.EntryV1.COLUMN_NAME_JSON_OBJECT, userFrequent.getJsonObject());
            values.put(UserFrequent.EntryV1.COLUMN_NAME_USER_FREQUENT_ID, userFrequent.getUserFrequentId());
            values.put(UserFrequent.EntryV1.COLUMN_NAME_TYPE, userFrequent.getType());

            int count = db.update(
                    UserFrequent.EntryV1.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            if (count == 0) {
                //aggiungere
                db.insert(UserFrequent.EntryV1.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }

    }

    public synchronized void saveUserFrequent(List<UserFrequent.Model> userFrequentList) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selection = UserFrequent.EntryV1.COLUMN_NAME_USER_FREQUENT_ID + " = ? and " + UserFrequent.EntryV1.COLUMN_NAME_TYPE + " = ?";
            for (UserFrequent.Model userRecent : userFrequentList) {
                String[] selectionArgs = {userRecent.getUserFrequentId(), Integer.toString(userRecent.getType())};
                ContentValues values = new ContentValues();
                values.put(UserFrequent.EntryV1.COLUMN_NAME_USER_FREQUENT_ID, userRecent.getUserFrequentId());
                values.put(UserFrequent.EntryV1.COLUMN_NAME_TYPE, userRecent.getType());
                values.put(UserFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER, userRecent.getOperationCounter());

                int count = db.update(
                        UserFrequent.EntryV1.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                if (count == 0) {
                    //aggiungere
                    db.insert(UserFrequent.EntryV1.TABLE_NAME, null, values);
                }

            }
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
    }

    public synchronized void saveUserRegistered(List<UserRegistered.Model> userRegisteredList) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(SQL_DELETE_RECORDS_REGISTERED);
            for (UserRegistered.Model userRegistered : userRegisteredList) {
                ContentValues values = new ContentValues();
                values.put(UserRegistered.EntryV1.COLUMN_NAME_PHONE, userRegistered.getPhone());
                values.put(UserRegistered.EntryV1.COLUMN_NAME_TYPE, userRegistered.getType());
                db.insert(UserRegistered.EntryV1.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
    }

    public synchronized void saveUserContact(List<ContactItem> userContacts) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(SQL_DELETE_RECORDS_CONTACTS);
            for (ContactItem userContact : userContacts) {
                ContentValues values = new ContentValues();
                values.put(UserContact.EntryV1.COLUMN_NAME_LETTER, userContact.getLetter());
                values.put(UserContact.EntryV1.COLUMN_NAME_DISPLAY_NAME, userContact.getName());
                values.put(UserContact.EntryV1.COLUMN_NAME_PHONE_NUMBER, userContact.getMsisdn());
                values.put(UserContact.EntryV1.COLUMN_NAME_PHOTO, userContact.getPhotoUri());
                values.put(UserContact.EntryV1.COLUMN_NAME_PINNING_TIME, userContact.getPinningTime());
                db.insert(UserContact.EntryV1.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
    }

    public synchronized void updateUserPinned(UserContact.Model userContact) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selection = UserContact.EntryV1.COLUMN_NAME_PHONE_NUMBER + " = ? and " + UserContact.EntryV1.COLUMN_NAME_DISPLAY_NAME + " = ?";
            String[] selectionArgs = {userContact.getNumber(), userContact.getDisplayName()};

            ContentValues values = new ContentValues();
            values.put(UserContact.EntryV1.COLUMN_NAME_PHONE_NUMBER, userContact.getNumber());
            values.put(UserContact.EntryV1.COLUMN_NAME_DISPLAY_NAME, userContact.getDisplayName());
            values.put(UserContact.EntryV1.COLUMN_NAME_PINNING_TIME, userContact.getPinningTime());

            int count = db.update(
                    UserContact.EntryV1.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            if (count == 0) {
                //aggiungere
                db.insert(UserContact.EntryV1.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
    }

    public synchronized void updateLoyaltyCardFrequent(LoyaltyCardFrequent.Model cardItem) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selection = LoyaltyCardFrequent.EntryV1.COLUMN_NAME_LOYALTY_CARD_ID + " = ?";
            String[] selectionArgs = {cardItem.getLoyaltyCardId()};

            ContentValues values = new ContentValues();
            values.put(LoyaltyCardFrequent.EntryV1.COLUMN_NAME_LOYALTY_CARD_ID, cardItem.getLoyaltyCardId());
            values.put(LoyaltyCardFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER, cardItem.getOperationCounter());

            int count = db.update(
                    LoyaltyCardFrequent.EntryV1.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            if (count == 0) {
                //aggiungere
                db.insert(LoyaltyCardFrequent.EntryV1.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
    }

    public synchronized List<UserRegistered.Model> getUserRegistered() {

        ArrayList<UserRegistered.Model> modelArrayList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(UserRegistered.EntryV1.TABLE_NAME, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    UserRegistered.Model td = getUserRegisteredModel(c);
                    modelArrayList.add(td);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
        return modelArrayList;
    }

    public synchronized List<UserFrequent.Model> getUserFrequent() {

        ArrayList<UserFrequent.Model> modelArrayList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(UserFrequent.EntryV1.TABLE_NAME, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    UserFrequent.Model td = getUserFrequentModel(c);
                    modelArrayList.add(td);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }

        return modelArrayList;
    }

    public synchronized List<ContactItem> getUserContactList() {

        ArrayList<ContactItem> modelArrayList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(UserContact.EntryV1.TABLE_NAME, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    ContactItem td = getUserContactModel(c);
                    modelArrayList.add(td);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
        return modelArrayList;
    }

    public synchronized HashMap<String, ContactItem> getUserContactMap() {

        HashMap<String, ContactItem> modelHashMap = new HashMap<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(UserContact.EntryV1.TABLE_NAME, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    ContactItem td = getUserContactModel(c);
                    modelHashMap.put(td.getMsisdn(), td);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }
        return modelHashMap;
    }

    public synchronized List<LoyaltyCardFrequent.Model> getLoyaltyCardFrequentList() {

        ArrayList<LoyaltyCardFrequent.Model> modelArrayList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(LoyaltyCardFrequent.EntryV1.TABLE_NAME, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    LoyaltyCardFrequent.Model td = getLoyaltyCardFrequentModel(c);
                    modelArrayList.add(td);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        }

        return modelArrayList;
    }

    private UserFrequent.Model getUserFrequentModel(Cursor c) {
        UserFrequent.Model td = new UserFrequent.Model();
        //td.setLastTransactionTime((c.getLong(c.getColumnIndex(UserFrequent.EntryV1.COLUMN_NAME_LAST_TRANSACTION_TIME))));
        //td.setPinningTime((c.getLong(c.getColumnIndex(UserFrequent.EntryV1.COLUMN_NAME_PINNING_TIME))));
        td.setJsonObject(c.getString((c.getColumnIndex(UserFrequent.EntryV1.COLUMN_NAME_JSON_OBJECT))));
        td.setType(c.getInt((c.getColumnIndex(UserFrequent.EntryV1.COLUMN_NAME_TYPE))));
        td.setOperationCounter(c.getInt((c.getColumnIndex(UserFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER))));
        td.setUserFrequentId(c.getString((c.getColumnIndex(UserFrequent.EntryV1.COLUMN_NAME_USER_FREQUENT_ID))));
        return td;
    }

    private UserRegistered.Model getUserRegisteredModel(Cursor c) {
        UserRegistered.Model td = new UserRegistered.Model();
        td.setPhone(c.getString((c.getColumnIndex(UserRegistered.EntryV1.COLUMN_NAME_PHONE))));
        td.setType((c.getInt(c.getColumnIndex(UserRegistered.EntryV1.COLUMN_NAME_TYPE))));
        return td;
    }

    private ContactItem getUserContactModel(Cursor c) {
        ContactItem td = new ContactItem();
        td.setLetter(c.getString((c.getColumnIndex(UserContact.EntryV1.COLUMN_NAME_LETTER))));
        td.setName((c.getString(c.getColumnIndex(UserContact.EntryV1.COLUMN_NAME_DISPLAY_NAME))));
        td.setMsisdn((c.getString(c.getColumnIndex(UserContact.EntryV1.COLUMN_NAME_PHONE_NUMBER))));
        td.setPhotoUri((c.getString(c.getColumnIndex(UserContact.EntryV1.COLUMN_NAME_PHOTO))));
        td.setPinningTime((c.getLong(c.getColumnIndex(UserContact.EntryV1.COLUMN_NAME_PINNING_TIME))));
        return td;
    }

    private LoyaltyCardFrequent.Model getLoyaltyCardFrequentModel(Cursor c) {
        LoyaltyCardFrequent.Model td = new LoyaltyCardFrequent.Model();
        td.setOperationCounter(c.getInt((c.getColumnIndex(LoyaltyCardFrequent.EntryV1.COLUMN_NAME_OPERATION_COUNTER))));
        td.setLoyaltyCardId(c.getString((c.getColumnIndex(LoyaltyCardFrequent.EntryV1.COLUMN_NAME_LOYALTY_CARD_ID))));
        return td;
    }

    public synchronized void deleteContactsFrequent() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(UserFrequent.EntryV1.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public synchronized void deleteLoyaltyCardFrequent() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(LoyaltyCardFrequent.EntryV1.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            CustomLogger.e("DbError", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

}
