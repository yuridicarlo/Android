package it.bancomatpay.sdkui.activities.split_bill.fragments;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CONTACT_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CONTACTS_CONSENT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.events.update.FrequentItemUpdate;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.activities.split_bill.SplitBillActivity;
import it.bancomatpay.sdkui.adapter.CheckableContactsAdapter;
import it.bancomatpay.sdkui.adapter.SplitBillContactsAdapter;
import it.bancomatpay.sdkui.databinding.FragmentCreateSplitBillBinding;
import it.bancomatpay.sdkui.events.ContactsRefreshEvent;
import it.bancomatpay.sdkui.fragment.GenericErrorFragment;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;
import it.bancomatpay.sdkui.model.FrequentsMerchantDataMerchant;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.SplitItemConsumer;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.NavHelper;
import it.bancomatpay.sdkui.viewModel.SplitBillViewModel;
import it.bancomatpay.sdkui.widgets.fastscroll.FastScroller;
import it.bancomatpay.sdkui.widgets.fastscroll.FastScrollerBuilder;

public class CreateSplitBillFragment extends GenericErrorFragment implements InteractionListener, SplitBillContactsAdapter.SplitMoneyContactClickListener {

    private static final String TAG = CreateSplitBillFragment.class.getSimpleName();

    private FragmentCreateSplitBillBinding binding;

    private SplitBillViewModel splitBillViewModel;

    public static final int PERMISSION_CONTACTS_FRAGMENT = 1055;

    ArrayList<FrequentItem> frequentItems;

    private int numberOfContactRefresh = 0;
    private CheckableContactsAdapter adapter;

    private SplitBillContactsAdapter selectedContactsAdapter;

    ActivityResultLauncher<Intent> activityResultLauncherContacts = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                //manageResult(REQUEST_CODE_SHOW_CONTACTS_CONSENT,result.getResultCode(),data);
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateSplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splitBillViewModel = new ViewModelProvider(requireActivity()).get(SplitBillViewModel.class);

        binding.toolbarSimple.setOnClickLeftImageListener(v -> requireActivity().onBackPressed());

