package it.bancomatpay.sdk.manager.task.interactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.db.UserFrequent;
import it.bancomatpay.sdk.manager.db.UserRegistered;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;

public class FrequentContactItemInteractor implements Callable<ArrayList<ItemInterface>> {

    @Override
    public ArrayList<ItemInterface> call() {

        List<ContactItem> contactItemsRaw = UserDbHelper.getInstance().getUserContactList();
        HashMap<String, ItemInterface> stringModelHashMap = new HashMap<>();
        for (ItemInterface itemInterface : contactItemsRaw) {
            if (!stringModelHashMap.containsKey(itemInterface.getPhoneNumber())) {
                stringModelHashMap.put(itemInterface.getPhoneNumber(), itemInterface);
            }
        }

        List<UserRegistered.Model> userRegisteredList = UserDbHelper.getInstance().getUserRegistered();
        HashMap<String, UserRegistered.Model> stringModelRegisteredHashMap = new HashMap<>();
        for (UserRegistered.Model userRegistered : userRegisteredList) {
            stringModelRegisteredHashMap.put(userRegistered.getPhone(), userRegistered);
        }

        ArrayList<ItemInterface> frequentItems = new ArrayList<>();
        List<UserFrequent.Model> userFrequentList = UserDbHelper.getInstance().getUserFrequent();
        HashMap<String, UserFrequent.Model> stringModelFrequentHashMap = new HashMap<>();
        for (UserFrequent.Model userFrequent : userFrequentList) {
            stringModelFrequentHashMap.put(userFrequent.getUserFrequentId(), userFrequent);
        }

        if (!stringModelFrequentHashMap.isEmpty()) {
            for (ItemInterface item : contactItemsRaw) {
                ContactItem contactItem = (ContactItem) item;
                if (stringModelFrequentHashMap.containsKey(contactItem.getMsisdn())) {
                    UserRegistered.Model user = stringModelRegisteredHashMap.get(contactItem.getMsisdn());
                    if (user != null) {
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

                    if (!stringModelFrequentHashMap.containsKey(contactItem.getMsisdn())) {

                        UserFrequent.Model userFrequent = new UserFrequent.Model();
                        userFrequent.setOperationCounter(1);
                        userFrequent.setType(user != null ? user.getType() : -1);
                        userFrequent.setUserFrequentId(user != null ? user.getPhone() : contactItem.getMsisdn());
                        userFrequent.setJsonObject(contactItem.getJson());

                        FrequentItem frequentItem = new FrequentItem(contactItem);
                        frequentItem.setDbModel(userFrequent);
                        frequentItem.setOperationCounter(userFrequent.getOperationCounter());
                        frequentItems.add(frequentItem);

                    } else {

                        UserFrequent.Model userFrequent = stringModelFrequentHashMap.get(user != null ? user.getPhone() : contactItem.getMsisdn());
                        FrequentItem frequentItem = new FrequentItem();
                        frequentItem.setItemInterface(contactItem);
                        frequentItem.setDbModel(userFrequent);
                        //frequentItem.setDbModel(userFrequent);
                        //frequentItem.setOperationCounter(userFrequent.getOperationCounter());
                        frequentItems.add(frequentItem);

                    }

                }
            }

        }

        return frequentItems;    }
}
