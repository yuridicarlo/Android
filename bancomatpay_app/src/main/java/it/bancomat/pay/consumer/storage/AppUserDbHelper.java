package it.bancomat.pay.consumer.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomat.pay.consumer.exception.BanksDataDbException;
import it.bancomat.pay.consumer.network.dto.response.DtoGetBanksConfigurationFileResponse;
import it.bancomat.pay.consumer.utilities.AppApplicationModel;
import it.bancomatpay.sdk.manager.db.BanksData;
import it.bancomatpay.sdk.manager.db.UserCustomerJourneyTag;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.utilities.CjConstants;

public class AppUserDbHelper extends SQLiteOpenHelper {

	private static final String TAG = AppUserDbHelper.class.getSimpleName();

	public static final String SQL_CREATE_BANKS_DATA =
			"CREATE TABLE " + BanksData.EntryV1.TABLE_NAME + " (" +
					BanksData.EntryV1.COLUMN_NAME_JSON_VERSION + " TEXT," +
					BanksData.EntryV1.COLUMN_NAME_JSON_STRING + " TEXT)";

	private static final String SQL_CREATE_CUSTOMER_JOURNEY_TAGS =
			"CREATE TABLE " + UserCustomerJourneyTag.EntryV1.TABLE_NAME + " (" +
					UserCustomerJourneyTag.EntryV1._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP + " TEXT," +
					UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_EXECUTION_ID + " TEXT," +
					UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_KEY + " TEXT," +
					UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_JSON_DATA + " TEXT," +
					UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_CUID_ADVERTISING + " TEXT)";

	private static final String SQL_DELETE_BANKS_DATA =
			"DROP TABLE IF EXISTS " + BanksData.EntryV1.TABLE_NAME;

	private static final String SQL_DELETE_BANKS_DATA_ROWS =
			"DELETE FROM " + BanksData.EntryV1.TABLE_NAME;

	private static final String SQL_DELETE_RECORDS_CUSTOMER_JOURNEY_TAGS =
			"DROP TABLE IF EXISTS " + UserCustomerJourneyTag.EntryV1.TABLE_NAME;

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "AppUser.db";

	private static AppUserDbHelper mInstance;

	public static AppUserDbHelper getInstance() {
		if (mInstance == null) {
			mInstance = new AppUserDbHelper(BancomatApplication.getAppContext());
		}
		return mInstance;
	}

