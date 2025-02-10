package it.bancomatpay.sdkui.activities.split_bill.fragments;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.split_bill.SplitBillActivity;
import it.bancomatpay.sdkui.adapter.EmptyAdapter;
import it.bancomatpay.sdkui.adapter.SplitBillHistoryAdapter;
import it.bancomatpay.sdkui.databinding.FragmentHistorySplitBillBinding;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.SplitItemConsumer;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;

public class HistorySplitBillFragment extends GenericErrorFragment implements SplitBillHistoryAdapter.InteractionListener {

    private static final String TAG = HistorySplitBillFragment.class.getSimpleName();

    private FragmentHistorySplitBillBinding binding;

    private SplitBillViewModel splitBillViewModel;

    private SplitBillHistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistorySplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splitBillViewModel = new ViewModelProvider(requireActivity()).get(SplitBillViewModel.class);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> requireActivity().onBackPressed());
        binding.toolbarSimple.setOnClickRightImageListener(v -> NavHelper.navigate(requireActivity(), HistorySplitBillFragmentDirections.actionSplitBillListFragmentToCreateSplitBillFragment()));

        ((SplitBillActivity) requireActivity()).setLightStatusBar(binding.mainLayout, R.color.white_background);

        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.movementList.setLayoutManager(layoutManagerPanel);

        binding.refresh.setOnRefreshListener(this::refreshTransactions);
        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.colorAccentBancomat));

        if(splitBillViewModel.getSplitBillHistory().isEmpty()) {
            refreshTransactions();
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactList();
            }
        }
        else{
            adapter = new SplitBillHistoryAdapter(splitBillViewModel.getSplitBillHistory(), this);
            binding.movementList.setAdapter(adapter);
        }
    }

    private void refreshTransactions() {
      BancomatSdkInterface.Factory.getInstance().doGetSplitBillHistory(requireActivity(), result ->
                handleHistoryResponse(result)
                , SessionManager.getInstance().getSessionToken());
        binding.refresh.setRefreshing(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleHistoryResponse(Result<List<SplitBillHistory>> result){
        binding.refresh.setRefreshing(false);
        if (result != null) {
            if (result.isSuccess()) {
                List<SplitBillHistory> list = result.getResult();

                if (list.isEmpty()) {
                    showEmptyText(true);
                } else {
                    showEmptyText(false);
                    splitBillViewModel.setSplitBillHistory(list);
                    adapter = new SplitBillHistoryAdapter(splitBillViewModel.getSplitBillHistory(), this);
                    binding.movementList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(getActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
            } else {
                showError(result.getStatusCode());

                showEmptyText(true);
                if (binding.movementList.getAdapter() == null) {
                    EmptyAdapter emptyAdapter = new EmptyAdapter();
                    binding.movementList.setAdapter(emptyAdapter);
                }
            }
        }
    }

    private void showEmptyText(boolean isShow) {
        if (isShow) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.movementText, DEFAULT_DURATION);
        } else {
            binding.movementText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListViewInteraction(SplitBillHistory item) {
        Log.d(TAG, "Transaction clicked");
        NavHelper.navigate(requireActivity(), HistorySplitBillFragmentDirections.actionSplitBillListFragmentToHistoryDetailSplitBillFragment(item.getSplitBillUUID()));
    }

    public void contactList() {
        BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(
                requireActivity(), result -> contactsResponseHandler(result),
                true, SessionManager.getInstance().getSessionToken());
        manageListContacts(ApplicationModel.getInstance().getContactItems());
    }

    private void manageListContacts(ArrayList<ContactItem> contactItemArrayList) {
        if (contactItemArrayList != null) {
            ArrayList<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();

            for (ContactItem contactItem : contactItemArrayList) {
                itemsWithSeparator.add(new SplitItemConsumer(contactItem));
            }
            splitBillViewModel.setItemsWithSeparator(itemsWithSeparator);
        }
    }

    public void contactsResponseHandler(Result<SyncPhoneBookData> result) {

        binding.refresh.setRefreshing(false);

        if (result != null) {
            if (result.isSuccess()) {
                if (result.getResult() != null) {
                        manageListContacts(result.getResult().getContactItems());
                }

            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(requireActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
            }
        }
    }
}
