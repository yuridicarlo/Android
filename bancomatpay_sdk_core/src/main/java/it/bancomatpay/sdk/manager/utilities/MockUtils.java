package it.bancomatpay.sdk.manager.utilities;

import java.util.ArrayList;
import java.util.Arrays;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class MockUtils {

    public static Result<SyncPhoneBookData> fakeSyncPhoneBookDataResponse(){
        SyncPhoneBookData d = new SyncPhoneBookData();
        ArrayList<ContactItem> contactItems = new ArrayList<>();

        ContactItem c1 = new ContactItem();
        c1.setContactId(1);
        c1.setName("Giacomino");
        c1.setMsisdn("41793834315");
        c1.setPhotoUri(null);
        c1.setType(ItemInterface.Type.CONSUMER);
        c1.setLetter("G");
        c1.setBlocked(false);

        ContactItem c2 = new ContactItem();
        c2.setContactId(2);
        c2.setName("PierSilvio");
        c2.setMsisdn("41793834316");
        c2.setPhotoUri(null);
        c2.setType(ItemInterface.Type.CONSUMER);
        c2.setLetter("PS");
        c2.setBlocked(false);

        ContactItem c3 = new ContactItem();
        c3.setContactId(3);
        c3.setName("Marco");
        c3.setMsisdn("41793834317");
        c3.setPhotoUri(null);
        c3.setType(ItemInterface.Type.CONSUMER);
        c3.setLetter("M");
        c3.setBlocked(false);

        ContactItem c4 = new ContactItem();
        c4.setContactId(4);
        c4.setName("Augusto");
        c4.setMsisdn("41793834318");
        c4.setPhotoUri(null);
        c4.setType(ItemInterface.Type.NONE);
        c4.setLetter("A");
        c4.setBlocked(false);

        ContactItem c5 = new ContactItem();
        c5.setContactId(5);
        c5.setName("Carlo");
        c5.setMsisdn("41793834319");
        c5.setPhotoUri(null);
        c5.setType(ItemInterface.Type.CONSUMER_PR);
        c5.setLetter("C");
        c5.setBlocked(false);

        ContactItem c6 = new ContactItem();
        c6.setContactId(6);
        c6.setName("Alberto");
        c6.setMsisdn("41793834310");
        c6.setPhotoUri(null);
        c6.setType(ItemInterface.Type.CONSUMER_PR);
        c6.setLetter("A");
        c6.setBlocked(false);

        contactItems.add(c1);
        contactItems.add(c2);
        contactItems.add(c3);
        contactItems.add(c4);
        contactItems.add(c5);
        contactItems.add(c6);
        d.setContactItems(contactItems);
        d.setContactsSynced(true);


        for(int i = 0; i<100; i++){
            ContactItem c = new ContactItem();
            c.setContactId(i);
            c.setName("Alberto");
            c.setMsisdn("41793834310");
            c.setPhotoUri(null);
            c.setType(ItemInterface.Type.CONSUMER_PR);
            c.setLetter(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J").get(i%10));

            c.setBlocked(false);
            contactItems.add(c);
        }

        Result<SyncPhoneBookData> r = new Result();
        r.setResult(d);
        r.setStatusCode(StatusCode.Mobile.OK);
        return r;
    }
}
