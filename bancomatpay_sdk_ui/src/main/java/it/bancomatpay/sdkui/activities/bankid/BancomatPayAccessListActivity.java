package it.bancomatpay.sdkui.activities.bankid;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.BankIdRequest;
import it.bancomatpay.sdk.manager.task.model.BankIdRequestsData;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.activities.SettingsActivity;
import it.bancomatpay.sdkui.adapter.BcmPayAccessesAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmAccessListBinding;
import it.bancomatpay.sdkui.model.DateAccess;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

public class BancomatPayAccessListActivity extends GenericErrorActivity {

	private BcmPayAccessesAdapter adapter;
	private ArrayList<DateDisplayData> accessList;
	private ActivityBcmAccessListBinding binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityBcmAccessListBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setActivityName(SettingsActivity.class.getSimpleName());

		binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		binding.recyclerViewBcmPayAccesses.setLayoutManager(layoutManager);
		accessList = new ArrayList<>();
		adapter = new BcmPayAccessesAdapter(this, accessList);
		binding.recyclerViewBcmPayAccesses.setAdapter(adapter);

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
		BancomatSdkInterface.Factory.getInstance().doGetBankIdRequests(this, result -> {

			binding.refresh.setRefreshing(false);

			if (result != null) {
				if (result.isSuccess()) {

					BankIdRequestsData resultData = result.getResult();

					accessList.clear();
					for (BankIdRequest item : resultData.getBankIdRequestsList()) {
						accessList.add(new DateAccess(item));
					}

					if (!accessList.isEmpty()) {
						boolean isListEmpty = false;
						if (adapter.getItemCount() == 0) {
							isListEmpty = true;
						}
						if (isListEmpty) {
							adapter = new BcmPayAccessesAdapter(this, accessList);
							binding.recyclerViewBcmPayAccesses.setAdapter(adapter);
							AnimationRecyclerViewUtil.runLayoutAnimation(binding.recyclerViewBcmPayAccesses);
						} else {
							adapter.updateList(accessList);
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
			binding.recyclerViewBcmPayAccesses.setVisibility(View.INVISIBLE);
		} else {
			binding.textListEmpty.setVisibility(View.GONE);
			binding.recyclerViewBcmPayAccesses.setVisibility(View.VISIBLE);
		}
	}

}
