package it.bancomatpay.sdk.manager.task.interactor;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.db.UserContact;
import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;

public class ContactItemInteractor implements Callable<ArrayList<ContactItem>> {

	private static String EXTRA_ADDRESS_BOOK_INDEX;
	private static String EXTRA_ADDRESS_BOOK_INDEX_TITLES;
	private static String EXTRA_ADDRESS_BOOK_INDEX_COUNTS;

	protected static String TAG = ContactItemInteractor.class.getSimpleName();

	@Override
	public ArrayList<ContactItem> call() {
		String[] sections = {};
		int[] counts = {};

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI.buildUpon().appendQueryParameter(
				ContactsContract.DIRECTORY_PARAM_KEY, String.valueOf(ContactsContract.Directory.DEFAULT))
				.appendQueryParameter(EXTRA_ADDRESS_BOOK_INDEX, "true")
				.build();

		String orderByMethod;
		if (BancomatDataManager.getInstance().getFlagModel().isOrderContactsPerName()) {
			orderByMethod = ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY;
		} else {
			orderByMethod = ContactsContract.CommonDataKinds.Phone.SORT_KEY_ALTERNATIVE;
		}

		ArrayList<ContactItem> contactItems = new ArrayList<>();

		Cursor cursor = PayCore.getAppContext().getContentResolver().query(uri,
				new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.PHOTO_URI},
				null, null, orderByMethod);

		if (cursor != null) {
			Bundle bundle = cursor.getExtras();
			if (bundle.containsKey(EXTRA_ADDRESS_BOOK_INDEX_COUNTS) &&
					bundle.containsKey(EXTRA_ADDRESS_BOOK_INDEX_TITLES)) {
				sections = bundle.getStringArray(EXTRA_ADDRESS_BOOK_INDEX_TITLES);
				counts = bundle.getIntArray(EXTRA_ADDRESS_BOOK_INDEX_COUNTS);
			}

			CustomLogger.d(TAG, "sections: " + new ArrayList<>(Collections.singletonList(sections)));
			CustomLogger.d(TAG, "counts : " + new ArrayList<>(Collections.singletonList(counts)));


			int indexId = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
			int indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			int indexPhotoUri = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

			int currentSection = 0;

			long id;
			String name;
			Phonenumber.PhoneNumber number;
			String photo;
			String displayNumber;
			ContactItem current;
			HashSet<String> setNumbers = new HashSet<>();
			PhoneNumber.updatePrefix();
			if (cursor.moveToFirst()) {
				HashMap<String, ContactItem> contactsMap = UserDbHelper.getInstance().getUserContactMap();
				//ContactItem existingContact = ApplicationModel.getInstance().getContactItem(displayNumber);

				do {
					id = cursor.getLong(indexId);
					name = cursor.getString(indexName);
					number = PhoneNumber.getPhoneNumber(cursor.getString(indexNumber));
					displayNumber = PhoneNumber.getE164Number(number);
					photo = cursor.getString(indexPhotoUri);
					if (TextUtils.isEmpty(name)) {
						name = ""; //evitiamo un null pointer da gestire
					}
					current = new ContactItem();
					current.setContactId(id);
					current.setName(name);
					current.setMsisdn(displayNumber);
					current.setPhotoUri(photo);
					if (sections != null && currentSection < sections.length) {
						current.setLetter(sections[currentSection]);
					} else {
						current.setLetter("");
					}

					if (PhoneNumber.isValidNumber(number) && PhoneNumber.isValidMobileNumber(displayNumber) && !setNumbers.contains(displayNumber)) {

						current.setDbModel(new UserContact.Model());
						ContactItem existingContact = contactsMap.get(displayNumber);
						if (existingContact != null) {
							current.setDbModel(existingContact.getDbModel());
						}

						contactItems.add(current);
						setNumbers.add(displayNumber);
					}

					if (counts != null && currentSection < counts.length) {
						counts[currentSection]--;
						if (counts[currentSection] == 0) {
							currentSection++;
						}
					}
				} while (cursor.moveToNext());

			}
			cursor.close();

		}

		UserDbHelper.getInstance().saveUserContact(contactItems);

		return contactItems;
	}

	static {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			EXTRA_ADDRESS_BOOK_INDEX = ContactsContract.CommonDataKinds.Phone.EXTRA_ADDRESS_BOOK_INDEX;
			EXTRA_ADDRESS_BOOK_INDEX_TITLES = ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_TITLES;
			EXTRA_ADDRESS_BOOK_INDEX_COUNTS = ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_COUNTS;
		} else {
			Class<?> contactsContractClass = android.provider.ContactsContract.class;
			Class<?>[] classes = contactsContractClass.getDeclaredClasses();
			Class<?> contactCountsClass;

			for (Class<?> aClass : classes) {
				if (aClass.getName().equals("android.provider.ContactsContract$ContactCounts")) {
					contactCountsClass = aClass;

					try {
						EXTRA_ADDRESS_BOOK_INDEX = String.valueOf(contactCountsClass.getDeclaredField("ADDRESS_BOOK_INDEX_EXTRAS").get(contactCountsClass));
					} catch (Exception e) {
						EXTRA_ADDRESS_BOOK_INDEX = "address_book_index_extras";
					}

					try {
						EXTRA_ADDRESS_BOOK_INDEX_TITLES = String.valueOf(contactCountsClass.getDeclaredField("EXTRA_ADDRESS_BOOK_INDEX_TITLES").get(contactCountsClass));
					} catch (Exception e) {
						EXTRA_ADDRESS_BOOK_INDEX_TITLES = "address_book_index_titles";
					}

					try {
						EXTRA_ADDRESS_BOOK_INDEX_COUNTS = String.valueOf(contactCountsClass.getDeclaredField("EXTRA_ADDRESS_BOOK_INDEX_COUNTS").get(contactCountsClass));
					} catch (Exception e) {
						EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "address_book_index_counts";
					}
					break;
				}
			}
		}
	}

}
