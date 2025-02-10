package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.events.update.FrequentItemUpdate;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.PhoneNumber;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.adapter.ContactsAdapter;
import it.bancomatpay.sdkui.events.ContactsRefreshEvent;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.FrequentItemConsumer;
import it.bancomatpay.sdkui.model.FrequentsMerchantDataMerchant;
import it.bancomatpay.sdkui.model.InteractionListener;
import it.bancomatpay.sdkui.model.ItemInterfaceConsumer;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.widgets.BottomDialogSendMoney;
import it.bancomatpay.sdkui.widgets.ToolbarSimple;
import it.bancomatpay.sdkui.widgets.fastscroll.FastScroller;
import it.bancomatpay.sdkui.widgets.fastscroll.FastScrollerBuilder;

import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.CONTACT_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_CONTACTS_CONSENT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_HOME_PHONEBOOK;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_ADD_NUMBER;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CONTACT_SELECTED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PERMISSION_DENIED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PERMISSION;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_SEARCH_TEXT;

public class ContactsActivity extends GenericErrorActivity implements InteractionListener {

    private final static String TAG = ContactsActivity.class.getSimpleName();
    public static final int PERMISSION_CONTACTS_ACTIVITY = 1055;

    protected static final int REQUEST_PERMISSION_CONTACTS = 1000;
    public static String PAYMENT_CONTACT_FLOW_TYPE = "payment_contact_flow_type";

    List<ItemInterfaceConsumer> itemsWithSeparator;
    ArrayList<FrequentItem> frequentItems;
    ArrayList<FrequentItemConsumer> frequentItemList = new ArrayList<>();

    private int numberOfContactRefresh = 0;
    private PaymentContactFlowType flowType;
    private ContactsAdapter adapter;

    private BottomDialogSendMoney bottomDialog;

    protected ToolbarSimple toolbar;
    View spaceTop;
    TextView title;
    RecyclerView recyclerViewContacts;
    SwipeRefreshLayout refresh;
    View cancelButtonSearch;
    EditText searchContactEditText;
    TextView contactListEmpty;

    View consentsDeniedLayout;
    AppCompatButton settingsBtn;

