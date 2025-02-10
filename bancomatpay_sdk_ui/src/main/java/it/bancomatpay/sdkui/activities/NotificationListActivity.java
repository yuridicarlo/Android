package it.bancomatpay.sdkui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.DirectDebitRequest;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.adapter.EmptyAdapter;
import it.bancomatpay.sdkui.adapter.NotificationAdapter;
import it.bancomatpay.sdkui.databinding.ActivityBcmNotificationsBinding;
import it.bancomatpay.sdkui.flowmanager.BankIdFlowManager;
import it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.NotificationAccessItem;
import it.bancomatpay.sdkui.model.NotificationDirectDebitItem;
import it.bancomatpay.sdkui.model.NotificationPaymentItem;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.AnimationRecyclerViewUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;

import static it.bancomatpay.sdkui.utilities.AnimationFadeUtil.DEFAULT_DURATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_OPEN_FROM_NOTIFICATION_LIST;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_OPEN_FROM_NOTIFICATION_LIST;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PAYMENT_REQUEST_DATE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PAYMENT_REQUEST_ID;

public class NotificationListActivity extends GenericErrorActivity implements NotificationAdapter.InteractionListener {

    private ActivityBcmNotificationsBinding binding;

    NotificationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(NotificationListActivity.class.getSimpleName());
        binding = ActivityBcmNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimple.setOnClickLeftImageListener(v -> finish());

        LinearLayoutManager layoutManagerPanel = new LinearLayoutManager(this);
        binding.notificationPaymentList.setLayoutManager(layoutManagerPanel);

        adapter = new NotificationAdapter(new NotificationData(), this);
        binding.notificationPaymentList.setAdapter(adapter);

