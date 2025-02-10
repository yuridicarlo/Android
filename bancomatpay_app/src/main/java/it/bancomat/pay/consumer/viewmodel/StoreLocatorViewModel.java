package it.bancomat.pay.consumer.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.model.ShopCategoryConsumer;

public class StoreLocatorViewModel extends ViewModel {
    private final static String TAG = StoreLocatorViewModel.class.getSimpleName();
    public static final int REQUEST_CHECK_SETTINGS = 999;
    private MutableLiveData<ArrayList<ShopCategoryConsumer>> shopCategories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isGpsResultPositive = new MutableLiveData<>();

    private boolean isPreActivation = false;

    public void onSaveInstanceState(Bundle outstate){
        CustomLogger.d(TAG, "onSaveInstanceState");
    }

    public void restoreSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            CustomLogger.d(TAG, "restoreSaveInstanceState");
        }
    }


    public MutableLiveData<ArrayList<ShopCategoryConsumer>> getShopCategories() {
        return shopCategories;
    }

    public void setShopCategories(ArrayList<ShopCategoryConsumer> shopCategories) {
        this.shopCategories.setValue(shopCategories);
    }

    public MutableLiveData<Boolean> isGpsResultPositive() {
        return isGpsResultPositive;
    }

    public void setIsGpsResultPositive(Boolean isGpsResultPositive) {
        this.isGpsResultPositive.setValue(isGpsResultPositive);
    }

    public ShopCategory getShopCategoryByUuid(String uuid) {
        if(shopCategories.getValue() != null) {
            for(ShopCategoryConsumer category : shopCategories.getValue()) {
                if(category.getShopCategory().getUuid().equals(uuid)) {
                    return category.getShopCategory();
                }
            }
        }
        return null;
    }

    public boolean isReloadRequired() {
        return shopCategories.getValue() == null || shopCategories.getValue().isEmpty();
    }

    public boolean isShowBackButton() {
        return !BancomatPayApiInterface.Factory.getInstance().isUserRegistered();
    }

    public boolean isPreActivation() {
        return isPreActivation;
    }

    public void setPreActivation(boolean preActivation) {
        isPreActivation = preActivation;
    }
}
