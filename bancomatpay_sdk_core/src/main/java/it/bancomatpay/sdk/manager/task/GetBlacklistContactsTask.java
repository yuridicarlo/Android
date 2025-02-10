package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetBlackListContactsRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetBlackListContactsResponse;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.ExtendedError;
import it.bancomatpay.sdk.manager.utilities.Mapper;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;

public class GetBlacklistContactsTask extends ExtendedTask<ArrayList<ContactItem>> {

    public GetBlacklistContactsTask(OnCompleteResultListener<ArrayList<ContactItem>> mListener) {
        super(mListener);
    }

    @Override
    protected void start() {
        List<String> msisdns = new ArrayList<>();
        ArrayList<ContactItem> listContacts = ApplicationModel.getInstance().getContactBcmItems();
        if (listContacts != null) {
            for (ContactItem contact : listContacts) {
                msisdns.add(contact.getPhoneNumber());
            }
        }

        DtoGetBlackListContactsRequest request = new DtoGetBlackListContactsRequest();
        request.setMsisdns(msisdns);

        if (!msisdns.isEmpty()) {
            Single.fromCallable(new HandleRequestInteractor<>(DtoGetBlackListContactsResponse.class, request, Cmd.GET_BLACKLIST_CONTACTS, getJsessionClient()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ObserverSingleCustom<>(listener));
        } else {
            new SyncPhoneBookTask(result -> {
                if (result != null && result.isSuccess()) {
                    start();
                } else {
                    listener.onCompleteWithError(new ExtendedError(null, GENERIC_ERROR));
                }
            }, SessionManager.getInstance().getSessionToken()).start();
        }

    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetBlackListContactsResponse>> listener = new NetworkListener<DtoGetBlackListContactsResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetBlackListContactsResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<ArrayList<ContactItem>> r = new Result<>();
            prepareResult(r, response);
            if (r.isSuccess()) {
                r.setResult(Mapper.getBlockedContacts(response.getRes()));
            }
            sendCompletition(r);
        }

    };

}
