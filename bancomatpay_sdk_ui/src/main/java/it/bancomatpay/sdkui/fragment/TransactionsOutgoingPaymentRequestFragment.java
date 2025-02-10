package it.bancomatpay.sdkui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.TransactionDataOutgoing;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.EmptyAdapter;
import it.bancomatpay.sdkui.adapter.TransactionOutgoingAdapter;
import it.bancomatpay.sdkui.databinding.MovementsOutgoingPaymentRequestFragmentBinding;
import it.bancomatpay.sdkui.events.TransactionOutgoingRefreshEvent;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.model.DateTransactionOutgoing;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class TransactionsOutgoingPaymentRequestFragment extends GenericErrorFragment {

    private MovementsOutgoingPaymentRequestFragmentBinding binding;

    TransactionOutgoingAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MovementsOutgoingPaymentRequestFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        binding.movementList.setLayoutManager(layoutManagerPanel);

        binding.refresh.setOnRefreshListener(this::refreshTransactionsOutgoing);
        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.colorAccentBancomat));

        refreshTransactionsOutgoing();
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

    private void refreshTransactionsOutgoing() {
        BancomatSdkInterface.Factory.getInstance().doGetOutgoingPaymentRequest(requireActivity(), result ->
                        EventBus.getDefault().post(new TransactionOutgoingRefreshEvent(result))
                , SessionManager.getInstance().getSessionToken());
        binding.refresh.setRefreshing(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TransactionOutgoingRefreshEvent event) {

        Result<OutgoingPaymentRequestData> result = (Result<OutgoingPaymentRequestData>) event.getResult();

        binding.refresh.setRefreshing(false);
        if (result != null) {
            if (result.isSuccess()) {
                List<TransactionDataOutgoing> list = result.getResult().getPaymentRequest();
                if (!list.isEmpty()) {
                    showEmptyText(false);
                    ArrayList<DateDisplayData> transactions = new ArrayList<>();
                    for (TransactionDataOutgoing transactionData : list) {
                        transactions.add(new DateTransactionOutgoing(transactionData));
                    }
                    adapter = new TransactionOutgoingAdapter(transactions, (TransactionOutgoingAdapter.InteractionListener) getActivity());
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
