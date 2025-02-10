package it.bancomat.pay.consumer.home;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import it.bancomatpay.consumer.R;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdkui.databinding.ExchangeMoneyBottomSheetDialogBinding;
import it.bancomatpay.sdkui.databinding.FragmentHomeRestyleBinding;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;


import androidx.core.app.ActivityCompat;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.activation.databank.DataBankManager;
import it.bancomat.pay.consumer.extended.activities.HomeActivityExtended;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.viewmodel.HomeViewModel;
import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.DateDisplayData;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.adapter.EmptyAdapter;
import it.bancomatpay.sdkui.adapter.HomeFrequentContactsAdapter;
import it.bancomatpay.sdkui.adapter.HomeRecentSplitBillsAdapter;
import it.bancomatpay.sdkui.adapter.HomeTransactionAdapter;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.flowmanager.TransactionsFlowManager;
import it.bancomatpay.sdkui.model.DateTransaction;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;


import it.bancomatpay.sdkui.model.Transaction;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.LogoBankSingleton;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class HomeFragmentRestyle extends GenericErrorFragment implements HomeTransactionAdapter.InteractionListener, HomeFrequentContactsAdapter.FrequentItemInteractionListener, HomeRecentSplitBillsAdapter.SplitBillHistoryItemInteractionListener {
    private static final String TAG = HomeFragmentRestyle.class.getSimpleName();

    private HomeViewModel homeViewModel;
    private FragmentHomeRestyleBinding binding;
    private ExchangeMoneyBottomSheetDialogBinding bottomSheetBinding;
    private BottomSheetBehavior sheetBehavior;
    private CoordinatorLayout bottomMenulayoutContainer;
    private boolean isPlafondVisible;
    private boolean isFirstBalanceLoad = true;
    private InteractionListener listener;

    private Thread threadLogo;

    private boolean oldState;

    HomeRecentSplitBillsAdapter recentSplitBillAdapter;
    HomeFrequentContactsAdapter frequentContactsAdapter;
    HomeTransactionAdapter transactionAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeRestyleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        bottomSheetBinding = ExchangeMoneyBottomSheetDialogBinding.bind(view.findViewById(R.id.exchange_money_bottom_sheet_dialog));
        bottomMenulayoutContainer = view.findViewById(R.id.bottomMenulayoutContainer);
        sheetBehavior = BottomSheetBehavior.from(bottomMenulayoutContainer);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    homeViewModel.setBottomSheetExpanded(true);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    homeViewModel.setBottomSheetExpanded(false);
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {

                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetBinding.arrowExpand.setRotation(slideOffset * 180);
            }
        });

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        oldState = Boolean.TRUE.equals(homeViewModel.isBottomSheetExpanded().getValue());
        if(oldState) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.sendReceiveLayoutShadow.setVisibility(View.VISIBLE);
            binding.headerMargin.setVisibility(View.GONE);
            binding.logoBancomat.setVisibility(View.VISIBLE);
            bottomSheetBinding.arrowExpand.setRotation(180);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.sendReceiveLayoutShadow.setVisibility(View.INVISIBLE);
            binding.headerMargin.setVisibility(View.VISIBLE);
            binding.logoBancomat.setVisibility(View.GONE);
        }

        binding.imageEye.setOnClickListener(new CustomOnClickListener(v -> {
            isPlafondVisible = !isPlafondVisible;
            FullStackSdkDataManager.getInstance().putShowBalance(isPlafondVisible);
            setPlafondText(isPlafondVisible);
        }));
        listener = (HomeActivityExtended) requireActivity();
        binding.notificationButton.setOnClickListener(new CustomOnClickListener(v -> HomeFlowManager.goToNotifications(requireActivity())));
        binding.profileButton.setOnClickListener(new CustomOnClickListener(v -> {
            if (listener != null) {
                listener.onProfileClick();
            }
        }));


        bottomSheetBinding.arrowExpandLayout.setOnClickListener(v -> {
            if(Boolean.TRUE.equals(homeViewModel.isBottomSheetExpanded().getValue())){
                homeViewModel.setBottomSheetExpanded(false);
            } else {
                homeViewModel.setBottomSheetExpanded(true);
            }});

        homeViewModel.isCameraSheetExpanded().observe(requireActivity(), isExpanded ->{
            if(!oldState) {
                if (isExpanded) {
                    AnimationFadeUtil.startFadeInAnimationV2(binding.sendReceiveLayoutShadow, 250);
                    binding.headerMargin.setVisibility(View.GONE);
                    AnimationFadeUtil.startFadeInAnimationV2(binding.logoBancomat, 250);
                } else{
                    AnimationFadeUtil.startFadeOutAnimationV2(binding.sendReceiveLayoutShadow, 250, View.INVISIBLE);
                    binding.headerMargin.setVisibility(View.VISIBLE);
                    AnimationFadeUtil.startFadeOutAnimationV2(binding.logoBancomat, 250, View.GONE);
                }
            }
        });

        homeViewModel.isBottomSheetExpanded().observe(requireActivity(), isExpanded ->{
            if (oldState != isExpanded) {
                if (isExpanded) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    AnimationFadeUtil.startFadeInAnimationV2(binding.sendReceiveLayoutShadow, 250);
                    binding.headerMargin.setVisibility(View.GONE);
                    AnimationFadeUtil.startFadeInAnimationV2(binding.logoBancomat, 250);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    AnimationFadeUtil.startFadeOutAnimationV2(binding.sendReceiveLayoutShadow, 250, View.INVISIBLE);
                    binding.headerMargin.setVisibility(View.VISIBLE);
                    AnimationFadeUtil.startFadeOutAnimationV2(binding.logoBancomat, 250, View.GONE);
                }

                oldState = isExpanded;
            }
        });

        binding.sendMoneyButton.setOnClickListener(v -> {
            HomeFlowManager.goToContacts(requireActivity(), PaymentContactFlowType.SEND);
        });

        binding.requestMoneyButton.setOnClickListener(v -> {
            HomeFlowManager.goToContacts(requireActivity(), PaymentContactFlowType.REQUEST);

        });

        String bankUuid = BancomatPayApiInterface.Factory.getInstance().getBankUuidChoosed();
        if (!TextUtils.isEmpty(bankUuid)) {
            DataBank dataBank = DataBankManager.getDataBank(bankUuid);
            if (dataBank != null) {

                if (LogoBankSingleton.getInstance().getLogoBank() == null) {
                    threadLogo = new Thread(() -> {
                        try {
                            Bitmap bitmapLogoSearch = Picasso.get().load(dataBank.getLogoSearch())
                                    .placeholder(R.drawable.empty)
                                    .networkPolicy(NetworkPolicy.NO_STORE)
                                    .memoryPolicy(MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_STORE)
                                    .get();


                            Drawable drawableLogoSearch = new BitmapDrawable(PayCore.getAppContext().getResources(), bitmapLogoSearch);
                            LogoBankSingleton.getInstance().setLogoBank(drawableLogoSearch);
                            CustomLogger.d(TAG, "Bank logo loaded successfully");

                        } catch (IOException e) {
                            CustomLogger.e(TAG, "Bank logo not loaded: " + e.getMessage());
                        }
                    });
                    threadLogo.start();
                }

                if (!TextUtils.isEmpty(dataBank.getLogoHome())) {
                    Picasso.get().load(dataBank.getLogoHome())
                            .placeholder(R.drawable.empty)
                            .networkPolicy(NetworkPolicy.NO_STORE)
                            .memoryPolicy(MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_STORE)
                            .into(binding.logoBank, null);
                }
            }
        }

        homeViewModel.getUserData().observe(requireActivity(), userData -> {
            manageUserData(userData);
        });

        if (homeViewModel.getUserData().getValue() != null) {
            manageUserData(homeViewModel.getUserData().getValue());
        }

        bottomSheetBinding.sendMoneyPlusButton.setOnClickListener((v -> {
            HomeFlowManager.goToContacts(requireActivity(), PaymentContactFlowType.SEND);
        }));
        bottomSheetBinding.splitBillPlusButton.setOnClickListener((v -> {
            HomeFlowManager.goToNewSplitBill(requireActivity());
        }));
        bottomSheetBinding.showAllMovementsButton.setOnClickListener((v -> {
            HomeFlowManager.goToTransactions(requireActivity());
        }));

        setUpRecentContactsAdapter();
        setUpRecentSplitBillsAdapter();
        setUpLastTransactionsAdapter();

        homeViewModel.getFrequentItemListData().observe(requireActivity(), frequentItemConsumers -> {
            manageFrequentItemConsumersData(frequentItemConsumers);
        });
        homeViewModel.getSplitBillHistoryData().observe(requireActivity(), splitBillHistoryData -> {
            manageSplitBillHistoryData(splitBillHistoryData);
        });
        homeViewModel.getHistoryData().observe(requireActivity(), paymentHistoryDataResult -> {
            manageTransactionHistoryData(paymentHistoryDataResult);
        });


    }

    private void setUpRecentContactsAdapter() {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        layoutManagerPanel.setOrientation(LinearLayoutManager.HORIZONTAL);
        bottomSheetBinding.lastContactsRv.setLayoutManager(layoutManagerPanel);
    }

    private void setUpRecentSplitBillsAdapter() {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        layoutManagerPanel.setOrientation(LinearLayoutManager.HORIZONTAL);
        bottomSheetBinding.lastSplitBillsRv.setLayoutManager(layoutManagerPanel);
    }

    private void setUpLastTransactionsAdapter() {
        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(getActivity());
        bottomSheetBinding.movementList.setLayoutManager(layoutManagerPanel);
    }

    private void refreshData() {
        setContactsLoading(true);
        setSplitBillLoading(true);
        setMovementsLoading(true);
        if (!homeViewModel.isContactsInitialized()) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(requireActivity(),
                        contactsResult -> {
                            if (contactsResult != null) {
                                if (contactsResult.isSuccess()) {
                                    homeViewModel.setContactsInitialized();
                                    retrieveTransactions();
                                    retrieveFrequentContactItems();
                                    retrieveSplitBillHistory();
                                } else if (contactsResult.isSessionExpired()) {
                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                            .onAbortSession(getActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
                                } else {
                                    showError(contactsResult.getStatusCode());
                                    retrieveTransactions();
                                    retrieveFrequentContactItems();
                                    retrieveSplitBillHistory();
                                }
                            }
                        },
                        true, SessionManager.getInstance().getSessionToken());
            } else {
                retrieveTransactions();
                retrieveFrequentContactItems();
                retrieveSplitBillHistory();
            }
        } else {
            retrieveTransactions();
            retrieveFrequentContactItems();
            retrieveSplitBillHistory();
        }
    }

    private void retrieveTransactions() {
        BancomatSdkInterface.Factory.getInstance().doGetPaymentHistory(requireActivity(), result ->
                        homeViewModel.setHistoryData(result)
                , null, SessionManager.getInstance().getSessionToken());
    }

    private void retrieveFrequentContactItems() {
        ArrayList<FrequentItem> frequentItems = BancomatSdk.getInstance().getUserFrequent();
        if(frequentItems != null) {
            ArrayList<FrequentItemConsumer> frequentItemConsumerList = new ArrayList<>();
            for (FrequentItem frequentItem : BancomatSdk.getInstance().getUserFrequent()) {
                frequentItemConsumerList.add(new FrequentItemConsumer(frequentItem));
            }
            homeViewModel.setFrequentItemListData(frequentItemConsumerList);
        } else {
            showEmptyFrequentContactItems(true);
            setContactsLoading(false);
            homeViewModel.setFrequentItemListData(new ArrayList<>());
        }
    }

    private void retrieveSplitBillHistory() {
        BancomatSdkInterface.Factory.getInstance().doGetSplitBillHistory(requireActivity(), result ->
                        homeViewModel.setSplitBillHistoryData(result)
                , SessionManager.getInstance().getSessionToken());
    }

    private void manageFrequentItemConsumersData(ArrayList<FrequentItemConsumer> frequentItemConsumers) {
        setContactsLoading(false);
        if(frequentItemConsumers.isEmpty()) {
            showEmptyFrequentContactItems(true);
        } else {
            frequentContactsAdapter = new HomeFrequentContactsAdapter(frequentItemConsumers, this);
            bottomSheetBinding.lastContactsRv.setAdapter(frequentContactsAdapter);
            showEmptyFrequentContactItems(false);
        }
    }

    private void manageSplitBillHistoryData(Result<List<SplitBillHistory>> result) {
        setSplitBillLoading(false);
        if (result != null) {
            if (result.isSuccess()) {
                List<SplitBillHistory> list = result.getResult();

                if (list.isEmpty()) {
                    showEmptySplitBillText(true);
                } else {
                    showEmptySplitBillText(false);
                    recentSplitBillAdapter = new HomeRecentSplitBillsAdapter(list, this);
                    bottomSheetBinding.lastSplitBillsRv.setAdapter(recentSplitBillAdapter);
                }
            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(getActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
            } else {
                showError(result.getStatusCode());

                showEmptySplitBillText(true);
                if (bottomSheetBinding.movementList.getAdapter() == null) {
                    EmptyAdapter emptyAdapter = new EmptyAdapter();
                    bottomSheetBinding.movementList.setAdapter(emptyAdapter);
                }
            }
        }
    }

    private void manageTransactionHistoryData(Result<PaymentHistoryData> result) {
        setMovementsLoading(false);
        if (result != null) {
            if (result.isSuccess()) {
                ArrayList<TransactionData> list = result.getResult().getTransactionDatas();
                ArrayList<DateDisplayData> transactions = new ArrayList<>();
                for (TransactionData transactionData : list) {
                    transactions.add(new DateTransaction(transactionData));
                }

                if (!list.isEmpty()) {
                    showEmptyTransactionsText(false);
                    transactionAdapter = new HomeTransactionAdapter(transactions, this);
                    bottomSheetBinding.movementList.setAdapter(transactionAdapter);
                    AnimationRecyclerViewUtil.runLayoutAnimation(bottomSheetBinding.movementList);
                } else {
                    showEmptyTransactionsText(true);
                    EmptyAdapter emptyAdapter = new EmptyAdapter();
                    bottomSheetBinding.movementList.setAdapter(emptyAdapter);
                }

            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(getActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
            } else {
                showError(result.getStatusCode());

                showEmptyTransactionsText(true);
                if (bottomSheetBinding.movementList.getAdapter() == null) {
                    EmptyAdapter emptyAdapter = new EmptyAdapter();
                    bottomSheetBinding.movementList.setAdapter(emptyAdapter);
                }
            }
        }
    }

    private void showEmptyFrequentContactItems(boolean isShow) {
        if (isShow) {
            AnimationFadeUtil.startFadeInAnimationV1(bottomSheetBinding.lastContactsText, DEFAULT_DURATION);
        } else {
            bottomSheetBinding.lastContactsText.setVisibility(View.GONE);
        }
    }
    private void showEmptySplitBillText(boolean isShow) {
        if (isShow) {
            AnimationFadeUtil.startFadeInAnimationV1(bottomSheetBinding.lastSplitBillsText, DEFAULT_DURATION);
        } else {
            bottomSheetBinding.lastSplitBillsText.setVisibility(View.GONE);
        }
    }

    private void showEmptyTransactionsText(boolean isShow) {
        if (isShow) {
            AnimationFadeUtil.startFadeInAnimationV1(bottomSheetBinding.movementText, DEFAULT_DURATION);
        } else {
            bottomSheetBinding.movementText.setVisibility(View.GONE);
        }
    }

    private void setContactsLoading(boolean isLoading) {
        if (isLoading) {
            bottomSheetBinding.lottieAnimationContacts.setVisibility(View.VISIBLE);
            bottomSheetBinding.lastContactsRv.setVisibility(View.GONE);
        } else {
            bottomSheetBinding.lottieAnimationContacts.setVisibility(View.GONE);
            bottomSheetBinding.lastContactsRv.setVisibility(View.VISIBLE);
        }
    }

    private void setSplitBillLoading(boolean isLoading) {
        if (isLoading) {
            bottomSheetBinding.lottieAnimationSplitBill.setVisibility(View.VISIBLE);
            bottomSheetBinding.lastSplitBillsRv.setVisibility(View.GONE);
        } else {
            bottomSheetBinding.lottieAnimationSplitBill.setVisibility(View.GONE);
            bottomSheetBinding.lastSplitBillsRv.setVisibility(View.VISIBLE);
        }
    }

    private void setMovementsLoading(boolean isLoading) {
        if (isLoading) {
            bottomSheetBinding.lottieAnimationMovements.setVisibility(View.VISIBLE);
            bottomSheetBinding.movementList.setVisibility(View.GONE);
        } else {
            bottomSheetBinding.lottieAnimationMovements.setVisibility(View.GONE);
            bottomSheetBinding.movementList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Boolean.TRUE.equals(homeViewModel.isRefreshHomeDataRequired().getValue())) {
            refreshData();
            homeViewModel.setRefreshHomeDataRequired(false);
            CustomLogger.d(TAG, "Refreshing data home bottom sheet");
        }
        isPlafondVisible = FullStackSdkDataManager.getInstance().getShowBalance();
        if (isPlafondVisible) {
            binding.imageEye.setImageResource(it.bancomatpay.sdkui.R.drawable.eye_show);
        } else {
            binding.imageEye.setImageResource(it.bancomatpay.sdkui.R.drawable.eye_hide);
        }
    }

    private void setPlafondText(boolean isPlafondVisible) {
        String amountBalance = StringUtils.getFormattedValue(ApplicationModel.getInstance().getUserData().getBalance());
        binding.textPlafond.setText(amountBalance);
        binding.textPlafondHidden.setText("××××××××");

        if (isPlafondVisible) {
            binding.imageEye.setImageResource(it.bancomatpay.sdkui.R.drawable.eye_show);

            if (binding.textPlafondHidden.getVisibility() == View.VISIBLE || isFirstBalanceLoad) {
                binding.textPlafond.setVisibility(View.VISIBLE);
                binding.textPlafondHidden.setVisibility(View.INVISIBLE);
            }

        } else {
            binding.imageEye.setImageResource(it.bancomatpay.sdkui.R.drawable.eye_hide);

            if (binding.textPlafond.getVisibility() == View.VISIBLE || isFirstBalanceLoad) {
                binding.textPlafond.setVisibility(View.INVISIBLE);
                binding.textPlafondHidden.setVisibility(View.VISIBLE);
            }
        }
    }

    public void manageUserData(UserData userData) {
        if (userData != null) {
            AnimationFadeUtil.startFadeOutAnimationV1(binding.textPlafondError, DEFAULT_DURATION, View.GONE);
            String amountBalance = StringUtils.getFormattedValue(userData.getBalance());
            if (TextUtils.isEmpty(amountBalance)) {
                binding.textPlafond.setText("");
                binding.textPlafondHidden.setText("××××××××");
                binding.textPlafondHidden.setVisibility(View.VISIBLE);
                FullStackSdkDataManager.getInstance().putShowBalance(false);
            } else {
                isPlafondVisible = FullStackSdkDataManager.getInstance().getShowBalance();
                setPlafondText(isPlafondVisible);
            }


            if (binding.layoutPlafondValue.getVisibility() != View.VISIBLE) {
                AnimationFadeUtil.startFadeInAnimationV1(binding.layoutPlafondValue, DEFAULT_DURATION);
            } else {
                binding.layoutPlafondValue.setVisibility(View.VISIBLE);
            }

            if (userData.getPaymentRequestNumber() != null) {
                if (userData.getPaymentRequestNumber().intValue() > 0) {
                    binding.layoutNotificationsCircle.post(() -> {
                        binding.layoutNotificationsCircle.setVisibility(View.VISIBLE);
                    });
                } else {
                    binding.layoutNotificationsCircle.post(() -> binding.layoutNotificationsCircle.setVisibility(View.INVISIBLE));
                }
            }

        } else {
            AnimationFadeUtil.startFadeInAnimationV1(binding.textPlafondError, DEFAULT_DURATION);
        }
        setFirstBalanceLoad(false);
    }

    public void setProfileInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }

    public void setFirstBalanceLoad(boolean isFirstLoad) {
        isFirstBalanceLoad = isFirstLoad;
    }

    @Override
    public void onListViewInteraction(Transaction item) {
        //transaction list item tap
        TransactionsFlowManager.goToDetailTransaction(requireActivity(), item);
    }

    @Override
    public void onTap(FrequentItemConsumer item) {
        //frequent contact item tap
        CjUtils.getInstance().startP2PPaymentFlow();

//        HashMap<String, String> mapEventParams = new HashMap<>();
//
//        CjUtils.getInstance().sendCustomerJourneyTagEvent(requireActivity(), KEY_P2P_CONTACT_SELECTED, mapEventParams, true);

        PaymentFlowManager.goToInsertAmount(requireActivity(), item, false, PaymentContactFlowType.SEND, true);
    }

    @Override
    public void onTap(SplitBillHistory item) {
        //split bill history item tap
        HomeFlowManager.goToSplitBill(requireActivity(), item);
    }

    public interface InteractionListener {
        void onProfileClick();

        void onCashbackClick();
    }
}