    ActivityResultLauncher<Intent> activityResultLauncherContacts = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_CONTACTS_CONSENT,result.getResultCode(),data);
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_contacts);

        toolbar = findViewById(R.id.toolbar_simple);
        spaceTop = findViewById(R.id.space_top);
        title = findViewById(R.id.text_title);
        recyclerViewContacts = findViewById(R.id.recycler_view_contacts);
        refresh = findViewById(R.id.refresh);
        cancelButtonSearch = findViewById(R.id.cancel_button_search);
        searchContactEditText = findViewById(R.id.search_contact_edit_text);
        contactListEmpty = findViewById(R.id.contact_list_empty);
        consentsDeniedLayout = findViewById(R.id.consents_denied_layout);
        settingsBtn = findViewById(R.id.settings_btn);

        flowType = (PaymentContactFlowType) getIntent().getSerializableExtra(PAYMENT_CONTACT_FLOW_TYPE);
        if (flowType == null) {
            if (savedInstanceState != null) {
                flowType = (PaymentContactFlowType) savedInstanceState.getSerializable(PAYMENT_CONTACT_FLOW_TYPE);
            }
        }
        if (flowType == null) {
            flowType = PaymentContactFlowType.SEND;
        }

        if (flowType == PaymentContactFlowType.REQUEST) {
            title.setText(getString(R.string.tab_layout_get_money));
        } else {
            title.setText(getString(R.string.tab_layout_send_money));
        }

        //Prevent keyboard open
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        toolbar.setOnClickLeftImageListener(v ->
                finish()
        );
        toolbar.setOnClickRightImageListener(v -> {
            bottomDialog = new BottomDialogSendMoney(this, number -> {
                if (PhoneNumber.isValidNumber(number)) {
                    ContactItem contactItem = new ContactItem();
                    contactItem.setMsisdn(number);
                    ContactsItemConsumer contactsItemConsumer = new ContactsItemConsumer(contactItem);
                    if (bottomDialog.isVisible()) {
                        bottomDialog.dismiss();
                    }
                    PaymentFlowManager.goToInsertAmount(this, contactsItemConsumer, false, flowType, false);
                }
            });
            bottomDialog.showDialog();
            CjUtils.getInstance().startP2PPaymentFlow();

            CjUtils.getInstance().sendCustomerJourneyTagEvent(
                    this, KEY_P2P_ADD_NUMBER, null, true);
        });
        toolbar.post(() -> toolbar.setRightCenterImageVisibility(false));

        itemsWithSeparator = new ArrayList<>();
        frequentItemList = new ArrayList<>();

        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(this);
        recyclerViewContacts.setLayoutManager(layoutManagerPanel);

        FastScroller fastScroller = new FastScrollerBuilder(recyclerViewContacts).build();

        fastScroller.setOnTouchCallback(isInViewTouchTarget -> {
            if (refresh.isEnabled() == isInViewTouchTarget) {
                refresh.setEnabled(!isInViewTouchTarget);
            }
        });

        initContactList(itemsWithSeparator, frequentItemList);


        refresh.setOnRefreshListener(() -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactList(true);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACTS_ACTIVITY);
                stopRefreshing();
            }
        });
        refresh.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorAccentBancomat));

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
                    if (cancelButtonSearch.getVisibility() != View.VISIBLE) {
                        AnimationFadeUtil.startFadeInAnimationV1(cancelButtonSearch, 250);
                    }
                } else {
                    AnimationFadeUtil.startFadeOutAnimationV1(cancelButtonSearch, 250, View.INVISIBLE);
                }

            }
        };
        searchContactEditText.addTextChangedListener(textWatcher);

        cancelButtonSearch.setOnClickListener(new CustomOnClickListener(v -> {
            searchContactEditText.getText().clear();
            AnimationFadeUtil.startFadeOutAnimationV1(cancelButtonSearch, 250, View.INVISIBLE);
        }));

        int insetTop = BancomatDataManager.getInstance().getScreenInsetTop();
        if (insetTop != 0) {
            spaceTop.post(() -> {
                LinearLayout.LayoutParams spaceParams = (LinearLayout.LayoutParams) spaceTop.getLayoutParams();
                spaceParams.height = insetTop;
                spaceTop.setLayoutParams(spaceParams);
                spaceTop.requestLayout();
            });
        }

        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(CONTACT_DISCLOSURE)) { //prima volta faccio vedere disclosure
            showPermissionContacts();
        } else {
            if (EventBus.getDefault().getStickyEvent(HomeSectionEvent.class) == null) {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        this, KEY_HOME_PHONEBOOK, null, false);
            } else {
                EventBus.getDefault().removeStickyEvent(HomeSectionEvent.class);
            }
            if (!hasPermissionContacts()) { //da seconda volta in poi, se i permessi non sono stati dati, li richiedo dopo aver mostrato il fragment
                requestPermissionContacts();
            }
        }
    }

    private boolean hasPermissionContacts() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionContacts() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CONTACTS);
    }

    private void showPermissionContacts() {
        PermissionFlowManager.goToContactDisclosure(this, activityResultLauncherContacts);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(this,
                        result -> CustomLogger.d(TAG, "getSyncPhoneBook end"),
                        true, SessionManager.getInstance().getSessionToken());
                BancomatSdk.getInstance().getUserFrequent();
            } else {
                HashMap<String, String> mapEventParams = new HashMap<>();
                mapEventParams.put(PARAM_PERMISSION, "Contacts");
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_PERMISSION_DENIED, mapEventParams, false);
            }

        }
    }

    private void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SHOW_CONTACTS_CONSENT) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(it.bancomatpay.sdkui.R.anim.fade_in, it.bancomatpay.sdkui.R.anim.fade_out, it.bancomatpay.sdkui.R.anim.fade_in, it.bancomatpay.sdkui.R.anim.fade_out);
