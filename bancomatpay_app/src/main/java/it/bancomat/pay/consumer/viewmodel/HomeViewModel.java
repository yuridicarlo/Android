package it.bancomat.pay.consumer.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;

public class HomeViewModel extends ViewModel {
    private final static String TAG = HomeViewModel.class.getSimpleName();
    private final int MAX_DISPLAYED_TRANSACTIONS = 3;
    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<Result<PaymentHistoryData>> historyData = new MutableLiveData<>();
    private final MutableLiveData<Result<List<SplitBillHistory>>> splitBillData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<FrequentItemConsumer>> frequentItemList = new MutableLiveData<>();

    private boolean contactsInitialized = false;
    private final MutableLiveData<Boolean> bottomSheetExpanded = new MutableLiveData<>(Boolean.FALSE);
    private final MutableLiveData<Boolean> cameraSheetExpanded = new MutableLiveData<>(Boolean.FALSE);

    private final MutableLiveData<Boolean> refreshHomeDataRequired = new MutableLiveData<>(Boolean.TRUE);

    private static final String BOTTOM_SHEET_EXPANDED = TAG + "_BOTTOM_SHEET_EXPANDED";
    private static final String CONTACTS_INITIALIZED = TAG + "_CONTACTS_INITIALIZED";

    public void onSaveInstanceState(Bundle outstate){
        CustomLogger.d(TAG, "onSaveInstanceState");
        outstate.putSerializable(BOTTOM_SHEET_EXPANDED, bottomSheetExpanded.getValue());
        outstate.putSerializable(CONTACTS_INITIALIZED, contactsInitialized);
    }

    public void restoreSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            CustomLogger.d(TAG, "restoreSaveInstanceState");
            Boolean isExpanded = (Boolean) savedInstanceState.getSerializable(BOTTOM_SHEET_EXPANDED);
            bottomSheetExpanded.setValue(isExpanded);
            contactsInitialized = (boolean) savedInstanceState.getSerializable(CONTACTS_INITIALIZED);
        }
    }

    public void setUserData(UserData data) {
        userData.setValue(data);
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }

    public void setHistoryData(Result<PaymentHistoryData> data) {
        if(data != null && data.isSuccess() && data.getResult().getTransactionDatas() != null) {
            List<TransactionData> originalList = data.getResult().getTransactionDatas();
            ArrayList<TransactionData> subList = new ArrayList<>(originalList.subList(0, Math.min(MAX_DISPLAYED_TRANSACTIONS, originalList.size())));
            data.getResult().setTransactionDatas(subList);
        }
        historyData.setValue(data);
    }

    public MutableLiveData<Result<PaymentHistoryData>> getHistoryData() { return historyData; }

    public void setSplitBillHistoryData(Result<List<SplitBillHistory>> data) { splitBillData.setValue(data); }

    public MutableLiveData<Result<List<SplitBillHistory>>> getSplitBillHistoryData() { return splitBillData; }

    public void setFrequentItemListData(ArrayList<FrequentItemConsumer> data) { frequentItemList.setValue(data); }

    public MutableLiveData<ArrayList<FrequentItemConsumer>> getFrequentItemListData() { return frequentItemList; }

    public boolean isContactsInitialized() {
        return contactsInitialized;
    }

    public void setContactsInitialized() {
        this.contactsInitialized = true;
    }

    public MutableLiveData<Boolean> isBottomSheetExpanded() {
        return bottomSheetExpanded;
    }

    public void setBottomSheetExpanded(boolean bottomSheetExpanded) {
        this.bottomSheetExpanded.setValue(new Boolean(bottomSheetExpanded));
    }

    public MutableLiveData<Boolean> isCameraSheetExpanded() {
        return cameraSheetExpanded;
    }

    public void setCameraSheetExpanded(boolean cameraSheetExpanded) {
        this.cameraSheetExpanded.setValue(new Boolean(cameraSheetExpanded));
    }

    public void toggleCameraSheetExpanded() {
        this.cameraSheetExpanded.setValue(Boolean.FALSE.equals(cameraSheetExpanded.getValue()));
    }

    public MutableLiveData<Boolean> isRefreshHomeDataRequired() {
        return refreshHomeDataRequired;
    }

    public void setRefreshHomeDataRequired(boolean refreshRequired) {
        this.refreshHomeDataRequired.setValue(new Boolean(refreshRequired));
    }
}
