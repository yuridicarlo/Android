package it.bancomatpay.sdk.manager.task.interactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import it.bancomatpay.sdk.manager.db.UserContact;
import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.db.UserRegistered;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;

public class CachedContactItemInteractor implements Callable<ArrayList<ContactItem>> {

    @Override
    public ArrayList<ContactItem> call() {
        List<ContactItem> contactItemsRaw = UserDbHelper.getInstance().getUserContactList();

        List<UserRegistered.Model> userRegisteredList = UserDbHelper.getInstance().getUserRegistered();
        HashMap<String, UserContact.Model> stringModelHashMap = new HashMap<>();
        HashMap<String, UserRegistered.Model> stringModelRegisteredHashMap = new HashMap<>();
        for (UserRegistered.Model userRegistered : userRegisteredList) {
            stringModelRegisteredHashMap.put(userRegistered.getPhone(), userRegistered);
        }
        for (ContactItem user : contactItemsRaw) {
            UserContact.Model model = new UserContact.Model();
            model.setDisplayName(user.getTitle());
            model.setLetter(user.getLetter());
            model.setNumber(user.getPhoneNumber());
            model.setPhoto(user.getImage());
            model.setPinningTime(user.getPinningTime());
            stringModelHashMap.put(user.getPhoneNumber(), model);
        }
        ArrayList<ContactItem> contactItems = new ArrayList<>();
        for (ItemInterface item : contactItemsRaw) {
            ContactItem contactItem = (ContactItem) item;
            if (stringModelRegisteredHashMap.containsKey(contactItem.getMsisdn())) {
                UserRegistered.Model user = stringModelRegisteredHashMap.get(contactItem.getMsisdn());
                switch (user.getType()) {
                    case 0:
                        contactItem.setType(ContactItem.Type.CONSUMER);
                        break;
                    case 1:
                        contactItem.setType(ContactItem.Type.MERCHANT);
                        break;
                    case 2:
                        contactItem.setType(ContactItem.Type.BOTH);
                        break;
                    case 3:
                        contactItem.setType(ContactItem.Type.CONSUMER_PR);
                        break;
                    case 4:
                        contactItem.setType(ContactItem.Type.BOTH_PR);
                        break;

                }
            }
            contactItem.setDbModel(stringModelHashMap.get(contactItem.getMsisdn()));
            contactItems.add(contactItem);
        }

        return contactItems;
    }
}
