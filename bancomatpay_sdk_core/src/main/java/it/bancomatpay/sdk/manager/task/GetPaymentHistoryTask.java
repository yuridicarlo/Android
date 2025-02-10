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
import it.bancomatpay.sdk.manager.network.dto.request.DtoGetPaymentsHistoryRequest;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetPaymentHistoryResponse;
import it.bancomatpay.sdk.manager.task.interactor.CachedContactItemInteractor;
import it.bancomatpay.sdk.manager.task.interactor.HandleRequestInteractor;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.Cmd;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.Mapper;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;

public class GetPaymentHistoryTask extends ExtendedTask<PaymentHistoryData> {

    private Result<PaymentHistoryData> result;
    private final String msisdn;

    public GetPaymentHistoryTask(OnCompleteResultListener<PaymentHistoryData> mListener, String msisdn) {
        super(mListener);
        this.msisdn = msisdn;
    }

    @Override
    protected void start() {
        DtoGetPaymentsHistoryRequest request = new DtoGetPaymentsHistoryRequest();
        request.setMsisdn(this.msisdn);

        Single.fromCallable(new HandleRequestInteractor<>(DtoGetPaymentHistoryResponse.class, request, Cmd.GET_PAYMENTS_HISTORY, getJsessionClient()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ObserverSingleCustom<>(listener));
    }

    private final OnNetworkCompleteListener<DtoAppResponse<DtoGetPaymentHistoryResponse>> listener = new NetworkListener<DtoGetPaymentHistoryResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoGetPaymentHistoryResponse> response) {
            CustomLogger.d(TAG, response.toString());
            result = new Result<>();
            prepareResult(result, response);
            if (result.isSuccess()) {
                PaymentHistoryData paymentHistoryData = new PaymentHistoryData();
                paymentHistoryData.setTransactionDatas(Mapper.getTransactions(response.getRes().getTransactions()));
                result.setResult(paymentHistoryData);
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
                                CustomLogger.d(TAG, "Nothing expected in OnSuccess");
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {

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

    private final OnCompleteTaskListener<ArrayList<ContactItem>> listener2 = result -> {
        HashMap<String, ContactItem> contactItemHashMap = new HashMap<>();
        for (ItemInterface itemInterface : result) {
            if (!contactItemHashMap.containsKey(itemInterface.getPhoneNumber())) {
                contactItemHashMap.put(itemInterface.getPhoneNumber(), (ContactItem) itemInterface);
            }
        }
        enrich(contactItemHashMap);
        sendCompletition(GetPaymentHistoryTask.this.result);
    };

    private void enrich(HashMap<String, ContactItem> contactItemHashMap) {
        for (TransactionData transactionData : result.getResult().getTransactionDatas()) {
            if (transactionData.getTransactionType() == TransactionType.P2P) {
                String key = PhoneNumber.getE164Number(transactionData.getMsisdn());
                if (contactItemHashMap.containsKey(key)) {
                    ContactItem contactItem = contactItemHashMap.get(key);
                    if (contactItem != null) {
                        transactionData.setDisplayName(contactItem.getTitle());
                        transactionData.setImageResource(contactItem.getPhotoUri());
                        transactionData.setLetter(contactItem.getLetter());
                        transactionData.setItemInterface(contactItem);
                    }
                }
            }
        }
    }

}
