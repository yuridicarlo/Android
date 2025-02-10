package it.bancomatpay.sdkui.activities.split_bill.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.split_bill.SplitBillActivity;
import it.bancomatpay.sdkui.adapter.SplitBillContactsRecapAdapter;
import it.bancomatpay.sdkui.databinding.FragmentHistoryDetailSplitBillBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.MapperConsumer;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;

public class HistoryDetailSplitBillFragment extends GenericErrorFragment{

    private static final String TAG = HistoryDetailSplitBillFragment.class.getSimpleName();

    private FragmentHistoryDetailSplitBillBinding binding;

    private SplitBillViewModel splitBillViewModel;

    private String splitBillUUID;

    private SplitBillHistory historyItem;

    private SplitBillContactsRecapAdapter adapter;

    private String SPLIT_BILL_UUID = "splitBillUUID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryDetailSplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splitBillViewModel = new ViewModelProvider(requireActivity()).get(SplitBillViewModel.class);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> requireActivity().onBackPressed());
        binding.toolbarSimple.setOnClickRightImageListener(v -> shareAction());

        ((SplitBillActivity) requireActivity()).clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

        Bundle args = getArguments();
        if (args != null) {
            splitBillUUID = args.getString(SPLIT_BILL_UUID);
            Log.d(TAG, "args received: " + splitBillUUID);
        } else {
            splitBillUUID = (String) savedInstanceState.getSerializable(SPLIT_BILL_UUID);
        }

        historyItem = splitBillViewModel.getSplitBillHistory(splitBillUUID);

        if(historyItem != null) {
            binding.causal.setText(historyItem.getCausal());
            binding.description.setText(historyItem.getDescription());

            if(historyItem.getSplitBeneficiary() == null) {
                retrieveDetails();
            } else{
                configureRecyclerView();
            }
        }

        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.colorAccentBancomat));

        binding.refresh.setOnRefreshListener(this::retrieveDetails);
    }

    private void configureRecyclerView(){
        binding.refresh.setRefreshing(false);
        adapter = new SplitBillContactsRecapAdapter(null, MapperConsumer.splitItemConsumerListFromSplitBeneficiaryList(historyItem.getSplitBeneficiary()));
        binding.groupContactsRV.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        binding.groupContactsRV.setLayoutManager(llm);
    }

    private void retrieveDetails(){
        binding.refresh.setRefreshing(true);
        BancomatSdkInterface.Factory.getInstance().doGetSplitBillHistoryDetail(requireActivity(),
                this::handleHistoryDetailResponse,
                splitBillUUID, SessionManager.getInstance().getSessionToken());

    }

    private void handleHistoryDetailResponse(Result<List<SplitBeneficiary>> result) {
        if (result != null) {
            if (result.isSuccess()) {
                if(result.getResult() != null){
                    splitBillViewModel.populateHistoryDetail(splitBillUUID, result.getResult());
                    historyItem = splitBillViewModel.getSplitBillHistory(splitBillUUID);
                    configureRecyclerView();
                }
            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(getActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
            } else {
                showError(result.getStatusCode());
            }
        }
    }

    private void shareAction() {
        String message = getString(R.string.split_bill_success_share);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getResources().getString(R.string.share));
        startActivity(shareIntent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SPLIT_BILL_UUID, splitBillUUID);
    }
}