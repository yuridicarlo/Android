package it.bancomatpay.sdkui.activities.directdebit;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.DirectDebitHistoryElement;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.DirectDebitMerchantAdapter;
import it.bancomatpay.sdkui.adapter.EmptyAdapter;
import it.bancomatpay.sdkui.databinding.ActivityDirectDebitMerchantListBinding;
import it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager;
import it.bancomatpay.sdkui.model.DateDirectDebitMerchant;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.model.DirectDebit;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;

public class DirectDebitMerchantListActivity extends GenericErrorActivity implements DirectDebitMerchantAdapter.InteractionListener {

    private ActivityDirectDebitMerchantListBinding binding;
    private DirectDebitMerchantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(DirectDebitMerchantListActivity.class.getSimpleName());
        binding = ActivityDirectDebitMerchantListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.recyclerViewDdMerchantList.setLayoutManager(new LinearLayoutManager(this));

        binding.refresh.setOnRefreshListener(this::doRequest);
        binding.refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccentBancomat));
        doRequest();
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doRequest();
    }

    private void doRequest() {
        binding.refresh.setRefreshing(true);
        BancomatSdkInterface.Factory.getInstance().doGetDirectDebitsHistory(this, result -> {
            binding.refresh.setRefreshing(false);
            if (result.getResult() != null) {
                if (result.isSuccess()) {
                    ArrayList<DirectDebitHistoryElement> merchantList = result.getResult().getDirectDebitHistoryElementList();
                    ArrayList<DateDisplayData> directDebits = new ArrayList<>();

                    for (DirectDebitHistoryElement item : merchantList) {
                        directDebits.add(new DateDirectDebitMerchant(item));
                    }

                    if (!merchantList.isEmpty()) {
                        adapter = new DirectDebitMerchantAdapter(directDebits, this);

                        binding.recyclerViewDdMerchantList.setAdapter(adapter);

                        AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewDdMerchantList);

                        showEmptyText(false);
                    } else {
                        showEmptyText(true);
                        EmptyAdapter emptyAdapter = new EmptyAdapter();
                        binding.recyclerViewDdMerchantList.setAdapter(emptyAdapter);
                    }
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                    showEmptyText(true);
                }
            } else {
                showError(result.getStatusCode());
                showEmptyText(true);
            }
        }, SessionManager.getInstance().getSessionToken());
        binding.refresh.setRefreshing(true);
    }

    private void showEmptyText(boolean isShown) {
        if (isShown) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.ddTextListEmpty, AnimationFadeUtil.DEFAULT_DURATION);
            binding.recyclerViewDdMerchantList.setVisibility(View.INVISIBLE);
        } else {
            binding.ddTextListEmpty.setVisibility(View.GONE);
            binding.recyclerViewDdMerchantList.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMerchantElementInteraction(DirectDebit directDebitData) {
        DirectDebitFlowManager.goToDetailDirectDebit(this, directDebitData);
    }
}
