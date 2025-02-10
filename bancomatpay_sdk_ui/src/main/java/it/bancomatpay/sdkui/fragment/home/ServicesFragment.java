package it.bancomatpay.sdkui.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import it.bancomatpay.sdk.manager.events.request.BankServicesUpdateEvent;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankServices;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.HomeServicesDetailAdapter;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.widgets.ToolbarSimple;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_DOCUMENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_LOYALTY_CARD;

public class ServicesFragment extends GenericErrorFragment implements HomeServicesDetailAdapter.ServiceClickListener {

	private HomeServicesDetailAdapter adapterRecyclerServices;
	private final ArrayList<BankServices.EBankService> servicesList = new ArrayList<>();

	private static final int COLUMNS_NUMBER = 2;

	protected ToolbarSimple toolbar;
	View spaceTop;
	RecyclerView recyclerBankServices;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_services, container, false);

		toolbar = view.findViewById(R.id.toolbar_simple);
		spaceTop = view.findViewById(R.id.space_top);
		recyclerBankServices = view.findViewById(R.id.recycler_bank_services);


		toolbar.post(() -> {
			toolbar.setRightImageVisibility(false);
			toolbar.setRightCenterImageVisibility(false);
		});

		recyclerBankServices.setLayoutManager(new GridLayoutManager(requireContext(), COLUMNS_NUMBER));
		adapterRecyclerServices = new HomeServicesDetailAdapter(servicesList, this);
		recyclerBankServices.setAdapter(adapterRecyclerServices);

		initBankServices();

		int insetTop = BancomatDataManager.getInstance().getScreenInsetTop();
		if (insetTop != 0) {
			spaceTop.post(() -> {
				ConstraintLayout.LayoutParams spaceParams = (ConstraintLayout.LayoutParams) spaceTop.getLayoutParams();
				spaceParams.height = insetTop;
				spaceTop.setLayoutParams(spaceParams);
				spaceTop.requestLayout();
			});
		}

		return view;
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

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(BankServicesUpdateEvent event) {
		initBankServices();
		adapterRecyclerServices.notifyDataSetChanged();
	}

	private void initBankServices() {

		BankServices bankServices = BancomatDataManager.getInstance().getBankBankActiveServices();

		servicesList.clear();

		if (bankServices != null) {
			for (BankServices.EBankService service : bankServices.getBankServiceList()) {
				if (service != null && service.isServiceCard()) {
					servicesList.add(BankServices.EBankService.valueOf(service.getServiceName()));
				}
			}
		}

	}

	@Override
	public void onItemClick(BankServices.EBankService service) {
		if (service == BankServices.EBankService.LOYALTY_CARD || service == BankServices.EBankService.LOYALTY) {
			CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_LOYALTY_CARD, null, false);
			HomeFlowManager.goToLoyaltyCards(requireActivity());
		} else if (service == BankServices.EBankService.DOCUMENT) {
			CjUtils.getInstance().sendCustomerJourneyTagEvent(requireContext(), KEY_DOCUMENT, null, false);
			HomeFlowManager.goToDocuments(requireActivity());
		} else if (service == BankServices.EBankService.BANKID) {
			HomeFlowManager.goToBankId(requireActivity());
		} else if (service == BankServices.EBankService.ATM) {
			HomeFlowManager.goToAtmCardless(requireActivity());
		} else if (service == BankServices.EBankService.SPLIT_BILL) {
			HomeFlowManager.goToSplitBill(requireActivity());
		} else if (service == BankServices.EBankService.DIRECT_DEBITS) {
			HomeFlowManager.goToDirectDebits(requireActivity());
		}
	}

}
