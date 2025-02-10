package it.bancomatpay.sdkui.events;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;

public class ContactsRefreshEvent {

    private Result<SyncPhoneBookData> result;
    private boolean isContactsSynched;

    public ContactsRefreshEvent(Result<SyncPhoneBookData> result, boolean isContactSynched) {
        this.result = result;
        this.isContactsSynched = isContactSynched;
    }

    public Result<SyncPhoneBookData> getResult() {
        return result;
    }

    public void setResult(Result<SyncPhoneBookData> result) {
        this.result = result;
    }

    public boolean isContactsSynched() {
        return isContactsSynched;
    }

}