	private AppUserDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_BANKS_DATA);
		db.execSQL(SQL_CREATE_CUSTOMER_JOURNEY_TAGS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_BANKS_DATA);
		db.execSQL(SQL_DELETE_RECORDS_CUSTOMER_JOURNEY_TAGS);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public void createBanksDataTable(DtoGetBanksConfigurationFileResponse response) {
		if(!isTableExists(BanksData.EntryV1.TABLE_NAME)){
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL(SQL_CREATE_BANKS_DATA);
		}
		saveBanksDataFile(response);
	}

	public synchronized boolean saveBanksDataFile(DtoGetBanksConfigurationFileResponse data) {
		try (SQLiteDatabase db = this.getWritableDatabase()) {
			db.execSQL(SQL_DELETE_BANKS_DATA_ROWS);
			ContentValues values = new ContentValues();
			values.put(BanksData.EntryV1.COLUMN_NAME_JSON_VERSION, data.getVersion());
			values.put(BanksData.EntryV1.COLUMN_NAME_JSON_STRING, data.getFile());
			long result = db.insert(BanksData.EntryV1.TABLE_NAME, null, values);
			return (result > 0);
		} catch (Exception e) {
			CustomLogger.e("DbError", e.getMessage());
			return false;
		}
	}

	public boolean isTableExists(String tableName) {
		SQLiteDatabase db = getWritableDatabase();
			if (db == null || !db.isOpen()) {
				db = getReadableDatabase();
			}

		String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'";
		try (Cursor cursor = db.rawQuery(query, null)) {
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					cursor.close();
					return true;
				}
			}
			cursor.close();
			return false;
		}}

	public synchronized BanksData.Model getBanksData() throws BanksDataDbException {

		BanksData.Model banksDataModel = new BanksData.Model();
		try (SQLiteDatabase db = this.getReadableDatabase()) {
			Cursor c = db.query(BanksData.EntryV1.TABLE_NAME, null, null, null, null, null, null);
			if (c.moveToFirst()) {
				do {
				//	banksDataModel.setVersion(c.getString(c.getColumnIndex(BanksData.EntryV1.COLUMN_NAME_JSON_VERSION)));
					banksDataModel.setVersion(c.getString(c.getColumnIndexOrThrow(BanksData.EntryV1.COLUMN_NAME_JSON_VERSION)));
				//	banksDataModel.setFile(c.getString(c.getColumnIndex(BanksData.EntryV1.COLUMN_NAME_JSON_STRING)));
					banksDataModel.setFile(c.getString(c.getColumnIndexOrThrow(BanksData.EntryV1.COLUMN_NAME_JSON_STRING)));
				} while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			CustomLogger.e("DbError", e.getMessage());
			throw new BanksDataDbException(e.getMessage());
		}
		return banksDataModel;
	}

	public synchronized void addCustomerJourneyTag(CustomerJourneyTag customerJourneyTag) {
		int actualRowCount = AppApplicationModel.getInstance().getCustomerJourneySavedTagRowCount();
		CustomLogger.d(TAG, "addCustomerJourneyTag - actualRowCount = " + actualRowCount);
		if (actualRowCount < CjConstants.MAX_SAVED_TAG_ROW_COUNT) {

			SQLiteDatabase db = getWritableDatabase();
			db.beginTransaction();
			try {
				ContentValues values = new ContentValues();
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP, customerJourneyTag.getTagTimestamp());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_EXECUTION_ID, customerJourneyTag.getTagExecutionId());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_KEY, customerJourneyTag.getTagKey());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_JSON_DATA, customerJourneyTag.getTagJsonData());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_CUID_ADVERTISING, customerJourneyTag.getCuid());
				long result = db.insert(UserCustomerJourneyTag.EntryV1.TABLE_NAME, null, values);
				if (result != -1) {
					AppApplicationModel.getInstance().incrementCustomerJourneyRowCount();
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				CustomLogger.e("DbError", e.getMessage());
			} finally {
				db.endTransaction();
			}

		} else {
			updateOldestCustomerJourneyTag(customerJourneyTag);
		}
	}

	public synchronized List<CustomerJourneyTag> getCustomerJourneyTagsList() {
		ArrayList<CustomerJourneyTag> modelArrayList = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		try {
			Cursor c = db.query(UserCustomerJourneyTag.EntryV1.TABLE_NAME, null, null, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					CustomerJourneyTag td = getCustomerJourneyTagModel(c);
					modelArrayList.add(td);
				} while (c.moveToNext());
			}
			c.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			CustomLogger.e("DbError", e.getMessage());
		} finally {
			db.endTransaction();
		}
		return modelArrayList;
	}

	public synchronized void updateOldestCustomerJourneyTag(CustomerJourneyTag newTag) {
		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		try {
			Cursor c = db.query(UserCustomerJourneyTag.EntryV1.TABLE_NAME,
					new String[]{UserCustomerJourneyTag.EntryV1._ID, UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP},
					null, null, null, null,
					UserCustomerJourneyTag.EntryV1._ID + " ASC");

			if (c.moveToFirst()) {
				CustomerJourneyTag oldTag = getCustomerJourneyTagModelRestricted(c);

				String selection = UserCustomerJourneyTag.EntryV1._ID + " = ? and "
						+ UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP + " = ?";
				String[] selectionArgs = {oldTag.getRowId(), oldTag.getTagTimestamp()};

				ContentValues values = new ContentValues();
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP, newTag.getTagTimestamp());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_EXECUTION_ID, newTag.getTagExecutionId());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_KEY, newTag.getTagKey());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_JSON_DATA, newTag.getTagJsonData());
				values.put(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_CUID_ADVERTISING, newTag.getCuid());

				int resultDelete = db.delete(UserCustomerJourneyTag.EntryV1.TABLE_NAME, selection, selectionArgs);
				if (resultDelete == 1) {
					long resultInsert = db.insert(UserCustomerJourneyTag.EntryV1.TABLE_NAME, null, values);

					if (resultInsert != -1) {
						CustomLogger.d(TAG, "updateOldestCustomerJourneyTag - item with _id " + oldTag.getRowId()
								+ " updated with new item with _id " + resultInsert);
					} else {
						CustomLogger.d(TAG, "updateOldestCustomerJourneyTag - item not updated, result == -1");
					}
				}

			}

			c.close();
			db.setTransactionSuccessful();

		} catch (Exception e) {
			CustomLogger.e("DbError", e.getMessage());
		} finally {
			db.endTransaction();
		}
	}


	private CustomerJourneyTag getCustomerJourneyTagModel(Cursor c) {
		CustomerJourneyTag td = new CustomerJourneyTag();
		td.setRowId(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1._ID))));
		td.setTagTimestamp(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP))));
		td.setTagExecutionId(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_EXECUTION_ID))));
		td.setTagKey(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_KEY))));
		td.setTagJsonData(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_JSON_DATA))));
		td.setCuid(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_CUID_ADVERTISING))));
		return td;
	}

	private CustomerJourneyTag getCustomerJourneyTagModelRestricted(Cursor c) {
		CustomerJourneyTag td = new CustomerJourneyTag();
		td.setRowId(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1._ID))));
		td.setTagTimestamp(c.getString((c.getColumnIndexOrThrow(UserCustomerJourneyTag.EntryV1.COLUMN_NAME_TAG_TIMESTAMP))));
		return td;
	}

	public synchronized void deleteCustomerJourneyTags() {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete(UserCustomerJourneyTag.EntryV1.TABLE_NAME, null, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			CustomLogger.e("DbError", e.getMessage());
		} finally {
			db.endTransaction();
			AppApplicationModel.getInstance().setCustomerJourneySavedTagRowCount(0);
		}
	}

}