        ((SplitBillActivity) requireActivity()).setLightStatusBar(binding.contactsLayoutContainer, R.color.white_background);

        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CONTACT_DISCLOSURE)) { //prima volta faccio vedere disclosure
            PermissionFlowManager.goToContactDisclosure(requireActivity(), activityResultLauncherContacts);
        }


        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(requireContext());
        binding.recyclerViewContacts.setLayoutManager(layoutManagerPanel);
        LinearLayoutManager layoutManagerPanel2 = new LinearLayoutManager(requireContext());
        layoutManagerPanel2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerViewSelectedContacts.setLayoutManager(layoutManagerPanel2);
        binding.recyclerViewContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean lastVisibility = true;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean isVisible = isVisible();
                if(isVisible != lastVisibility) {
                    if (isVisible) {
                        binding.refresh.setEnabled(true);
                    } else {
                        binding.refresh.setEnabled(false);
                    }
                    lastVisibility = isVisible;
                }
            }

            boolean isVisible() {
                return layoutManagerPanel.findFirstVisibleItemPosition() == 0;
            }
        });

        initContactList(splitBillViewModel.getItemsWithSeparator(), splitBillViewModel.getFrequentItemList());

        binding.refresh.setOnRefreshListener(() -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactList(false);
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACTS_FRAGMENT);
                stopRefreshing();
            }
        });
        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorAccentBancomat));

        if (splitBillViewModel.getItemsWithSeparator().isEmpty()) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                binding.refresh.setVisibility(View.VISIBLE);
                contactList(true);
                AnimationFadeUtil.startFadeOutAnimationV1(binding.consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            } else
                showContactPermissionDeniedLayout();
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable text) {
                adapter.getFilter().filter(text);

                if (text.length() > 0) {
                    if (binding.cancelButtonSearch.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.cancelButtonSearch, 250);
                    }
                } else {
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
                }

            }
        };
        binding.searchContactEditText.addTextChangedListener(textWatcher);

        binding.cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
            binding.searchContactEditText.getText().clear();
            AnimationFadeUtil.startFadeOutAnimationV1(binding.cancelButtonSearch, 250, View.INVISIBLE);
        }));

        binding.continueButton.setOnClickListener(new CustomOnClickListener(v -> {
            if(splitBillViewModel.showNoBpayAlert()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(R.string.warning_title);
                builder.setMessage(R.string.split_bill_all_contacts_not_bpay_alert_message)
                        .setCancelable(false);

                builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                    dialog.dismiss();
                });

                builder.show();
            } else {
                NavHelper.navigate(requireActivity(), CreateSplitBillFragmentDirections.actionCreateSplitBillFragmentToSelectAmountSplitBillFragment());
            }
            }));

        refreshEnableButton();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            binding.refresh.setVisibility(View.VISIBLE);
            contactList(true);
            AnimationFadeUtil.startFadeOutAnimationV1(binding.consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            AnimationFadeUtil.startFadeOutAnimationV1(binding.continueButton, 250, View.VISIBLE);
        } else
            showContactPermissionDeniedLayout();
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

    @Override
    public void onPause() {
        super.onPause();
        if (frequentItems != null) {
            BancomatSdk.getInstance().updateUserFrequent(frequentItems);
        }
    }

    private void initContactList(List<ItemInterfaceConsumer> contactList, List<FrequentItemConsumer> recentsItemList) {
        adapter = new CheckableContactsAdapter(requireContext(), contactList, recentsItemList, splitBillViewModel.getSelectedContactsList(), this);
        binding.recyclerViewContacts.setAdapter(adapter);

//        FastScroller fastScroller = new FastScrollerBuilder(binding.recyclerViewContacts).build();
//
//        fastScroller.setOnTouchCallback(isInViewTouchTarget -> {
//            if (binding.refresh.isEnabled() == isInViewTouchTarget) {
//                binding.refresh.setEnabled(!isInViewTouchTarget);
//            }
//        });

        ArrayList<SplitItemConsumer> listCopy = (ArrayList<SplitItemConsumer>) splitBillViewModel.getSelectedContactsList().clone();
        selectedContactsAdapter = new SplitBillContactsAdapter(listCopy, this);
        binding.recyclerViewSelectedContacts.setAdapter(selectedContactsAdapter);
    }

    public void changeStyle(boolean isEmpty) {
        if (isEmpty) {
            stopRefreshing();
            AnimationFadeUtil.startFadeInAnimationV1(binding.contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION);
            AnimationFadeUtil.startFadeOutAnimationV1(binding.recyclerViewContacts, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        } else {
            AnimationFadeUtil.startFadeOutAnimationV1(binding.contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            AnimationFadeUtil.startFadeInAnimationV1(binding.recyclerViewContacts, AnimationFadeUtil.DEFAULT_DURATION);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FrequentItemUpdate event) {
        frequentItems = event.getFrequentItems();
    }

    public void contactList(boolean forced) {
        binding.refresh.setRefreshing(true);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(
                requireActivity(), result -> contactsResponseHandler(result, false),
                forced, SessionManager.getInstance().getSessionToken());
        frequentItems = BancomatSdk.getInstance().getUserFrequent();
        manageListContacts(ApplicationModel.getInstance().getContactItems(), frequentItems);
    }

    public void showContactPermissionDeniedLayout() {
        binding.refresh.setVisibility(View.GONE);
        binding.continueButton.setVisibility(View.GONE);
        stopRefreshing();
        AnimationFadeUtil.startFadeInAnimationV1(binding.consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.recyclerViewContacts, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        binding.settingsBtn.setOnClickListener(new CustomOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }));
        AnimationFadeUtil.startFadeOutAnimationV1(binding.contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
    }

    private void manageListContacts(ArrayList<ContactItem> contactItemArrayList, ArrayList<FrequentItem> frequentItems) {

        ArrayList<FrequentItemConsumer> frequentItemListTmp = splitBillViewModel.getFrequentItemList();
        if (frequentItems != null) {
            this.frequentItems = frequentItems;
            frequentItemListTmp = new ArrayList<>();
            for (FrequentItem frequentItem : frequentItems) {
                if (frequentItem.getItemInterface() instanceof ContactItem) {
                    frequentItemListTmp.add(new FrequentItemConsumer(frequentItem));
                } else {
                    frequentItemListTmp.add(new FrequentsMerchantDataMerchant(frequentItem));
                }
            }
            splitBillViewModel.setFrequentItemList(frequentItemListTmp);
        }

        if (contactItemArrayList != null) {

            ArrayList<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();

            itemsWithSeparator.addAll(frequentItemListTmp);

            for (ContactItem contactItem : contactItemArrayList) {
                itemsWithSeparator.add(new SplitItemConsumer(contactItem));
            }
            if (!contactItemArrayList.isEmpty() && !itemsWithSeparator.isEmpty()) {
                binding.refresh.setRefreshing(false);
                adapter.updateModel(itemsWithSeparator, frequentItemListTmp);
                changeStyle(false);
            } else {
                if (numberOfContactRefresh >= 1) {
                    binding.refresh.setRefreshing(false);
                    changeStyle(true);
                } else {
                    binding.refresh.setRefreshing(true);
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                    changeStyle(false);
                }
            }

            numberOfContactRefresh++;
            splitBillViewModel.setItemsWithSeparator(itemsWithSeparator);
        } else {
            binding.refresh.setRefreshing(true);
            AnimationFadeUtil.startFadeOutAnimationV1(binding.contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            AnimationFadeUtil.startFadeOutAnimationV1(binding.consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(requireActivity(), result -> EventBus.getDefault().post(new ContactsRefreshEvent(result, true)), false, SessionManager.getInstance().getSessionToken());
        }
    }

    public void contactsResponseHandler(Result<SyncPhoneBookData> result, boolean isContactsSynched) {

        binding.refresh.setRefreshing(false);

        if (result != null) {
            if (result.isSuccess()) {
                if (result.getResult() != null) {
                    if (!isContactsSynched) {
                        manageListContacts(result.getResult().getContactItems(), frequentItems);
                    } else {
                        List<ItemInterfaceConsumer> itemsWithSeparator = new ArrayList<>();
                        for (ContactItem contactItem : result.getResult().getContactItems()) {
                            itemsWithSeparator.add(new SplitItemConsumer(contactItem));
                        }
                        adapter.updateModel(itemsWithSeparator, splitBillViewModel.getFrequentItemList());
                        if (!itemsWithSeparator.isEmpty()) {
                            binding.refresh.setRefreshing(false);
                        }
                        changeStyle(itemsWithSeparator.isEmpty());
                    }
                }

            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(requireActivity(), BCMAbortCallback.getInstance().getSessionRefreshListener());
            }

        } else {
            if (adapter != null && adapter.getItemCount() == 0) {
                changeStyle(true);
            }
        }
    }

    private void refreshEnableButton(){
        binding.continueButton.setEnabled(splitBillViewModel.isGroupSizeValid());
    }

    @Override
    public void onConsumerInteraction(ItemInterfaceConsumer item) {
        hideKeyboard();
        SplitItemConsumer splitItemConsumer;
        if(item instanceof SplitItemConsumer) {
            splitItemConsumer = (SplitItemConsumer) item;
        } else {
            splitItemConsumer =  SplitItemConsumer.fromFrequentItemConsumer((FrequentItemConsumer) item);
        }
        if (splitBillViewModel.toggleContact(splitItemConsumer)) {
            selectedContactsAdapter.addContact(splitItemConsumer);
        } else {
            selectedContactsAdapter.removeContact(binding.recyclerViewSelectedContacts, splitItemConsumer);
        }
        splitBillViewModel.logList();
        refreshEnableButton();
    }

    @Override
    public void onMerchantInteraction(ItemInterfaceConsumer item) {
        //Non usato
    }

    @Override
    public void onImageConsumerInteraction(ItemInterfaceConsumer item) {
        //Non usato
    }


    @Override
    public void onImageMerchantInteraction(ItemInterfaceConsumer item) {
        //Non usato
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void stopRefreshing() {
        binding.refresh.setRefreshing(false);
    }

    @Override
    public void onContactDelete(SplitItemConsumer contactItem) {
        splitBillViewModel.toggleContact(contactItem);
        selectedContactsAdapter.removeContact(binding.recyclerViewSelectedContacts, contactItem);
        adapter.toggleCheckContact(binding.recyclerViewSelectedContacts, contactItem);
        refreshEnableButton();
    }
}
