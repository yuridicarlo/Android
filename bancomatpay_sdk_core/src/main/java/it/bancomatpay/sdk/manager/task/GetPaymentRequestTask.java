package it.bancomatpay.sdk.manager.task;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnCompleteTaskListener;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetPaymentRequestRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetPaymentRequestResponse;
import it.bancomatpay.sdk.manager.task.interactor.CachedContactItemInteractor;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.NotificationPaymentData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class GetPaymentRequestTask extends ExtendedTask<NotificationData> {

    private Result<NotificationData> result;
    private String paymentRequestId;
    private PaymentRequestType paymentRequestType;

    public GetPaymentRequestTask(OnCompleteResultListener<NotificationData> mListener, String paymentRequestId, PaymentRequestType paymentRequestType) {
        super(mListener);
        this.paymentRequestId = paymentRequestId;
        this.paymentRequestType = paymentRequestType;
    }

    public GetPaymentRequestTask(OnCompleteResultListener<NotificationData> mListener) {
        super(mListener);
        this.paymentRequestType = PaymentRequestType.ALL;
    }

    @Override
    protected void start() {
        DtoGetPaymentRequestRequest req = new DtoGetPaymentRequestRequest();
        req.setPaymentRequestId(paymentRequestId);
        req.setPaymentRequestType(paymentRequestType);

        Single.fromCallable(new HandleRequestInteractor<>(DtoGetPaymentRequestResponse.class, req, Cmd.GET_PAYMENT_REQUEST, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(l));
    }

    private OnNetworkCompleteListener<DtoAppResponse<DtoGetPaymentRequestResponse>> l = new NetworkListener<DtoGetPaymentRequestResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetPaymentRequestResponse> response) {
            CustomLogger.d(TAG, response.toString());
            result = new Result<>();
            prepareResult(result, response);
            if (result.isSuccess()) {
                result.setResult(Mapper.getNotificationData(response.getRes()));
                HashMap<String, ContactItem> contactItemHashMap = ApplicationModel.getInstance().getContactItemHashMap();
                if (contactItemHashMap != null) {
                    enrich(contactItemHashMap);
                    sendCompletition(result);
                } else if (ContextCompat.checkSelfPermission(PayCore.getAppContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                    Single.fromCallable(new CachedContactItemInteractor())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new SingleObserver<ArrayList<ContactItem>>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@NonNull ArrayList<ContactItem> contactItems) {
                                    sendCompletition(result);
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Result<NotificationData> result = new Result<>();
                                    result.setStatusCode(StatusCode.Mobile.GENERIC_ERROR);
                                    sendCompletition(result);
                                    CustomLogger.e(TAG, "Error in complete CachedContactItemInteractor: " + e.getMessage());
                                }
                            });

                } else {
                    sendCompletition(result);
                }
            } else {
                sendCompletition(result);
            }
        }

    };

    private OnCompleteTaskListener<ArrayList<ContactItem>> listener = result -> {
        HashMap<String, ContactItem> contactItemHashMap = new HashMap<>();
        for (ItemInterface itemInterface : result) {
            if (!contactItemHashMap.containsKey(itemInterface.getPhoneNumber())) {
                contactItemHashMap.put(itemInterface.getPhoneNumber(), (ContactItem) itemInterface);
            }
        }
        enrich(contactItemHashMap);
        sendCompletition(this.result);
    };

    private void enrich(HashMap<String, ContactItem> contactItemHashMap) {
        for (NotificationPaymentData transactionData : result.getResult().getNotificationPaymentData()) {
            if (transactionData.getItem() instanceof ContactItem) {
                ContactItem contact = (ContactItem) transactionData.getItem();
                String key = PhoneNumber.getE164Number(contact.getMsisdn());
                if (contactItemHashMap.containsKey(key)) {
                    ContactItem contactItem = contactItemHashMap.get(key);
                    if (contactItem != null) {
                        contact.setName(contactItem.getTitle());
                        contact.setPhotoUri(contactItem.getPhotoUri());
                        contact.setLetter(contactItem.getLetter());
                    }
                }
            }
        }
    }

}
