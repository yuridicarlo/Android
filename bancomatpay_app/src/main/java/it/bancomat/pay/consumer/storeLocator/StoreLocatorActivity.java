package it.bancomat.pay.consumer.storeLocator;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.viewmodel.StoreLocatorViewModel;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.model.ShopCategoryConsumer;
import it.bancomatpay.sdkui.utilities.ExtendedProgressDialogFragment;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;


public class StoreLocatorActivity extends GenericErrorActivity {

    private final static String TAG = StoreLocatorActivity.class.getSimpleName();

    protected WindowViewModel windowViewModel;
    private StoreLocatorViewModel storeLocatorViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_locator);

        windowViewModel = new ViewModelProvider(this).get(WindowViewModel.class);
        storeLocatorViewModel = new ViewModelProvider(this).get(StoreLocatorViewModel.class);
        storeLocatorViewModel.restoreSaveInstanceState(savedInstanceState);
        storeLocatorViewModel.setPreActivation(true);

        windowViewModel.getLoader().observe(this, integer ->  new ExtendedProgressDialogFragment().showNow(getSupportFragmentManager(), ""));

        retrieveStoreCategories();
    }

    public void retrieveStoreCategories() {
        BancomatPayApiInterface.Factory.getInstance().doGetStoreLocatorCategories(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    ArrayList<ShopCategoryConsumer> shopCategories = new ArrayList<>();
                    for(ShopCategory shopCategory :result.getResult()) {
                        shopCategories.add(new ShopCategoryConsumer(shopCategory));
                    }
                    storeLocatorViewModel.setShopCategories(shopCategories);
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, SessionManager.getInstance().getSessionToken());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == StoreLocatorViewModel.REQUEST_CHECK_SETTINGS || requestCode == 0) {
            if(resultCode == -1) {
                storeLocatorViewModel.setIsGpsResultPositive(true);
            } else {
                storeLocatorViewModel.setIsGpsResultPositive(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!NavHelper.popBackStack(this))
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        storeLocatorViewModel.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}


