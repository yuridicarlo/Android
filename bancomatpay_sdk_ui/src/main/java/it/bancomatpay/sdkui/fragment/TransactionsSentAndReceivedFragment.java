package it.bancomatpay.sdkui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.EmptyAdapter;
import it.bancomatpay.sdkui.adapter.TransactionAdapter;
import it.bancomatpay.sdkui.databinding.MovementsSentAndReceivedFragmentBinding;
import it.bancomatpay.sdkui.events.TransactionRefreshEvent;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.model.DateTransaction;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class TransactionsSentAndReceivedFragment extends GenericErrorFragment {

    private static final String TAG = TransactionsSentAndReceivedFragment.class.getSimpleName();
    private MovementsSentAndReceivedFragmentBinding binding;

    TransactionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MovementsSentAndReceivedFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.movementList.setLayoutManager(layoutManagerPanel);

        binding.refresh.setOnRefreshListener(this::refreshTransactions);
        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.colorAccentBancomat));

        refreshTransactions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void refreshTransactions() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(requireActivity(),
                    result -> CustomLogger.d(TAG, "getSyncPhoneBook end"),
                    true, SessionManager.getInstance().getSessionToken());
        }

        BancomatSdkInterface.Factory.getInstance().doGetPaymentHistory(requireActivity(), result ->
                        EventBus.getDefault().post(new TransactionRefreshEvent(result))
                , null, SessionManager.getInstance().getSessionToken());
        binding.refresh.setRefreshing(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTransactions();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TransactionRefreshEvent event) {

        Result<PaymentHistoryData> result = (Result<PaymentHistoryData>) event.getResult();

        binding.refresh.setRefreshing(false);
        if (result != null) {
            if (result.isSuccess()) {
                ArrayList<TransactionData> list = result.getResult().getTransactionDatas();
                ArrayList<DateDisplayData> transactions = new ArrayList<>();
                for (TransactionData transactionData : list) {
                    transactions.add(new DateTransaction(transactionData));
                }

                if (!list.isEmpty()) {
                    showEmptyText(false);
                    adapter = new TransactionAdapter(transactions, (TransactionAdapter.InteractionListener) getActivity());
                    binding.movementList.setAdapter(adapter);
                    AnimationRecyclerViewUtil.runLayoutAnimation(binding.movementList);
                } else {
                    showEmptyText(true);
                    EmptyAdapter emptyAdapter = new EmptyAdapter();
                    binding.movementList.setAdapter(emptyAdapter);
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

}
