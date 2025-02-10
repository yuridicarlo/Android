package it.bancomatpay.sdkui.activities.bankid;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.SettingsActivity;
import it.bancomatpay.sdkui.adapter.BlockedMerchantsAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmBlockedMerchantsBinding;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class BlockedMerchantListActivity extends GenericErrorActivity implements BlockedMerchantsAdapter.InteractionListener {

	private static final String TAG = BlockedMerchantListActivity.class.getSimpleName();
	
	private BlockedMerchantsAdapter adapter;
	private ArrayList<BankIdMerchantData> merchantList;
	private ActivityBcmBlockedMerchantsBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityBcmBlockedMerchantsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setActivityName(SettingsActivity.class.getSimpleName());

		binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		binding.recyclerViewBlockedMerchants.setLayoutManager(layoutManager);

		merchantList = new ArrayList<>();
		adapter = new BlockedMerchantsAdapter(merchantList, this);
		binding.recyclerViewBlockedMerchants.setAdapter(adapter);

		binding.refresh.setColorSchemeResources(R.color.colorAccentBancomat);
		binding.refresh.setOnRefreshListener(this::doRequest);

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
		BancomatSdkInterface.Factory.getInstance().doGetBankIdBlacklist(this, result -> {

			binding.refresh.setRefreshing(false);

			if (result != null) {
				if (result.isSuccess()) {

					merchantList = result.getResult();

					if (!merchantList.isEmpty()) {
						boolean isListEmpty = false;
						if (adapter.getItemCount() == 0) {
							isListEmpty = true;
						}
						if (isListEmpty) {
							adapter = new BlockedMerchantsAdapter(merchantList, this);
							binding.recyclerViewBlockedMerchants.setAdapter(adapter);
							AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewBlockedMerchants);
						} else {
							adapter.updateList(merchantList);
						}

						showEmptyText(false);

					} else {
						showEmptyText(true);
					}

				} else if (result.isSessionExpired()) {
					BCMAbortCallback.getInstance().getAuthenticationListener()
							.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
				} else {
					showError(result.getStatusCode());
					if (adapter.getItemCount() == 0) {
						showEmptyText(true);
					}
				}
			} else {
				if (adapter.getItemCount() == 0) {
					showEmptyText(true);
				}
			}
		}, SessionManager.getInstance().getSessionToken());
	}

	private void showEmptyText(boolean isEmpty) {
		if (isEmpty) {
			AnimationFadeUtil.startFadeInAnimationV1(binding.textListEmpty, DEFAULT_DURATION);
			binding.recyclerViewBlockedMerchants.setVisibility(View.INVISIBLE);
		} else {
			binding.textListEmpty.setVisibility(View.GONE);
			binding.recyclerViewBlockedMerchants.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onListInteraction(BankIdMerchantData blockedMerchant) {

		LoaderHelper.showLoader(this);
		BancomatSdkInterface.Factory.getInstance().doDeleteBankIdBlacklist(this, result -> {
					if (result != null) {
						if (result.isSuccess()) {

							CustomLogger.d(TAG, "Merchant with tag = " + blockedMerchant.getMerchantTag()
									+ " and name = " + blockedMerchant.getMerchantName() + " successfully unlocked!");
							adapter.removeItem(blockedMerchant);
							showEmptyText(adapter.getItemCount() == 0);

						} else if (result.isSessionExpired()) {
							BCMAbortCallback.getInstance().getAuthenticationListener()
									.onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
						} else {
							showError(result.getStatusCode());
						}
					}
				},
				blockedMerchant.getMerchantTag(),
				blockedMerchant.getMerchantName(),
				SessionManager.getInstance().getSessionToken());

	}

}
