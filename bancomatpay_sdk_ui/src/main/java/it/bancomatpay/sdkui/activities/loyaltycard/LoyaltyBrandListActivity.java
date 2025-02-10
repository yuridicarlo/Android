package it.bancomatpay.sdkui.activities.loyaltycard;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.LoyaltyBrand;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.adapter.LoyaltyBrandListAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmBrandedCardListBinding;
import it.bancomatpay.sdkui.events.ReturnLoyaltyCardListEvent;
import it.bancomatpay.sdkui.flowmanager.LoyaltyCardFlowManager;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;

public class LoyaltyBrandListActivity extends GenericErrorActivity implements LoyaltyBrandListAdapter.InteractionListener {

	private ActivityBcmBrandedCardListBinding binding;

	private LoyaltyBrandListAdapter adapter;
	private List<LoyaltyBrand> brandList;

	ActivityResultLauncher<Intent> activityResultLauncherCaptureCardBarcode = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
			});

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActivityName(LoyaltyBrandListActivity.class.getSimpleName());
		binding = ActivityBcmBrandedCardListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		//Prevent keyboard open
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

		binding.refresh.setColorSchemeResources(R.color.colorAccentBancomat);

        LinearLayoutManager layoutManagerCards = new LinearLayoutManager(this);
		binding.recyclerViewBrandedCard.setLayoutManager(layoutManagerCards);

        brandList = new ArrayList<>();
		adapter = new LoyaltyBrandListAdapter(this, brandList, this);
		binding.recyclerViewBrandedCard.setAdapter(adapter);

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable text) {

				if (text.length() > 0) {
					if (binding.cancelButtonSearch.getVisibility() != View.VISIBLE) {
						AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, 250);
					}
					adapter.getFilter().filter(text);
				} else {
					AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
					adapter.getFilter().filter("");
				}

			}
		};
		binding.activationSearchBankEditText.addTextChangedListener(textWatcher);

		binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
			binding.activationSearchBankEditText.getText().clear();
			AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
		}));

		doRequest();
		binding.refresh.setOnRefreshListener(this::doRequest);

	}

	@Override
	protected void onDestroy() {
		BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
		super.onDestroy();
	}

	@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
	public void onMessageEvent(ReturnLoyaltyCardListEvent event) {
		EventBus.getDefault().removeStickyEvent(event);
		finish();
	}

	private void doRequest() {
		binding.refresh.setRefreshing(true);
		BancomatSdkInterface.Factory.getInstance().doGetLoyaltyCardBrands(this, result -> {

			binding.refresh.setRefreshing(false);

			if (result != null) {
				if (result.isSuccess()) {
					if (result.getResult() != null && result.getResult().getBrandList() != null) {

						boolean isListEmpty = false;
						if (adapter.getItemCount() == 0) {
							isListEmpty = true;
						}

						brandList = result.getResult().getBrandList();

						if (isListEmpty) {
							adapter = new LoyaltyBrandListAdapter(this, brandList, this);
							binding.recyclerViewBrandedCard.setAdapter(adapter);
							AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewBrandedCard);
						} else {
							runOnUiThread(() -> adapter.updateList(brandList));
						}
						if (adapter.getItemCount() == 0) {
							adapter.addOtherCardItem();
						}
					}
				} else if (result.isSessionExpired()) {
					BCMAbortCallback.getInstance().getAuthenticationListener()
							.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
				} else {
					showError(result.getStatusCode());
					if (adapter.getItemCount() == 0) {
						adapter.addOtherCardItem();
					}
				}
			} else {
				if (adapter.getItemCount() == 0) {
					adapter.addOtherCardItem();
				}
			}
		}, SessionManager.getInstance().getSessionToken());
	}

	@Override
	public void onListViewInteraction(LoyaltyBrand loyaltyBrand) {
		LoyaltyCardFlowManager.goToCaptureCardBarcode(this, loyaltyBrand, false, activityResultLauncherCaptureCardBarcode);
	}

	@Override
	public void onAddCardClicked() {
		LoyaltyCardFlowManager.goToCaptureCardBarcode(this);
	}

}
