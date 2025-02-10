package it.bancomatpay.sdkui.activities.split_bill;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.utilities.ExtendedProgressDialogFragment;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;
import it.bancomatpay.sdkui.viewModel.WindowViewModel;


public class SplitBillActivity extends GenericErrorActivity {

    private final static String TAG = SplitBillActivity.class.getSimpleName();

    protected WindowViewModel windowViewModel;

    protected SplitBillViewModel splitPaymentViewModel;

    Boolean isCreateNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_bill);

        windowViewModel = new ViewModelProvider(this).get(WindowViewModel.class);
        splitPaymentViewModel = new ViewModelProvider(this).get(SplitBillViewModel.class);
        splitPaymentViewModel.initViewModel(BancomatSdk.getInstance().getProfileData());
        splitPaymentViewModel.onRestoreInstanceState(savedInstanceState);

        windowViewModel.getLoader().observe(this, integer -> {

            new ExtendedProgressDialogFragment().showNow(getSupportFragmentManager(), "");

        });

        handleExtras(savedInstanceState);
    }

    private void handleExtras(Bundle savedInstanceState) {
        isCreateNew = (Boolean) getIntent().getSerializableExtra(HomeFlowManager.SPLIT_BILL_CREATE_NEW_EXTRA);
        SplitBillHistory historyItem = (SplitBillHistory) getIntent().getSerializableExtra(HomeFlowManager.SPLIT_BILL_DETAIL_EXTRA);
        if(isCreateNew == null) {
            if (savedInstanceState != null) {
                isCreateNew = (Boolean) savedInstanceState.getSerializable(HomeFlowManager.SPLIT_BILL_CREATE_NEW_EXTRA);
            }
        }

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        if(isCreateNew != null) {
            NavHelper.replaceFragment(navController, R.id.createSplitBillFragment);
        } else if(historyItem != null) {
                        List<SplitBillHistory> splitBillHistoryList = new ArrayList<>();
            splitBillHistoryList.add(historyItem);
            splitPaymentViewModel.setSplitBillHistory(splitBillHistoryList);

            Bundle args = new Bundle();
            args.putString("splitBillUUID", historyItem.getSplitBillUUID());
            NavHelper.replaceFragment(navController, R.id.historyDetailSplitBillFragment, args);
        }
    }

    @Override
    public void onBackPressed() {
        if(!NavHelper.popBackStack(this))
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        splitPaymentViewModel.onSaveInstanceState(outState);
        outState.putSerializable(HomeFlowManager.SPLIT_BILL_CREATE_NEW_EXTRA, isCreateNew);
        super.onSaveInstanceState(outState);
    }
}