//            AnimationFadeUtil.startFadeInAnimationV1(imageStatusBarBackground, AnimationFadeUtil.DEFAULT_DURATION);
            if (EventBus.getDefault().getStickyEvent(HomeSectionEvent.class) == null) {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        this, KEY_HOME_PHONEBOOK, null, false);
            } else {
                EventBus.getDefault().removeStickyEvent(HomeSectionEvent.class);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //  AnimationFadeUtil.startFadeOutAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            refresh.setVisibility(View.VISIBLE);
            contactList(true);
            AnimationFadeUtil.startFadeOutAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        } else
            showContactPermissionDeniedLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (frequentItems != null) {
            BancomatSdk.getInstance().updateUserFrequent(frequentItems);
        }
    }

    private void initContactList(List<ItemInterfaceConsumer> contactList, List<FrequentItemConsumer> recentsItemList) {
        adapter = new ContactsAdapter(this, contactList, recentsItemList, this);
        recyclerViewContacts.setAdapter(adapter);
    }

    public void changeStyle(boolean isEmpty) {
        if (isEmpty) {
            stopRefreshing();
            AnimationFadeUtil.startFadeInAnimationV1(contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION);
            AnimationFadeUtil.startFadeOutAnimationV1(recyclerViewContacts, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
         //   AnimationFadeUtil.startFadeOutAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        } else {
            AnimationFadeUtil.startFadeOutAnimationV1(contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            AnimationFadeUtil.startFadeInAnimationV1(recyclerViewContacts, AnimationFadeUtil.DEFAULT_DURATION);
       //     AnimationFadeUtil.startFadeOutAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FrequentItemUpdate event) {
        frequentItems = event.getFrequentItems();
    }

    public void contactList(boolean forced) {
        refresh.setRefreshing(true);
        AnimationFadeUtil.startFadeOutAnimationV1(contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(
                this, result -> EventBus.getDefault().post(new ContactsRefreshEvent(result, false)),
                forced, SessionManager.getInstance().getSessionToken());
        frequentItems = BancomatSdk.getInstance().getUserFrequent();
        manageListContacts(null, frequentItems);
    }

    public void showContactPermissionDeniedLayout() {
        refresh.setVisibility(View.GONE);
        stopRefreshing();
        AnimationFadeUtil.startFadeInAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION);
        AnimationFadeUtil.startFadeOutAnimationV1(recyclerViewContacts, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
        settingsBtn.setOnClickListener(new CustomOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }));
        AnimationFadeUtil.startFadeOutAnimationV1(contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
    }

    private void manageListContacts(ArrayList<ContactItem> contactItemArrayList, ArrayList<FrequentItem> frequentItems) {

        if (frequentItems != null) {
            this.frequentItems = frequentItems;
            frequentItemList = new ArrayList<>();
            for (FrequentItem frequentItem : frequentItems) {
                if (frequentItem.getItemInterface() instanceof ContactItem) {
                    frequentItemList.add(new FrequentItemConsumer(frequentItem));
                } else {
                    frequentItemList.add(new FrequentsMerchantDataMerchant(frequentItem));
                }
            }
        }

        if (contactItemArrayList != null) {

            itemsWithSeparator = new ArrayList<>();

            itemsWithSeparator.addAll(frequentItemList);

            for (ContactItem contactItem : contactItemArrayList) {
                if (flowType == PaymentContactFlowType.SEND || contactItem.getType() != ContactItem.Type.NONE) {
                    itemsWithSeparator.add(new ContactsItemConsumer(contactItem));
                }
            }

            if (!contactItemArrayList.isEmpty() && !itemsWithSeparator.isEmpty()) {
                refresh.setRefreshing(false);
                adapter.updateModel(itemsWithSeparator, frequentItemList);
                changeStyle(false);
            } else {
                if (numberOfContactRefresh >= 1) {
                    refresh.setRefreshing(false);
                    changeStyle(true);
                } else {
                    refresh.setRefreshing(true);
                    AnimationFadeUtil.startFadeOutAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                    changeStyle(false);
                }
            }

            numberOfContactRefresh++;

        } else {
            refresh.setRefreshing(true);
            AnimationFadeUtil.startFadeOutAnimationV1(contactListEmpty, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            AnimationFadeUtil.startFadeOutAnimationV1(consentsDeniedLayout, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
            BancomatSdkInterface.Factory.getInstance().getSyncPhoneBook(this, result -> EventBus.getDefault().post(new ContactsRefreshEvent(result, true)), false, SessionManager.getInstance().getSessionToken());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ContactsRefreshEvent event) {

        Result<SyncPhoneBookData> result = event.getResult();

        refresh.setRefreshing(false);

        if (result != null) {
            if (result.isSuccess()) {
                if (result.getResult() != null) {
                    if (!event.isContactsSynched()) {
                        manageListContacts(result.getResult().getContactItems(), frequentItems);
                    } else {
                        itemsWithSeparator = new ArrayList<>();
                        for (ContactItem contactItem : result.getResult().getContactItems()) {
                            itemsWithSeparator.add(new ContactsItemConsumer(contactItem));
                        }
                        adapter.updateModel(itemsWithSeparator, frequentItemList);
                        if (!itemsWithSeparator.isEmpty()) {
                            refresh.setRefreshing(false);
                        }
                        changeStyle(itemsWithSeparator.isEmpty());
                    }
                }

            } else if (result.isSessionExpired()) {
                BCMAbortCallback.getInstance().getAuthenticationListener()
                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
            }

        } else {
            if (adapter != null && adapter.getItemCount() == 0) {
                changeStyle(true);
            }
        }
    }

    @Override
    public void onConsumerInteraction(ItemInterfaceConsumer item) {
        hideKeyboard();

        CjUtils.getInstance().startP2PPaymentFlow();

        HashMap<String, String> mapEventParams = new HashMap<>();
        if (!searchContactEditText.getText().toString().isEmpty()) {
            mapEventParams.put(PARAM_SEARCH_TEXT, searchContactEditText.getText().toString());
        }
        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_CONTACT_SELECTED, mapEventParams, true);

        PaymentFlowManager.goToInsertAmount(this, item, false, flowType, false);
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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void stopRefreshing() {
        refresh.setRefreshing(false);
    }

    public void onSaveInstanceState(Bundle outstate) {
        CustomLogger.d(TAG, "onSaveInstanceState");
        outstate.putSerializable(PAYMENT_CONTACT_FLOW_TYPE, flowType);
        super.onSaveInstanceState(outstate);
    }
}
