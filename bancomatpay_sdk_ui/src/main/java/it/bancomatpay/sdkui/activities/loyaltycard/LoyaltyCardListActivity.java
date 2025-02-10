package it.bancomatpay.sdkui.activities.loyaltycard;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.LoyaltyCardAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmLoyaltyCardListBinding;
import it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.TutorialFlowManager;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_LOYALTY_CARD_ADD;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_LOYALTY_CARD_SELECTED;

public class LoyaltyCardListActivity extends GenericErrorActivity implements LoyaltyCardAdapter.InteractionListener {

    private ActivityBcmLoyaltyCardListBinding binding;

    private LoyaltyCardAdapter adapter;
    private List<LoyaltyCard> cardList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(LoyaltyCardListActivity.class.getSimpleName());
        binding = ActivityBcmLoyaltyCardListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());
        binding.toolbarSimple.setOnClickRightImageListener(v -> {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_LOYALTY_CARD_ADD, null, false);
            LoyaltyCardFlowManager.goToBrandedCardList(this);
        });
        if (Constants.BANK_SERVICE_TUTORIAL_ENABLED) {
            binding.toolbarSimple.setOnClickRightCenterImageListener(v -> TutorialFlowManager.goToTutorialLoyaltyCards(this));
        } else {
            binding.toolbarSimple.setRightCenterImageVisibility(false);
        }

        binding.refresh.setColorSchemeResources(R.color.colorAccentBancomat);

        GridLayoutManager layoutManagerCards = new GridLayoutManager(this, 2);
        binding.recyclerViewFidelityCard.setLayoutManager(layoutManagerCards);

        cardList = new ArrayList<>();
        adapter = new LoyaltyCardAdapter(this, cardList, this);
        binding.recyclerViewFidelityCard.setAdapter(adapter);

        binding.refresh.setOnRefreshListener(this::doRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        doRequest();
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    private void doRequest() {
        binding.refresh.setRefreshing(true);
        BancomatSdkInterface.Factory.getInstance().doGetLoyaltyCards(this, result -> {

            binding.refresh.setRefreshing(false);

            if (result != null) {
                if (result.isSuccess()) {
                    if (result.getResult() != null && result.getResult().getLoyaltyCardList() != null) {

                        boolean isListEmpty = false;
                        if (adapter.getItemCount() == 0) {
                            isListEmpty = true;
                        }

                        cardList = result.getResult().getLoyaltyCardList();

                        if (!cardList.isEmpty()) {
                            if (isListEmpty) {
                                adapter = new LoyaltyCardAdapter(this, cardList, this);
                                binding.recyclerViewFidelityCard.setAdapter(adapter);
                                //if (isListEmpty) {
                                AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewFidelityCard);
                                //}
                            } else {
                                adapter.updateList(cardList);
                            }
                            showEmptyText(false);
                        } else {
                            showEmptyText(true);
                        }
                    }
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                }
            }
        }, SessionManager.getInstance().getSessionToken());
    }

    public void showEmptyText(boolean isEmpty) {
        if (isEmpty) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.fidelityCardLayoutEmpty, DEFAULT_DURATION);
            binding.recyclerViewFidelityCard.setVisibility(View.INVISIBLE);
        } else {
            binding.fidelityCardLayoutEmpty.setVisibility(View.INVISIBLE);
            binding.recyclerViewFidelityCard.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListViewInteraction(LoyaltyCard loyaltyCard) {
        if (!binding.refresh.isRefreshing()) {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_LOYALTY_CARD_SELECTED, null, false);
            LoyaltyCardFlowManager.goToCardDetail(this, loyaltyCard);
        }
    }

}