        binding.refresh.setOnRefreshListener(this::doRequest);
        binding.refresh.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorAccentBancomat));

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

    public void doRequest() {
        BancomatSdkInterface.Factory.getInstance().doGetPaymentRequest(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {

                    binding.refresh.setRefreshing(false);

                    /*boolean isListEmpty = false;
                    if (adapter.getItemCount() == 0) {
                        isListEmpty = true;
                    }*/

                    NotificationData notificationData = result.getResult();

                    // if (isListEmpty) {
                    if(!notificationData.getNotificationBankIdRequest().isEmpty() || !notificationData.getNotificationDirectDebitRequests().isEmpty() || !notificationData.getNotificationPaymentData().isEmpty()){
                        showEmptyText(false);
                        adapter = new NotificationAdapter(notificationData, this);
                        binding.notificationPaymentList.setAdapter(adapter);
                        AnimationRecyclerViewUtil.runLayoutAnimation(binding.notificationPaymentList);
                    }  else {
                        showEmptyText(true);
                        EmptyAdapter emptyAdapter = new EmptyAdapter();
                        binding.notificationPaymentList.setAdapter(emptyAdapter);
                    }

                    //   showEmptyText(adapter.getItemCount() == 0);

                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    binding.refresh.setRefreshing(false);
                    showEmptyText(true);
                    showError(result.getStatusCode());
                }

            } else {
                binding.refresh.setRefreshing(false);
                showEmptyText(true);
            }
        }, SessionManager.getInstance().getSessionToken());

        binding.refresh.setRefreshing(true);

    }

    public void showEmptyText(boolean isShow) {
        if (isShow) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.notificationText, DEFAULT_DURATION);
            binding.notificationPaymentList.setVisibility(View.INVISIBLE);
        } else {
            binding.notificationText.setVisibility(View.INVISIBLE);
            binding.notificationPaymentList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPaymentInteraction(NotificationPaymentItem paymentItem) {
        if (!binding.refresh.isRefreshing()) {
            if (paymentItem.getNotificationPaymentData().getItem() instanceof ShopItem) {

                CjUtils.getInstance().startP2BPaymentFlow();

                HashMap<String, String> mapEventParams = new HashMap<>();
                if (paymentItem.getNotificationPaymentData().getPaymentItem() != null) {
                    mapEventParams.put(PARAM_PAYMENT_REQUEST_ID, paymentItem.getNotificationPaymentData().getPaymentItem().getPaymentId());
                    mapEventParams.put(PARAM_PAYMENT_REQUEST_DATE,
                            CjUtils.getInstance().getPaymentDateTimestampString(
                                    paymentItem.getNotificationPaymentData().getPaymentItem().getPaymentDate()
                            ));
                }
                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        this, KEY_P2B_OPEN_FROM_NOTIFICATION_LIST, mapEventParams, true);

                ShopsDataMerchant shopsDataMerchant = new ShopsDataMerchant((ShopItem) paymentItem.getNotificationPaymentData().getItem());
                MerchantPaymentData abstractPaymentData = new MerchantPaymentData();
                abstractPaymentData.setDisplayData(shopsDataMerchant);
                abstractPaymentData.setAmount(paymentItem.getNotificationPaymentData().getPaymentItem().getAmount());
                abstractPaymentData.setCausal(paymentItem.getNotificationPaymentData().getPaymentItem().getCausal());
                abstractPaymentData.setCentsAmount(paymentItem.getNotificationPaymentData().getPaymentItem().getCentsAmount());
                abstractPaymentData.setPaymentId(paymentItem.getNotificationPaymentData().getPaymentItem().getPaymentId());
                abstractPaymentData.setTillId(paymentItem.getNotificationPaymentData().getPaymentItem().getTillId());
                abstractPaymentData.setTag(shopsDataMerchant.getShopItem().getTag());
                abstractPaymentData.setShopId(shopsDataMerchant.getShopItem().getShopId());
                abstractPaymentData.setRequestPayment(true);
                abstractPaymentData.setType(paymentItem.getNotificationPaymentData().getPaymentItem().getType());

                PaymentFlowManager.goToAcceptance(this, abstractPaymentData, true);

            } else if (paymentItem.getNotificationPaymentData().getItem() instanceof ContactItem) {

                CjUtils.getInstance().startP2PPaymentFlow();

                HashMap<String, String> mapEventParams = new HashMap<>();
                if (paymentItem.getNotificationPaymentData().getPaymentItem() != null) {
                    mapEventParams.put(PARAM_PAYMENT_REQUEST_ID, paymentItem.getNotificationPaymentData().getPaymentItem().getPaymentId());
                    mapEventParams.put(PARAM_PAYMENT_REQUEST_DATE,
                            CjUtils.getInstance().getPaymentDateTimestampString(
                                    paymentItem.getNotificationPaymentData().getPaymentItem().getPaymentDate()
                            ));
                }
                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                        this, KEY_P2P_OPEN_FROM_NOTIFICATION_LIST, mapEventParams, true);

                ContactItem contact = new ContactItem();
                contact.setName(paymentItem.getNotificationPaymentData().getPaymentItem().getInsignia());
                contact.setMsisdn(((ContactItem) paymentItem.getNotificationPaymentData().getItem()).getMsisdn());
                contact.setType(paymentItem.getNotificationPaymentData().getItem().getType());
                contact.setPhotoUri(((ContactItem) paymentItem.getNotificationPaymentData().getItem()).getPhotoUri());
                contact.setContactId(((ContactItem) paymentItem.getNotificationPaymentData().getItem()).getContactId());
                contact.setLetter(paymentItem.getInitials());
                ContactsItemConsumer contactDisplayData = new ContactsItemConsumer(contact);

                ConsumerPaymentData abstractPaymentData = new ConsumerPaymentData();
                abstractPaymentData.setDisplayData(contactDisplayData);
                abstractPaymentData.setAmount(paymentItem.getNotificationPaymentData().getPaymentItem().getAmount());
                abstractPaymentData.setCausal(paymentItem.getNotificationPaymentData().getPaymentItem().getCausal());
                abstractPaymentData.setCentsAmount(paymentItem.getNotificationPaymentData().getPaymentItem().getCentsAmount());
                abstractPaymentData.setPaymentId(paymentItem.getNotificationPaymentData().getPaymentItem().getPaymentId());
                abstractPaymentData.setExpirationDate(paymentItem.getNotificationPaymentData().getPaymentItem().getExpirationDate());
                abstractPaymentData.setRequestPayment(true);

                PaymentFlowManager.goToAcceptance(this, abstractPaymentData, true);
            }
        }
    }

    @Override
    public void onAccessInteraction(NotificationAccessItem accessItem) {
        if (!binding.refresh.isRefreshing()) {
            BankIdFlowManager.goToBankIdAuthorize(this,
                    accessItem.getNotificationAccessData().getRequestId(),
                    accessItem.getNotificationAccessData().getBankIdMerchantData(), true);
        }
    }

    @Override
    public void onDirectDebitInteraction(NotificationDirectDebitItem debitItem) {
        if (!binding.refresh.isRefreshing()) {
            DirectDebitRequest directDebitRequest = debitItem.getNotificationdirectDebitData();
            DirectDebitFlowManager.goToDirectDebitAuthorize(this, directDebitRequest, true);
        }
    }

}
