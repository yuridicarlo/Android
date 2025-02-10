package it.bancomatpay.sdkui.viewModel;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ProfileData;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.SplitItemConsumer;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class SplitBillViewModel extends ViewModel {
    private final static String TAG = SplitBillViewModel.class.getSimpleName();

    private int MIN_GROUP_SIZE = 2;
    private int MAX_GROUP_SIZE = Integer.MAX_VALUE;

    private ArrayList<SplitItemConsumer> selectedContactsList;
    private List<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();
    private ArrayList<FrequentItemConsumer> frequentItemList = new ArrayList<>();
    private List<SplitBillHistory> splitBillHistory;
    private String causal;
    private String description;
    private int amountCents;

    private static final String CAUSAL = TAG + "_CAUSAL";
    private static final String DESCRIPTION = TAG + "_DESCRIPTION";
    private static final String AMOUNT_CENTS = TAG + "_AMOUNT_CENTS";
    private static final String SELECTED_CONTACTS = TAG + "_SELECTED_CONTACTS";

    public void initViewModel(ProfileData profileData) {
        reset();

        ContactItem contactItem = new ContactItem();
        if (profileData.getName() != null) {
            contactItem.setName(profileData.getName());
        }
        contactItem.setPhotoUri(profileData.getImage());
        contactItem.setLetter(profileData.getLetter());

        selectedContactsList.add(new SplitItemConsumer(contactItem));
    }

    public void reset(){
        selectedContactsList = new ArrayList<>();
        splitBillHistory = new ArrayList<>();
        causal = null;
        description = null;
        amountCents = 0;
    }

    public void onSaveInstanceState(Bundle outstate){
        CustomLogger.d(TAG, "onSaveInstanceState");
        outstate.putSerializable(CAUSAL, causal);
        outstate.putSerializable(DESCRIPTION, description);
        outstate.putSerializable(AMOUNT_CENTS, amountCents);
        outstate.putSerializable(SELECTED_CONTACTS, selectedContactsList);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            CustomLogger.d(TAG, "restoreSaveInstanceState");
            causal = (String) savedInstanceState.getSerializable(CAUSAL);
            description = (String) savedInstanceState.getSerializable(DESCRIPTION);
            amountCents = (int) savedInstanceState.getSerializable(AMOUNT_CENTS);
            selectedContactsList = (ArrayList<SplitItemConsumer>) savedInstanceState.getSerializable(SELECTED_CONTACTS);
        }
    }

    public void populateHistoryDetail(String splitBillUuid, List<SplitBeneficiary> splitBeneficiaries){
        for (SplitBillHistory historyItem : splitBillHistory){
            if(historyItem.getSplitBillUUID().equals(splitBillUuid)){
                retrieveContactDetails(splitBeneficiaries);
                historyItem.setSplitBeneficiary(splitBeneficiaries);
                return;
            }
        }
    }


    private void retrieveContactDetails(List<SplitBeneficiary> splitBeneficiaries){
        int contactsFound = 0;
        for(ItemInterfaceConsumer itemInterfaceConsumer : itemsWithSeparator){
            if(itemInterfaceConsumer instanceof SplitItemConsumer){
                for(SplitBeneficiary splitBeneficiary : splitBeneficiaries) {
                    if(splitBeneficiary.getBeneficiary().getPhoneNumber().equals(itemInterfaceConsumer.getPhoneNumber())){
                        splitBeneficiary.setBeneficiary((ContactItem) itemInterfaceConsumer.getItemInterface());
                        contactsFound ++;
                        if(contactsFound >= splitBeneficiaries.size()) return;
                    }
                }
            }
        }
    }

    public SplitBillHistory getSplitBillHistory(String splitBillUuid){
        for (SplitBillHistory historyItem : splitBillHistory){
            if(historyItem.getSplitBillUUID().equals(splitBillUuid)){
                return historyItem;
            }
        }
        return null;
    }

    public boolean toggleContact(SplitItemConsumer contactItem) {
        if (!selectedContactsList.contains(contactItem)) {
            selectedContactsList.add(contactItem);
            return true;
        }
        selectedContactsList.remove(contactItem);
        return false;
    }

    public void removeContact(int pos) {
        selectedContactsList.remove(pos);
    }

    public ArrayList<SplitItemConsumer> getSelectedContactsList() {
        return selectedContactsList;
    }

    public ArrayList<SplitItemConsumer> getSelectedContactsListGroup() {
        ArrayList<SplitItemConsumer> filteredList = new ArrayList<>();
        for (int i = 1; i < selectedContactsList.size(); i++) {
            filteredList.add(selectedContactsList.get(i));
        }
        return filteredList;
    }

    public void logList() {
        Gson gson = new Gson();
        Log.v(TAG, (gson.toJson(selectedContactsList)));
    }

    public boolean isGroupSizeValid() {
        return (selectedContactsList.size() > MIN_GROUP_SIZE && selectedContactsList.size() <= MAX_GROUP_SIZE);
    }

    public boolean isButtonEnabled() {
        boolean isValid = true;

        if (amountCents <= selectedContactsList.size()){
            isValid = false;
        }

        if (isValid) {
            computeSingleAmounts();
        }
        return isValid;
    }

    private void computeSingleAmounts() {
        int groupSize = (selectedContactsList.size());
        int rest = amountCents % groupSize;
        int divisibleAmount = amountCents - rest;
        int baseSingleAmount = divisibleAmount / groupSize;
        for (SplitItemConsumer item : selectedContactsList) {
            item.setAmount(String.valueOf(baseSingleAmount));
        }
        selectedContactsList.get(0).setAmount(String.valueOf(baseSingleAmount+rest));
        logList();
    }

    public String getAmountFormatted(){
        return StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(amountCents));
    }

    public boolean showAlert() {
        for (int i = 1; i < selectedContactsList.size(); i++) {
            if(!selectedContactsList.get(i).showBancomatLogo()){
                return true;
            }
        }
        return false;
    }

    public boolean showNoBpayAlert() {
        for (int i = 1; i < selectedContactsList.size(); i++) {
            if(selectedContactsList.get(i).showBancomatLogo()){
                return false;
            }
        }
        return true;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(int amountCents) {
        this.amountCents = amountCents;
    }

    public List<ItemInterfaceConsumer> getItemsWithSeparator() {
        return itemsWithSeparator;
    }

    public void setItemsWithSeparator(List<ItemInterfaceConsumer> itemsWithSeparator) {
        this.itemsWithSeparator = itemsWithSeparator;
    }

    public ArrayList<FrequentItemConsumer> getFrequentItemList() {
        return frequentItemList;
    }

    public void setFrequentItemList(ArrayList<FrequentItemConsumer> frequentItemList) {
        this.frequentItemList = frequentItemList;
    }

    public List<SplitBillHistory> getSplitBillHistory() {
        return splitBillHistory;
    }

    public void setSplitBillHistory(List<SplitBillHistory> splitBillHistory) {
        this.splitBillHistory = splitBillHistory;
    }
}
