package it.bancomatpay.sdkui.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.BCMOperation;
import it.bancomatpay.sdk.manager.model.BCMPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ActivityBcmPaymentAcceptanceBinding;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.GoToHomeInterface;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_REQUEST_DENY_FAILED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_PAYMENT_REQUEST_DENY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_PAYMENT_REQUEST_DENY;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_PAYMENT_REQUEST_ID;

public class PaymentAcceptanceActivity extends GenericErrorActivity implements GoToHomeInterface, PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmPaymentAcceptanceBinding binding;

    AbstractPaymentData paymentData;
    boolean isFromNotification;
    boolean isConfirmButtonLocked = false;
    private boolean canCancelPreAuthorization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(PaymentAcceptanceActivity.class.getSimpleName());
        binding = ActivityBcmPaymentAcceptanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (BCMReturnHomeCallback.getInstance().getReturnHomeListener() == null) {
            BancomatFullStackSdk.getInstance().setReturnHomeListener(this);
        }

        paymentData = (AbstractPaymentData) getIntent().getSerializableExtra(PaymentFlowManager.PAYMENT_DATA);
        isFromNotification = getIntent().getBooleanExtra(PaymentFlowManager.IS_FROM_NOTIFICATION, false);

        if (paymentData != null) {
            initLayout(paymentData);
        }

        binding.toolbarSimple.setRightImageVisibility(true);
        binding.toolbarSimple.setOnClickRightImageListener(v -> {
           onBackPressed();
        });

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);

    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }


    private void initLayout(AbstractPaymentData paymentData) {
        if (paymentData.getDisplayData().getItemInterface() instanceof ShopItem
                || paymentData.getDisplayData().getItemInterface() instanceof ShopsDataMerchant) {

            binding.layoutConsumer.setVisibility(View.GONE);
            binding.layoutMerchant.setVisibility(View.VISIBLE);

            String constraint = paymentData.getDisplayData().getTitle() + " " + paymentData.getDisplayData().getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, constraint);

            binding.textMerchantName.setText(paymentData.getDisplayData().getTitle());
            binding.textMerchantAddress.setText(paymentData.getDisplayData().getDescription());

            if (paymentData.getType() == PaymentItem.EPaymentRequestType.PREAUTHORIZATION) {
                binding.textPaymentType.setText(getString(R.string.money_request_preauthorization_label));
            } else if (paymentData.getType() == PaymentItem.EPaymentRequestType.PAYMENT_REQUEST) {
                binding.textPaymentType.setText(getString(R.string.money_request_label));
            }

        } else {

            binding.layoutConsumer.setVisibility(View.VISIBLE);
            binding.layoutMerchant.setVisibility(View.GONE);

            binding.layoutMerchantImage.setBackgroundResource(R.drawable.img_recap);

            if (paymentData.getDisplayData().getBitmap() != null) {
                binding.contactConsumerImageProfile.setImageBitmap(paymentData.getDisplayData().getBitmap());
                binding.contactConsumerLetter.setVisibility(View.GONE);
            } else {
                binding.contactConsumerLetter.setVisibility(View.VISIBLE);
                binding.contactConsumerLetter.setText(paymentData.getDisplayData().getInitials());
            }

            if (TextUtils.isEmpty(paymentData.getDisplayData().getTitle())) {
                binding.contactConsumerName.setVisibility(View.GONE);
            } else {
                binding.contactConsumerName.setText(paymentData.getDisplayData().getTitle());
            }
            if (!TextUtils.isEmpty(paymentData.getDisplayData().getPhoneNumber())
                    && !paymentData.getDisplayData().getTitle().equals(paymentData.getDisplayData().getPhoneNumber())) {
                binding.contactConsumerNumber.setText(paymentData.getDisplayData().getPhoneNumber());
            }

            if (paymentData.getExpirationDate() != null) {
                String expirationDay = StringUtils.getDateStringFormatted(paymentData.getExpirationDate(), "dd MMMM");
                String expirationTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(paymentData.getExpirationDate());
                String expirationDate = getString(R.string.expiration_message, expirationDay, expirationTime);
                binding.expirationDateLayout.setVisibility(View.VISIBLE);
                binding.expirationDateMessage.setText(expirationDate.substring(0, 1).toUpperCase() + expirationDate.substring(1));
                binding.lineExpirationDate.setVisibility(View.GONE);
            }

        }

        if (paymentData.getType() != PaymentItem.EPaymentRequestType.PREAUTHORIZATION && !TextUtils.isEmpty(paymentData.getCausal())) {
            binding.messageLayout.setVisibility(View.VISIBLE);
            binding.message.setText(paymentData.getCausal());
            binding.lineExpirationDate.setVisibility(View.VISIBLE);
        }

        if (paymentData.getAmount() != null) {
            binding.totalAmount.setText(StringUtils.getFormattedValue(paymentData.getAmount()));
        }

        binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> clickConfirm()));
        binding.refuseButton.setOnClickListener(new CustomOnClickListener(v -> clickRefuse()));

    }

    private void clickConfirm() {
        if ( isConfirmButtonLocked ) {
            return;
        }
        isConfirmButtonLocked = true;
        binding.confirmButton.setEnabled(false);
        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();

        OnCompleteResultListener<VerifyPaymentData> listener = result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    // in this case we do not release the lock as it may take some time to load the following screen (and we won't need this anymore)
                    canCancelPreAuthorization = true;

                    paymentData.setFee(result.getResult().getFee());

                    String hiddenNameResult = result.getResult().getContactName();
                    paymentData.setFee(result.getResult().getFee());
                    paymentData.setPaymentRequestId(paymentData.getPaymentId());
                    paymentData.setPaymentId(result.getResult().getPaymentId());
                    LoaderHelper.dismissLoader();
                    PaymentFlowManager.goToResultPayment(
                            this, paymentData, hiddenNameResult, true, false, isFromNotification);

                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    isConfirmButtonLocked = false;
                    binding.confirmButton.setEnabled(true);
                    LoaderHelper.dismissLoader();
                    showError(result.getStatusCode());
                }
            } else {
                isConfirmButtonLocked = false;
                binding.confirmButton.setEnabled(true);
                LoaderHelper.dismissLoader();
                showError(StatusCode.Http.GENERIC_ERROR);
            }
        };

        if (paymentData instanceof ConsumerPaymentData) {

            LoaderHelper.showLoader(this);

            BCMOperation bcmPayment = new BCMOperation();
            bcmPayment.setMsisdn(paymentData.getDisplayData().getItemInterface().getPhoneNumber());
            bcmPayment.setAmount(paymentData.getCentsAmount());
            bcmPayment.setPaymentRequestId(paymentData.getPaymentId());
            bcmPayment.setCausal(!TextUtils.isEmpty(paymentData.getCausal()) ? paymentData.getCausal() : null);

            sdk.doVerifyPaymentP2P(this, listener, bcmPayment,
                    SessionManager.getInstance().getSessionToken());

        } else {

            PaymentFlowManager.goToResultPayment(
                    this, paymentData, "", true, false, isFromNotification);
        }

    }

    private void clickRefuse() {

        LoaderHelper.showLoader(this);

        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();

        HashMap<String, String> mapEventParams = new HashMap<>();
        if (paymentData instanceof MerchantPaymentData) {

            mapEventParams.put(PARAM_PAYMENT_REQUEST_ID, paymentData.getPaymentId());

            BCMPaymentRequest bcmPaymentRequest = new BCMPaymentRequest();
            bcmPaymentRequest.setPaymentId(paymentData.getPaymentId());

            sdk.doDenyPaymentRequestP2B(
                    this, result -> {
                        LoaderHelper.dismissLoader();
                        if (result != null) {
                            if (result.isSuccess()) {
                                CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                        this, KEY_P2B_PAYMENT_REQUEST_DENY, mapEventParams, true);
                                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                        .goToHome(this, false, false, false);
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showError(result.getStatusCode());
                            }
                        }
                    }, bcmPaymentRequest, "", SessionManager.getInstance().getSessionToken());
        } else {

            mapEventParams.put(PARAM_PAYMENT_REQUEST_ID, paymentData != null ? paymentData.getPaymentId() : null);

            ConsumerPaymentData consumerPaymentData = (ConsumerPaymentData) paymentData;

            BCMPaymentRequest bcmPaymentRequest = new BCMPaymentRequest();
            bcmPaymentRequest.setPaymentId(consumerPaymentData.getPaymentId());
            bcmPaymentRequest.setMsisdn(consumerPaymentData.getDisplayData().getPhoneNumber());

            sdk.doDenyPaymentRequestP2P(
                    this, result -> {
                        LoaderHelper.dismissLoader();
                        if (result != null) {
                            if (result.isSuccess()) {
                                if (result.getResult().getPaymentState() == PaymentStateType.EXECUTED) {
                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                                            this, KEY_P2P_PAYMENT_REQUEST_DENY, mapEventParams, true);
                                    if (isFromNotification) {
                                        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                    }
                                    BCMReturnHomeCallback.getInstance().getReturnHomeListener().goToHome(this, false, false, false);
                                } else {
                                    showError(P2P_REQUEST_DENY_FAILED);
                                }
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showError(result.getStatusCode());
                            }
                        }
                    },
                    bcmPaymentRequest,
                    false, SessionManager.getInstance().getSessionToken());
        }

    }

    @Override
    public void onBackPressed() {
        cancelPreAuthorization();
        if (isFromNotification) {
            super.onBackPressed();
        } else {
            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                    .goToHome(this, false, false, false);
        }
    }

    public void cancelPreAuthorization() {
        if (canCancelPreAuthorization) {
            BancomatSdkInterface.Factory.getInstance().doCancelPreauthorization(
                    this, ignore -> {
                        //Ignore result
                    },
                    paymentData.getPaymentId(),
                    paymentData.getPaymentRequestId(),
                    String.valueOf(paymentData.getCentsAmount()),
                    paymentData.getDisplayData().getPhoneNumber(),
                    SessionManager.getInstance().getSessionToken());
        }
    }

    @Override
    public void goToHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked, boolean returnToIntro) {
        PaymentFlowManager.goToHome(activity, finishSdkFlow);
    }

    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
    }

}