package it.bancomat.pay.consumer.extended.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.HashMap;

import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.notification.Push;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.utilities.UserMonitoringConstants;
import it.bancomatpay.consumer.R;
import it.bancomatpay.consumer.databinding.ActivityPushDataLoadingBinding;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.BankIdRequest;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.NotificationPaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.activities.FullscreenActivity;
import it.bancomatpay.sdkui.activities.GenericErrorActivity;
import it.bancomatpay.sdkui.databinding.ActivityBcmPaymentDataLoadingBinding;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.flowmanager.BankIdFlowManager;
import it.bancomatpay.sdkui.flowmanager.DirectDebitFlowManager;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;

import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.QR_DATA;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_OPEN_FROM_PUSH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_OPEN_FROM_PUSH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_QR_CODE_SCAN;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PAYMENT_REQUEST_DATE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PAYMENT_REQUEST_ID;

public class PaymentPushLoadingActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityPushDataLoadingBinding binding;
    public static final String PUSH_DATA = "push";
    private final static String TAG = PaymentPushLoadingActivity.class.getSimpleName();

    private Push pushData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(PaymentPushLoadingActivity.class.getSimpleName());
        binding = ActivityPushDataLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pushData = (Push) getIntent().getSerializableExtra(PUSH_DATA);

        if (pushData.getPaymentRequestId() != null) {
            if(pushData.getType() == Push.Type.DIRECT_DEBIT || pushData.getType() == Push.Type.BANKID) {
                binding.description.setText(R.string.dots);
                binding.recoveryPaymentDataLabel.setText(R.string.dots);
            }
            BancomatSdkInterface sdkCore = BancomatSdkInterface.Factory.getInstance();
            sdkCore.doGetPaymentRequest(this, result -> {

                        if (result != null) {
                            if (result.isSuccess()) {

                                if (result.getResult().getNotificationPaymentData() != null && result.getResult().getNotificationPaymentData().size() > 0) {

                                    NotificationPaymentData item = result.getResult().getNotificationPaymentData().get(0);

                                    if (item.getItem() instanceof ShopItem) {

                                        CjUtils.getInstance().startP2BPaymentFlow();

                                        HashMap<String, String> mapEventParams = new HashMap<>();
                                        if (item.getPaymentItem() != null) {
                                            mapEventParams.put(PARAM_PAYMENT_REQUEST_ID, item.getPaymentItem().getPaymentId());
                                            mapEventParams.put(PARAM_PAYMENT_REQUEST_DATE,
                                                    CjUtils.getInstance().getPaymentDateTimestampString(
                                                            item.getPaymentItem().getPaymentDate()
                                                    ));
                                        }
                                        CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                                this, KEY_P2B_OPEN_FROM_PUSH, mapEventParams, true);

                                        Task<?> task = BancomatPayApiInterface.Factory.getInstance().doUserMonitoring(resultMonitoring -> {
                                                    if (resultMonitoring != null) {
                                                        if (resultMonitoring.isSuccess()) {
                                                            CustomLogger.d(TAG, "doUserMonitoringTask success");
                                                        } else {
                                                            CustomLogger.e(TAG, "Error: doUserMonitoring failed");
                                                        }
                                                    }
                                                },
                                                AppBancomatDataManager.getInstance().getBankUuid(),
                                                UserMonitoringConstants.ECOMMERCE_TAP_ON_PUSH,
                                                UserMonitoringConstants.ECOMMERCE_TAP_ON_PUSH_EVENT,
                                                "");
                                        task.execute();

                                        ShopsDataMerchant shopsDataMerchant = new ShopsDataMerchant((ShopItem) item.getItem());
                                        MerchantPaymentData abstractPaymentData = new MerchantPaymentData();
                                        abstractPaymentData.setDisplayData(shopsDataMerchant);
                                        abstractPaymentData.setAmount(item.getPaymentItem().getAmount());
                                        abstractPaymentData.setCausal(item.getPaymentItem().getCausal());
                                        abstractPaymentData.setCentsAmount(item.getPaymentItem().getCentsAmount());
                                        abstractPaymentData.setPaymentId(item.getPaymentItem().getPaymentId());
                                        abstractPaymentData.setTillId(item.getPaymentItem().getTillId());

                                        abstractPaymentData.setTag(shopsDataMerchant.getShopItem().getTag());
                                        abstractPaymentData.setShopId(shopsDataMerchant.getShopItem().getShopId());

                                        abstractPaymentData.setRequestPayment(true);
                                        abstractPaymentData.setType(item.getPaymentItem().getType());

                                        PaymentFlowManager.goToAcceptanceNoAnimation(this, abstractPaymentData, false);
                                        finish();

                                    } else if (item.getItem() instanceof ContactItem) {

                                        CjUtils.getInstance().startP2PPaymentFlow();

                                        HashMap<String, String> mapEventParams = new HashMap<>();
                                        if (item.getPaymentItem() != null) {
                                            mapEventParams.put(PARAM_PAYMENT_REQUEST_ID, item.getPaymentItem().getPaymentId());
                                            mapEventParams.put(PARAM_PAYMENT_REQUEST_DATE,
                                                    CjUtils.getInstance().getPaymentDateTimestampString(
                                                            item.getPaymentItem().getPaymentDate()
                                                    ));
                                        }
                                        CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                                this, KEY_P2P_OPEN_FROM_PUSH, mapEventParams, true);

                                        ContactItem contact = new ContactItem();
                                        contact.setName(item.getPaymentItem().getInsignia());
                                        contact.setMsisdn(((ContactItem) item.getItem()).getMsisdn());
                                        contact.setType(item.getItem().getType());
                                        contact.setPhotoUri(((ContactItem) item.getItem()).getPhotoUri());
                                        contact.setContactId(((ContactItem) item.getItem()).getContactId());
                                        contact.setLetter(contact.getInitials());

                                        ContactsItemConsumer paymentDataConsumer = new ContactsItemConsumer(contact);
                                        ConsumerPaymentData abstractPaymentData = new ConsumerPaymentData();
                                        abstractPaymentData.setDisplayData(paymentDataConsumer);
                                        abstractPaymentData.setAmount(item.getPaymentItem().getAmount());
                                        abstractPaymentData.setCausal(item.getPaymentItem().getCausal());
                                        abstractPaymentData.setCentsAmount(item.getPaymentItem().getCentsAmount());
                                        abstractPaymentData.setPaymentId(item.getPaymentItem().getPaymentId());
                                        if (item.getPaymentItem().getExpirationDate() != null) {
                                            abstractPaymentData.setExpirationDate(item.getPaymentItem().getExpirationDate());
                                        }
                                        abstractPaymentData.setRequestPayment(true);

                                        PaymentFlowManager.goToAcceptanceNoAnimation(this, abstractPaymentData, false);
                                        finish();
                                    }

                                } else if (result.getResult().getNotificationBankIdRequest() != null && result.getResult().getNotificationBankIdRequest().size() > 0) {

                                    BankIdRequest bankIdRequest = result.getResult().getNotificationBankIdRequest().get(0);
                                    BankIdFlowManager.goToBankIdAuthorizeNoAnimation(this,
                                            bankIdRequest.getRequestId(), bankIdRequest.getBankIdMerchantData(), false);

                                    finish();

                                } else if (result.getResult().getNotificationDirectDebitRequests() != null && result.getResult().getNotificationDirectDebitRequests().size() > 0) {
                                    DirectDebitFlowManager.goToDirectDebitAuthorizeNoAnimation(this,
                                            result.getResult().getNotificationDirectDebitRequests().get(0), false);
                                    finish();

                                } else {
                                    HomeFlowManager.goToNotificationsNoAnimation(this);
                                    finish();
                                }
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> finish());
                            }
                        } else {
                            finish();
                        }
                    }, pushData.getPaymentRequestId(), pushData.getPaymentRequestType(), AppBancomatDataManager.getInstance().getTokens().getOauth());

        }else {
            finish();
        }
        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }



    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
    }
}
