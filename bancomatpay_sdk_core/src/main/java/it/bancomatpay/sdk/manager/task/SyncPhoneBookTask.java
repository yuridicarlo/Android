package it.bancomatpay.sdk.manager.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.db.UserRegistered;
import it.bancomatpay.sdk.manager.network.dto.request.DtoSynchPhoneBookRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSynchPhoneBookResponse;
import it.bancomatpay.sdk.manager.task.interactor.ContactItemInteractor;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

class SyncPhoneBookTask extends ExtendedTask<SyncPhoneBookData> {

    private ArrayList<ContactItem> mContactItems;
    private HashMap<String, List<ContactItem>> contactItemHashMap;

    SyncPhoneBookTask(OnCompleteResultListener<SyncPhoneBookData> mListener, String sessionToken) {
        super(mListener);
        SessionManager.getInstance().setSessionToken(sessionToken);
    }

    @Override
    protected void start() {
        Single.fromCallable(new ContactItemInteractor())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<ArrayList<ContactItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull ArrayList<ContactItem> contactItems) {
                        mContactItems = contactItems;
                        DtoSynchPhoneBookRequest dtoSynchPhoneBookRequest = new DtoSynchPhoneBookRequest();
                        Set<String> phoneNumbers = new HashSet<>();
                        contactItemHashMap = new HashMap<>();
                        for (ContactItem contactItem : mContactItems) {
                            List<ContactItem> list = new ArrayList<>();
                            if (contactItemHashMap.containsKey(contactItem.getMsisdn())) {
                                list = contactItemHashMap.get(contactItem.getMsisdn());
                            }
                            list.add(contactItem);
                            phoneNumbers.add(contactItem.getMsisdn());
                            contactItemHashMap.put(contactItem.getMsisdn(), list);
                        }
                        ArrayList<String> arrayList = new ArrayList<>(phoneNumbers);
                        dtoSynchPhoneBookRequest.setMsisdns(arrayList);

                        //evitiamo doppio aggiornamento lista prima di chiamare la vera SyncPhoneBook
                        //ApplicationModel.getInstance().setContactItems(result);

                        Single.fromCallable(new HandleRequestInteractor<>(DtoSynchPhoneBookResponse.class, dtoSynchPhoneBookRequest, Cmd.SYNCH_PHONE_BOOK, getJsessionClient()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new ObserverSingleCustom<>(l));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<SyncPhoneBookData> r = new Result<>();
                        r.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
                        sendCompletition(r);
                        CustomLogger.e(TAG, "Error on ContactItemInteractor: " + e.getMessage());
                    }
                });


    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoSynchPhoneBookResponse>> l = new NetworkListener<DtoSynchPhoneBookResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoSynchPhoneBookResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<SyncPhoneBookData> r = new Result<>();
            prepareResult(r, response);
            r.setResult(Mapper.getSyncPhoneBookData(mContactItems, true));
            if (r.isSuccess()) {
                final List<UserRegistered.Model> dbList = new ArrayList<>();

                HashSet<String> consumerSet = new HashSet<>();
                //HashSet<String> merchantSet = new HashSet<>();
                //HashSet<String> merchantAndConsumerSet = new HashSet<>();
                HashSet<String> consumerPRSet = new HashSet<>();

//              for (String phoneNumber : response.getRes().getMerchantMsidsns()) {
//                  merchantSet.add(PhoneNumber.getPhoneNumberToString(phoneNumber));
//              }

                for (String phoneNumber : response.getRes().getConsumerMsisdns()) {
                    consumerSet.add(PhoneNumber.getPhoneNumberToString(phoneNumber));
                }

                for (String phoneNumber : response.getRes().getConsumerMsisdnPRs()) {
                    consumerPRSet.add(PhoneNumber.getPhoneNumberToString(phoneNumber));
                }

//              merchantAndConsumerSet.addAll(consumerSet);
//              merchantAndConsumerSet.retainAll(merchantSet);
//
//              merchantSet.removeAll(merchantAndConsumerSet);
//              consumerSet.removeAll(merchantAndConsumerSet);

                HashSet<String> allSet = new HashSet<>();
                allSet.addAll(consumerSet);
//              allSet.addAll(merchantSet);
//              allSet.addAll(merchantAndConsumerSet);
                allSet.addAll(consumerPRSet);

                ArrayList<ContactItem> listBcmContacts = new ArrayList<>();

                for (ContactItem contactItem : mContactItems) {
                    String phoneNumber = contactItem.getPhoneNumber();
                    if (contactItemHashMap.containsKey(phoneNumber)) {
                        UserRegistered.Model userDbModel = new UserRegistered.Model();
                        userDbModel.setPhone(phoneNumber);

                        ContactItem.Type type;
                        /*if (merchantSet.contains(phoneNumber)) {
                            type = ContactItem.Type.MERCHANT;
                            userDbModel.setType(1);
                        } else*/
                        if (consumerSet.contains(phoneNumber)) {
                            if (consumerPRSet.contains(phoneNumber)) {
                                type = ContactItem.Type.CONSUMER_PR;
                                userDbModel.setType(3);
                            } else {
                                type = ContactItem.Type.CONSUMER;
                                userDbModel.setType(0);
                            }
                        } else if (allSet.contains(phoneNumber)) {
                            if (consumerPRSet.contains(phoneNumber)) {
                                type = ContactItem.Type.BOTH_PR;
                                userDbModel.setType(4);
                            } else {
                                type = ContactItem.Type.BOTH;
                                userDbModel.setType(2);
                            }
                        } else {
                            type = ContactItem.Type.NONE;
                        }

                        if (type != ContactItem.Type.NONE) {
                            dbList.add(userDbModel);
                        }

                        contactItem.setType(type);
                        if (!(contactItem.getType() == ContactItem.Type.MERCHANT || contactItem.getType() == ContactItem.Type.NONE)) {
                            listBcmContacts.add(contactItem);
                        }

                    }
                }

                ApplicationModel.getInstance().setContactBcmItems(listBcmContacts);

                new Thread(() -> UserDbHelper.getInstance().saveUserRegistered(dbList)).start();
            }

            sendCompletition(r);
        }

    };

}
