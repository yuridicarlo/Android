package it.bancomatpay.sdk.manager.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import it.bancomatpay.sdk.manager.db.UserContact;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.UserData;

public class ApplicationModel {

    private static ApplicationModel instance;

    private ArrayList<ContactItem> contactItems;
    private HashMap<String, ContactItem> contactItemHashMap;
    private UserData userData;
    private ArrayList<ShopItem> shopItems;
    private ArrayList<FrequentItem> frequentItems;
    private ArrayList<ContactItem> contactBcmItems;
    private BcmLocation lastLocation;
    private HashMap<DtoDocument.DocumentTypeEnum, Boolean> documentTypeMap;
    private HashMap<String, String> cachedBitmapMap;
    private BankIdContactsData bankIdContactsData;
    private HashMap<String, BankIdAddress> bankIdAddressMap;

    private ApplicationModel() {
    }

    public void resetApplicationModel() {
        contactItems = null;
        contactItemHashMap = null;
        userData = null;
        shopItems = null;
        frequentItems = null;
        contactBcmItems = null;
        lastLocation = null;
        documentTypeMap = null;
        bankIdContactsData = null;
        bankIdAddressMap = null;
        cachedBitmapMap = null;
    }

    public static ApplicationModel getInstance() {
        if (instance == null) {
            instance = new ApplicationModel();
        }
        return instance;
    }

    public ArrayList<ContactItem> getContactItems() {
        return contactItems;
    }

    public void setContactItems(ArrayList<ContactItem> contactItems) {
        this.contactItems = contactItems;
        contactItemHashMap = new HashMap<>();
        for (ContactItem contactItem : contactItems) {
            //in caso di contatti duplicati ma con nome diverso si tiene solo l'ultimo
            contactItemHashMap.put(contactItem.getPhoneNumber(), contactItem);
        }
    }

    public HashMap<String, ContactItem> getContactItemHashMap() {
        return contactItemHashMap;
    }

    public ContactItem getContactItem(String msisdn) {
        if (contactItemHashMap != null) {
            return contactItemHashMap.get(msisdn);
        } else {
            return null;
        }
    }

    public FrequentItem getFrequentItem(String msisdn) {
        FrequentItem foundItem = null;
        if (frequentItems != null) {
            for (FrequentItem item : frequentItems) {
                if (item.getPhoneNumber().equals(msisdn)) {
                    foundItem = item;
                }
            }
        }
        return foundItem;
    }

    public void setContactBcmItems(ArrayList<ContactItem> bcmItems) {
        this.contactBcmItems = bcmItems;
    }

    public ArrayList<ContactItem> getContactBcmItems() {
        return contactBcmItems;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public ArrayList<ShopItem> getShopItems() {
        return shopItems;
    }

    public void setShopItems(ArrayList<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    public BcmLocation getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(BcmLocation lastLocation) {
        this.lastLocation = lastLocation;
    }

    public ArrayList<FrequentItem> getFrequentItems() {
        return frequentItems;
    }

    public void setFrequentItems(ArrayList<FrequentItem> frequentItems) {
        this.frequentItems = frequentItems;
    }

    public void updateUserPinned(UserContact.Model model) {
        ContactItem contactItem = contactItemHashMap.get(model.getNumber());
        if (contactItem != null) {
            contactItem.setPinningTime(model.getPinningTime());
            contactItemHashMap.put(model.getNumber(), contactItem);

            int indexContact = contactItems.indexOf(contactItem);
            if (indexContact != -1) {
                contactItems.set(indexContact, contactItem);
            }

        }
    }

    public void setDocumentTypeMap(HashMap<DtoDocument.DocumentTypeEnum, Boolean> documentTypeMap) {
        this.documentTypeMap = documentTypeMap;
    }

    public HashMap<DtoDocument.DocumentTypeEnum, Boolean> getDocumentTypeMap() {
        return documentTypeMap;
    }

    public BankIdContactsData getBankIdContactsData() {
        return bankIdContactsData;
    }

    public void setBankIdContactsData(BankIdContactsData bankIdContactsData) {
        this.bankIdContactsData = bankIdContactsData;
        if (bankIdContactsData.getBankIdAddresses() != null) {
            if (this.bankIdAddressMap == null) {
                this.bankIdAddressMap = new HashMap<>();
            } else {
                this.bankIdAddressMap.clear();
            }
            for (BankIdAddress item : bankIdContactsData.getBankIdAddresses()) {
                this.bankIdAddressMap.put(item.getAddressId(), item);
            }
        }
    }

    public HashMap<String, BankIdAddress> getBankIdAddressMap() {
        if (this.bankIdAddressMap == null) {
            this.bankIdAddressMap = new HashMap<>();
        }
        return this.bankIdAddressMap;
    }

}
