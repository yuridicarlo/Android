package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.model.BCMOperation;
import it.bancomatpay.sdk.manager.model.BCMOperationAuthorization;
import it.bancomatpay.sdk.manager.model.BCMPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.PaymentStateType;
import it.bancomatpay.sdk.manager.network.dto.response.DtoSendPaymentRequestResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.OnProgressResultListener;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BCMAuthenticationCallback;
import it.bancomatpay.sdkui.BCMReturnHomeCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.BancomatFullStackSdkInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager;
import it.bancomatpay.sdkui.databinding.ActivityPaymentResultBinding;
import it.bancomatpay.sdkui.events.HomeSectionEvent;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.ErrorMapper;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.GoToHomeInterface;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsMerchantLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogMerchantLocation;

import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Http.GENERIC_ERROR;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2B_DAILY_THRESHOLD_REACHED;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_AMOUNT_NOT_VALID;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_BENEFICIARY_NOT_ENABLED_TO_PAYMENT_REQUEST;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_CONTACT_NOT_FOUND;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_DENARO_BANK_SERVICE_NOT_AVAILABLE;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_DENARO_NOT_VALID_BENEFICIARY;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_DENARO_ONE_BENEFICIARY_NOT_VALID;
import static it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode.Server.P2P_FAILED_INVIA_RICHIESTA_NO_VALID_SERVICE_FOUND;
import static it.bancomatpay.sdkui.activities.SaveContactNumberActivity.CONTACT_NAME;
import static it.bancomatpay.sdkui.activities.SaveContactNumberActivity.CONTACT_NUMBER;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.MULTIMEDIA_DISCLOSURE;
import static it.bancomatpay.sdkui.activities.disclosure.PermissionFlowManager.REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.REQUEST_CODE_SAVE_CONTACT;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.SHOP_LOCATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_PAYMENT_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_SHARE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_PAYMENT_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_REQUEST_FAILED_SHARE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_SAVE_NUMBER;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_SHARE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_ELAPSED;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_STATUS;

public class ResultPaymentActivity extends GenericErrorActivity implements GoToHomeInterface, PlacesClientUtil.MerchantImageLoadingListener {

    private static final int REQUEST_PERMISSION_STORAGE = 6000;

    private ActivityPaymentResultBinding binding;

    private ShopsDataMerchant merchantItem;
    AbstractPaymentData paymentData;
    PaymentData.State paymentResult;
    String hiddenNameResult;
    boolean isSendMoney;
    boolean isP2B = false;
    boolean isPreauthorization = false;

    private boolean isFromDeepLink = false;
    private boolean isFromNotification = false;
    private boolean isNoBcmPayMoneyRequest = false;

    private boolean showWarning = false;


    ActivityResultLauncher<Intent> activityResultLauncherMultimedia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT,result.getResultCode(),data);
            });

    ActivityResultLauncher<Intent> activityResultLauncherSaveContact = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                manageResult(REQUEST_CODE_SAVE_CONTACT,result.getResultCode(),data);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(ResultPaymentActivity.class.getSimpleName());
        binding = ActivityPaymentResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (BCMReturnHomeCallback.getInstance().getReturnHomeListener() == null) {
            BancomatFullStackSdk.getInstance().setReturnHomeListener(this);
        }

        manageToolbar();
        initTextSwitcher();

        binding.loadingImgBackground.setVisibility(View.VISIBLE);

        isFromDeepLink = getIntent().getBooleanExtra(PaymentFlowManager.PAYMENT_FROM_DEEP_LINK, false);
        isFromNotification = getIntent().getBooleanExtra(PaymentFlowManager.IS_FROM_NOTIFICATION, false);
        isSendMoney = getIntent().getBooleanExtra(PaymentFlowManager.OPERATION_TYPE, true);
        paymentData = (AbstractPaymentData) getIntent().getSerializableExtra(PaymentFlowManager.PAYMENT_DATA);
        hiddenNameResult = (String) getIntent().getSerializableExtra(PaymentFlowManager.HIDDEN_NAME);

        if (paymentData instanceof MerchantPaymentData || paymentData.getDisplayData().getItemInterface() instanceof ShopItem
                || paymentData.getDisplayData().getItemInterface() instanceof ShopsDataMerchant) {
            isP2B = true;
            isPreauthorization = paymentData.getType() == PaymentItem.EPaymentRequestType.PREAUTHORIZATION;
            if (isPreauthorization) {
                binding.textAmountLabel.setText(getString(R.string.payment_preauthorization_max_amount));
            }
        }

        initLayout(paymentData, isSendMoney, isP2B);

        if (isSendMoney) {
            sendMoneyTask(paymentData, isP2B);
        } else {
            getMoneyTask(paymentData);
        }

        showWarning = true;

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    private void manageToolbar() {
        binding.toolbarSimple.setCenterImageVisibility(true);
        binding.toolbarSimple.setRightImageVisibility(false);
        binding.toolbarSimple.setLeftImageVisibility(false);
        binding.toolbarSimple.setOnClickRightImageListener((v) -> {
            if (hasPermissionStorage() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                shareAction();
            } else {
                requestPermissionStorage();
            }
        });
    }

    private void showPermissionRequestDialog() {
        AlertDialogBuilderExtended builder = new AlertDialogBuilderExtended(this);
        builder.setTitle(R.string.dialog_request_contact_permission_title)
                .setCancelable(false)
                .setMessage(R.string.dialog_request_contact_permission_desc)
                .setPositiveButton(R.string.contact_fragment_no_consent_button, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> finish());
        builder.showDialog(this);
    }


    private boolean hasPermissionStorage() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionStorage() {
        if (!FullStackSdkDataManager.getInstance().isInAppDisclosureAlreadyShown(MULTIMEDIA_DISCLOSURE)) {
            PermissionFlowManager.goToMultimediaDisclosure(this, activityResultLauncherMultimedia);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareAction();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    protected void manageResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SHOW_MULTIMEDIA_CONSENT) {
            if (resultCode == RESULT_OK) {
                shareAction();
            }
        } else if (requestCode == REQUEST_CODE_SAVE_CONTACT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String contactName = data.getStringExtra(CONTACT_NAME);
                    String contactNumber = data.getStringExtra(CONTACT_NUMBER);
                    String initialLetters = getNameInitialLetters(contactName);
                    AnimationFadeUtil.startFadeInAnimationV1(binding.contactConsumerName, AnimationFadeUtil.DEFAULT_DURATION);
                    AnimationFadeUtil.startFadeInAnimationV1(binding.contactConsumerNumber, AnimationFadeUtil.DEFAULT_DURATION);
                    AnimationFadeUtil.startFadeInAnimationV1(binding.contactConsumerLetter, AnimationFadeUtil.DEFAULT_DURATION);
                    binding.contactConsumerName.setText(contactName);
                    binding.contactConsumerNumber.setText(contactNumber);
                    if (!TextUtils.isEmpty(initialLetters)) {
                        binding.contactConsumerLetter.setText(getNameInitialLetters(contactName));
                        binding.contactConsumerImageProfile.setImageResource(R.drawable.profile_letter_circle_background);
                    }
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.contactHiddenName, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                    AnimationFadeUtil.startFadeOutAnimationV1(binding.saveContactButton, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);
                }
            }
        }
    }







    private String getNameInitialLetters(String contactName) {
        if (contactName.contains(" ") && getTitle().length() > 1) {
            String first = contactName.substring(0, 1);
            String[] nameList = contactName.split(" ");
            if (nameList.length >= 2) {
                if (!TextUtils.isEmpty(nameList[0]) &&
                        !TextUtils.isEmpty(first) || !first.equals(" ")) {
                    first = nameList[0].substring(0, 1);
                }
                if (TextUtils.isEmpty(nameList[1])) {
                    return first.toUpperCase();
                } else if (nameList[1].length() >= 1) {
                    return first.toUpperCase() + nameList[1].substring(0, 1).toUpperCase();
                }
            } else if (nameList.length == 1) {
                return first.toUpperCase();
            }
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }


    private void initLayout(AbstractPaymentData paymentData, boolean isSendMoney, boolean isP2B) {

        if (isP2B) {

            binding.contactConsumerBox.setVisibility(View.GONE);
            binding.layoutMerchantBox.setVisibility(View.VISIBLE);

            binding.textMerchantName.setText(paymentData.getDisplayData().getTitle());
            binding.textMerchantAddress.setText(paymentData.getDisplayData().getDescription());
            binding.resultOkKo.setVisibility(View.INVISIBLE);
            AnimationFadeUtil.startFadeInAnimationV1(binding.resultOkKo, 200);

        } else {

            binding.contactConsumerBox.setVisibility(View.VISIBLE);
            binding.layoutMerchantBox.setVisibility(View.GONE);

            binding.layoutMerchantImage.setVisibility(View.INVISIBLE);

            if (paymentData.getDisplayData().getBitmap() != null) {
                if (TextUtils.isEmpty(paymentData.getDisplayData().getDescription()) && paymentData.getDisplayData().getItemInterface().getType() == ItemInterface.Type.NONE) {
                    binding.contactConsumerImageProfile.setImageResource(R.drawable.placeholder_contact);
                } else {
                    binding.contactConsumerImageProfile.setImageBitmap(paymentData.getDisplayData().getBitmap());
                }
                binding.contactConsumerLetter.setVisibility(View.GONE);
            } else {
                binding.contactConsumerLetter.setVisibility(View.VISIBLE);
                binding.contactConsumerLetter.setText(paymentData.getDisplayData().getInitials());
            }
            if (!TextUtils.isEmpty(hiddenNameResult)) {
                binding.contactHiddenName.setVisibility(View.VISIBLE);
                binding.contactHiddenName.setText(hiddenNameResult);
            }
            if (!paymentData.getDisplayData().showBancomatLogo()) {
                binding.contactConsumerIsActive.setVisibility(View.INVISIBLE);
            } else {
                binding.contactConsumerIsActive.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(paymentData.getDisplayData().getTitle())) {
                binding.contactConsumerName.setVisibility(View.GONE);
            } else {
                binding.contactConsumerName.setText(paymentData.getDisplayData().getTitle());
            }
            if (!TextUtils.isEmpty(paymentData.getDisplayData().getDescription())
                    && !paymentData.getDisplayData().getTitle().equals(paymentData.getDisplayData().getDescription())) {
                binding.contactConsumerNumber.setText(paymentData.getDisplayData().getPhoneNumber());
            } else {
                showSaveContactBtn();
                binding.contactConsumerNumber.setVisibility(View.GONE);
            }
        }
        if (paymentData.getAmount() != null) {
            binding.textAmount.setText(StringUtils.getFormattedValue(paymentData.getAmount()));
            if (paymentData.getFee() != null && paymentData.getFee().doubleValue() > 0) {
                binding.textFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(paymentData.getFee())));
                binding.textFee.setVisibility(View.VISIBLE);
            }

            if (paymentData.isQrCodeEmpsa() && (paymentData.getLocalAmount() != null)) {
                binding.textLocalAmountAndCurrency.setText("(" + StringUtils.getFormattedValue(paymentData.getLocalAmount(), paymentData.getLocalCurrency()) + ")");
            }
            else {
                binding.textLocalAmountAndCurrency.setVisibility(View.GONE);
            }
        }

        if (isP2B) {
            binding.resultTextSwitcher.setText(getString(R.string.p2b_result_pending));
            binding.layoutMerchantImage.setVisibility(View.GONE);
            // imageResult.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_recap));
            binding.resultOkKo.setVisibility(View.GONE);

        } else if (isSendMoney) {
            binding.resultTextSwitcher.setText(getString(R.string.send_money_loading_message));
            binding.resultOkKo.setVisibility(View.INVISIBLE);
            // imageResult.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_recap));
        } else {
            binding.resultTextSwitcher.setVisibility(View.INVISIBLE);
        }
        binding.fabMerchantLocation.setOnClickListener(new CustomOnClickListener(c -> clickFabMerchantLocation()));
    }

    private void showSaveContactBtn() {
        AnimationFadeUtil.startFadeInAnimationV1(binding.saveContactButton, AnimationFadeUtil.DEFAULT_DURATION);
        binding.saveContactButton.setOnClickListener(new CustomOnClickListener(v -> {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_SAVE_NUMBER, null, true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                showPermissionRequestDialog();
            } else
                PaymentFlowManager.goToSaveContact(this, paymentData.getDisplayData().getPhoneNumber(), activityResultLauncherSaveContact);
        }
        ));
    }


    private void initTextSwitcher() {

        binding.resultTextSwitcher.setFactory(() -> new TextView(
                new ContextThemeWrapper(this, R.style.AmountLabel), null, 0));

        Animation inAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation outAnim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        inAnim.setDuration(200);
        outAnim.setDuration(200);

        binding.resultTextSwitcher.setInAnimation(inAnim);
        binding.resultTextSwitcher.setOutAnimation(outAnim);

        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, 200);

    }

    public void sendMoneyTask(AbstractPaymentData abstractPaymentData, boolean isP2B) {

        OnCompleteResultListener<PaymentData> listener = result -> {

            showWarning = false;

            if (result != null) {
                if (result.isSuccess()) {
                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_STATUS,
                            result.getResult().getState() != null ? result.getResult().getState().toString() : null);
                    mapEventParams.put(PARAM_ELAPSED, isP2B ? CjUtils.getInstance().getP2BPaymentTimeElapsed() : CjUtils.getInstance().getP2PPaymentTimeElapsed());
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(ResultPaymentActivity.this,
                            isP2B ? KEY_P2B_PAYMENT_COMPLETED : KEY_P2P_PAYMENT_COMPLETED,
                            mapEventParams, true);

                    resultStatus(result, isP2B);
                } else if (result.isSessionExpired()) {
                    setCjPaymentCompletedFail();
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showError(result.getStatusCode());
                    setCjPaymentCompletedFail();
                    setStatusFail(isP2B);
                }
            } else {
                setStatusFail(isP2B);
            }

            binding.homeButton.post(() ->
                    AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION));
            binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                if (!isP2B) {
                    if (paymentData.isRequestPayment()) {
                        if (isFromNotification) {
                            EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                    .goToHome(ResultPaymentActivity.this, false, false, false);
                        } else {
                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                    .goToHome(this, false, false, false);
                        }

                    } else
                        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                }
                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                        .goToHome(this, false, false, false);
            }));

        };

        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
        ItemInterface itemInterface = abstractPaymentData.getDisplayData().getItemInterface();

        if (abstractPaymentData instanceof ConsumerPaymentData) {
            ConsumerPaymentData consumerPaymentData = (ConsumerPaymentData) abstractPaymentData;

            String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();

            BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
            bcmPaymentAuthorization.setPaymentId(consumerPaymentData.getPaymentId());
            bcmPaymentAuthorization.setOperation(AuthenticationOperationType.PAYMENT);
            bcmPaymentAuthorization.setAmount(String.valueOf(consumerPaymentData.getCentsAmount()));
            bcmPaymentAuthorization.setSender(userMsisdn);
            bcmPaymentAuthorization.setReceiver(itemInterface.getPhoneNumber());
            bcmPaymentAuthorization.setP2B(isP2B);

            if (consumerPaymentData.isRequestPayment()) {

                BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                        .authenticationNeededForDispositiveOperation(this,
                                bcmPaymentAuthorization, (authenticated, authToken, loyaltyToken, paymentId) -> {
                                    if (authenticated) {

                                        LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);

                                        if (!TextUtils.isEmpty(consumerPaymentData.getPaymentId()) && !TextUtils.isEmpty(paymentId)
                                                && consumerPaymentData.getPaymentId().equals(paymentId)) {

                                            BCMPaymentRequest bcmPaymentRequest = new BCMPaymentRequest();
                                            bcmPaymentRequest.setItemInterface(itemInterface);
                                            bcmPaymentRequest.setPaymentId(consumerPaymentData.getPaymentId());
                                            bcmPaymentRequest.setPaymentRequestId(consumerPaymentData.getPaymentRequestId());
                                            bcmPaymentRequest.setMsisdn(consumerPaymentData.getDisplayData().getPhoneNumber());
                                            bcmPaymentRequest.setAmount(consumerPaymentData.getCentsAmount());

                                            BancomatSdkInterface.Factory.getInstance()
                                                    .doConfirmPaymentRequestP2P(this, listener, bcmPaymentRequest,
                                                            SessionManager.getInstance().getSessionToken(), authToken);

                                        } else {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                                            setCjPaymentCompletedFail();
                                            setStatusFail(isP2B);
                                        }

                                    } else {
                                        setCjPaymentCompletedFail();
                                        setStatusFail(isP2B);

                                        binding.homeButton.post(() ->
                                                AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION));
                                        binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                            if (!isP2B) {
                                                EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                            }
                                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                    .goToHome(this, false, false, false);
                                        }));
                                    }
                                });

            } else {

                BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                        .authenticationNeededForDispositiveOperation(this,
                                bcmPaymentAuthorization, (authenticated, authToken, loyaltyToken, paymentId) -> {
                                    if (authenticated) {

                                        LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);

                                        if (!TextUtils.isEmpty(consumerPaymentData.getPaymentId()) && !TextUtils.isEmpty(paymentId)
                                                && consumerPaymentData.getPaymentId().equals(paymentId)) {

                                            BCMOperation bcmPayment = new BCMOperation();
                                            bcmPayment.setItemInterface(itemInterface);
                                            bcmPayment.setMsisdn(itemInterface.getPhoneNumber());
                                            bcmPayment.setPaymentId(consumerPaymentData.getPaymentId());
                                            bcmPayment.setPaymentRequestId(consumerPaymentData.getPaymentRequestId());
                                            bcmPayment.setAmount(consumerPaymentData.getCentsAmount());
                                            bcmPayment.setCausal(consumerPaymentData.getCausal());

                                            sdk.doConfirmPaymentP2P(this, listener::onComplete, bcmPayment,
                                                    SessionManager.getInstance().getSessionToken(), authToken);

                                        } else {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                                            setCjPaymentCompletedFail();
                                            setStatusFail(isP2B);
                                        }

                                    } else {
                                        setCjPaymentCompletedFail();
                                        setStatusFail(isP2B);

                                        binding.homeButton.post(() ->
                                                AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION));
                                        binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                            if (!isP2B) {
                                                EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                            }
                                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                    .goToHome(this, false, false, false);
                                        }));
                                    }
                                });

            }

        } else if (abstractPaymentData instanceof MerchantPaymentData) {

            MerchantPaymentData merchantPaymentData = (MerchantPaymentData) abstractPaymentData;

            String constraint =
                    merchantPaymentData.getDisplayData().getTitle()
                            + " "
                            + merchantPaymentData.getDisplayData().getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, constraint);

            String paymentIdMerchant;
            if (!TextUtils.isEmpty(merchantPaymentData.getPaymentId())) {
                paymentIdMerchant = merchantPaymentData.getPaymentId();
            } else {
                paymentIdMerchant = merchantPaymentData.getQrCodeId();
            }

            String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();

            BCMOperationAuthorization bcmPaymentAuthorization = new BCMOperationAuthorization();
            bcmPaymentAuthorization.setPaymentId(paymentIdMerchant);
            bcmPaymentAuthorization.setOperation(AuthenticationOperationType.PAYMENT);
            bcmPaymentAuthorization.setAmount(String.valueOf(merchantPaymentData.getCentsAmount()));
            bcmPaymentAuthorization.setSender(userMsisdn);
            bcmPaymentAuthorization.setReceiver(merchantPaymentData.getTag());
            bcmPaymentAuthorization.setP2B(isP2B);

            if (abstractPaymentData.isRequestPayment()) {

                BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                        .authenticationNeededForDispositiveOperation(this,
                                bcmPaymentAuthorization, (authenticated, authToken, loyaltyToken, paymentId) -> {
                                    if (authenticated) {

                                        LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);

                                        if (!TextUtils.isEmpty(paymentIdMerchant) && !TextUtils.isEmpty(paymentId)
                                                && paymentIdMerchant.equals(paymentId)) {

                                            BCMPaymentRequest bcmPaymentRequest = new BCMPaymentRequest();
                                            bcmPaymentRequest.setItemInterface(itemInterface);
                                            bcmPaymentRequest.setPaymentId(merchantPaymentData.getPaymentId());
                                            bcmPaymentRequest.setTag(merchantPaymentData.getTag());
                                            bcmPaymentRequest.setShopId(merchantPaymentData.getShopId());
                                            bcmPaymentRequest.setTillId(merchantPaymentData.getTillId());
                                            bcmPaymentRequest.setAmount(merchantPaymentData.getCentsAmount());
                                            bcmPaymentRequest.setCausal(merchantPaymentData.getCausal());

                                            sdk.doConfirmPaymentRequestP2B(this, listener,
                                                    bcmPaymentRequest,
                                                    SessionManager.getInstance().getSessionToken(),
                                                    authToken);

                                        } else {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                            setCjPaymentCompletedFail();
                                            setStatusFail(isP2B);
                                        }

                                    } else {
                                        setCjPaymentCompletedFail();
                                        setStatusFail(isP2B);

                                        AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                        binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                            if (!isP2B) {
                                                EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                            }
                                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                    .goToHome(this, false, false, false);
                                        }));
                                    }
                                });

            } else {

                BCMAuthenticationCallback.getInstance().getAuthenticationListener()
                        .authenticationNeededForDispositiveOperation(this,
                                bcmPaymentAuthorization, (authenticated, authToken, loyaltyToken, paymentId) -> {
                                    if (authenticated) {

                                        LoyaltyTokenManager.getInstance().setLoyaltyToken(loyaltyToken);

                                        if (!TextUtils.isEmpty(paymentIdMerchant) && !TextUtils.isEmpty(paymentId)
                                                && paymentIdMerchant.equals(paymentId)) {

                                            BCMOperation bcmPayment = new BCMOperation();
                                            bcmPayment.setItemInterface(itemInterface);
                                            bcmPayment.setPaymentId(merchantPaymentData.getPaymentId());
                                            bcmPayment.setTag(merchantPaymentData.getTag());
                                            bcmPayment.setShopId(merchantPaymentData.getShopId());
                                            bcmPayment.setTillId(merchantPaymentData.getTillId());
                                            bcmPayment.setAmount(merchantPaymentData.getCentsAmount());
                                            bcmPayment.setCausal(merchantPaymentData.getCausal());
                                            bcmPayment.setQrCodeId(merchantPaymentData.getQrCodeId());
                                            bcmPayment.setQrCodeEmpsa(merchantPaymentData.isQrCodeEmpsa());

                                            sdk.doConfirmPaymentP2B(
                                                    this, new OnProgressResultListener<PaymentData>() {
                                                        @Override
                                                        public void onProgress() {
                                                            PollingData pollingData = new PollingData();
                                                            pollingData.setPollingNeeded(false);
                                                        }

                                                        @Override
                                                        public void onComplete(Result<PaymentData> result) {

                                                            showWarning = false;

                                                            if (result != null) {

                                                                if (result.isSuccess()) {

                                                                    HashMap<String, String> mapEventParams = new HashMap<>();
                                                                    mapEventParams.put(PARAM_STATUS,
                                                                            result.getResult().getState() != null ? result.getResult().getState().toString() : null);
                                                                    mapEventParams.put(PARAM_ELAPSED, isP2B ? CjUtils.getInstance().getP2BPaymentTimeElapsed() : CjUtils.getInstance().getP2PPaymentTimeElapsed());
                                                                    CjUtils.getInstance().sendCustomerJourneyTagEvent(ResultPaymentActivity.this,
                                                                            isP2B ? KEY_P2B_PAYMENT_COMPLETED : KEY_P2P_PAYMENT_COMPLETED,
                                                                            mapEventParams, true);

                                                                    if (!TextUtils.isEmpty(merchantPaymentData.getQrCodeId())) {

                                                                        AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                                                        binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                                                            if (!isP2B) {
                                                                                EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                                                            }
                                                                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                                                    .goToHome(ResultPaymentActivity.this, false, false, false);
                                                                        }));

                                                                        //se qr mostro il result (dopo animazione)
                                                                        resultStatus(result, isP2B);
                                                                    } else {
                                                                        if (result.getResult().getState() == PaymentData.State.EXECUTED) {

                                                                            PollingData pollingData = result.getResult().getPollingData();
                                                                            pollingData.setPollingNeeded(true);

                                                                            BancomatSdkInterface.Factory.getInstance().doVerifyPaymentState(
                                                                                    ResultPaymentActivity.this, resultPolling -> {
                                                                                        if (resultPolling != null) {
                                                                                            if (resultPolling.isSuccess()) {

                                                                                                AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                                                                                binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                                                                                    if (!isP2B) {
                                                                                                        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                                                                                    }

                                                                                                    BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                                                                            .goToHome(ResultPaymentActivity.this, isFromDeepLink, false, false);
                                                                                                }));

                                                                                                resultStatus(resultPolling, isP2B);
                                                                                            } else if (resultPolling.isSessionExpired()) {
                                                                                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                                                                                        .onAbortSession(ResultPaymentActivity.this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                                                                            } else {

                                                                                                AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                                                                                binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                                                                                    if (!isP2B) {
                                                                                                        EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                                                                                    }
                                                                                                    BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                                                                            .goToHome(ResultPaymentActivity.this, false, false, false);
                                                                                                }));

                                                                                                showError(resultPolling.getStatusCode());
                                                                                                setCjPaymentCompletedFail();
                                                                                                setStatusFail(isP2B);
                                                                                            }
                                                                                        } else {
                                                                                            showErrorAndDoAction(StatusCode.Http.NO_RETE, (dialog, which) -> {
                                                                                                lockGenericError = true; //Linea necessaria
                                                                                                finish();
                                                                                            });
                                                                                        }
                                                                                    },
                                                                                    abstractPaymentData.getPaymentId(),
                                                                                    pollingData,
                                                                                    SessionManager.getInstance().getSessionToken());

                                                                        } else {

                                                                            AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                                                            binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                                                                if (!isP2B) {
                                                                                    EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                                                                }
                                                                                BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                                                        .goToHome(ResultPaymentActivity.this, false, false, false);
                                                                            }));

                                                                            resultStatus(result, isP2B);
                                                                        }
                                                                    }
                                                                } else if (result.isSessionExpired()) {
                                                                    setCjPaymentCompletedFail();
                                                                    BCMAbortCallback.getInstance().getAuthenticationListener()
                                                                            .onAbortSession(ResultPaymentActivity.this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                                                } else if (result.getStatusCode().equals(P2B_DAILY_THRESHOLD_REACHED)) {
                                                                    showError(result.getStatusCode());
                                                                    setCjPaymentCompletedFail();
                                                                    setStatusFail(true);
                                                                    AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                                                    binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                                                        if (!isP2B) {
                                                                            EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                                                        }
                                                                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                                                .goToHome(ResultPaymentActivity.this, false, false, false);
                                                                    }));
                                                                } else {
                                                                    showError(result.getStatusCode());
                                                                    setCjPaymentCompletedFail();
                                                                    setStatusFail(true);

                                                                    AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                                                    binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                                                        if (!isP2B) {
                                                                            EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                                                        }
                                                                        BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                                                .goToHome(ResultPaymentActivity.this, false, false, false);
                                                                    }));

                                                                }

                                                            } else {
                                                                setCjPaymentCompletedFail();
                                                                showErrorAndDoAction(StatusCode.Http.NO_RETE, (dialog, which) -> {
                                                                    lockGenericError = true; //Linea necessaria
                                                                    finish();
                                                                });
                                                            }
                                                        }
                                                    },
                                                    bcmPayment,
                                                    SessionManager.getInstance().getSessionToken(),
                                                    authToken);

                                        } else {
                                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                                    .onAbort(BancomatFullStackSdkInterface.EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                                            setCjPaymentCompletedFail();
                                            setStatusFail(isP2B);
                                        }

                                    } else {
                                        setCjPaymentCompletedFail();
                                        setStatusFail(isP2B);

                                        AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                                        binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                                            if (!isP2B) {
                                                EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                                            }
                                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                                    .goToHome(this, false, false, false);
                                        }));
                                    }
                                });

            }

        }
    }

    public void getMoneyTask(AbstractPaymentData paymentData) {
        isSendMoney = false;

        List<String> msisdnBeneficiary = new ArrayList<>();
        msisdnBeneficiary.add(paymentData.getDisplayData().getPhoneNumber());
        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
        sdk.doSendPaymentRequest(
                this, result -> {

                    showWarning = false;

                    if (result != null) {
                        AnimationFadeUtil.startFadeInAnimationV1(binding.homeButton, AnimationFadeUtil.DEFAULT_DURATION);
                        binding.loadingImgBackground.setVisibility(View.GONE);
                        binding.homeButton.setOnClickListener(new CustomOnClickListener(v -> {
                            if (!isP2B) {
                                EventBus.getDefault().postSticky(new HomeSectionEvent(HomeSectionEvent.Type.HOME));
                            }
                            BCMReturnHomeCallback.getInstance().getReturnHomeListener()
                                    .goToHome(this, false, false, false);
                        }));
                        if (result.isSuccess()) {

                            HashMap<String, String> mapEventParams = new HashMap<>();
                            mapEventParams.put(PARAM_STATUS,
                                    result.getResult().getPaymentState() != null ? result.getResult().getPaymentState().toString() : null);
                            mapEventParams.put(PARAM_ELAPSED, isP2B ? CjUtils.getInstance().getP2BPaymentTimeElapsed() : CjUtils.getInstance().getP2PPaymentTimeElapsed());
                            CjUtils.getInstance().sendCustomerJourneyTagEvent(ResultPaymentActivity.this,
                                    isP2B ? KEY_P2B_PAYMENT_COMPLETED : KEY_P2P_PAYMENT_COMPLETED,
                                    mapEventParams, true);

                            if (result.getResult().getPaymentState() == DtoSendPaymentRequestResponse.PaymentStateType.SENT) {
                                setStatusSuccess(isSendMoney, isP2B, isPreauthorization);
                            } else if (result.getResult().getPaymentState() == DtoSendPaymentRequestResponse.PaymentStateType.FAILED) {
                                if (isSendMoney) {
                                    setStatusFail(isP2B);
                                } else setPaymentRequestStatusFail(result.getStatusCode());
                            }

                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            setCjPaymentCompletedFail();
                        } else {
                            setCjPaymentCompletedFail();
                            if (isSendMoney) {
                                setStatusFail(isP2B);
                            } else
                                setPaymentRequestStatusFail(result.getStatusCode());
                        }
                    } else {
                        setCjPaymentCompletedFail();
                        showErrorAndDoAction(GENERIC_ERROR, (dialog, which) -> finish());
                    }
                },
                msisdnBeneficiary,
                String.valueOf(paymentData.getCentsAmount()),
                paymentData.getCausal(),
                paymentData.getDisplayData().getItemInterface(),
                SessionManager.getInstance().getSessionToken());

    }

    private void resultStatus(Result<PaymentData> result, boolean isP2B) {
        if (result.isSuccess()) {
            if (result.getResult().getState() == PaymentData.State.PENDING) {
                paymentResult = PaymentData.State.PENDING;
                setStatusPending(isSendMoney);
            } else if (result.getResult().getState() == PaymentData.State.AUTHORIZED) {
                paymentResult = PaymentData.State.AUTHORIZED;
                setStatusAuthorized();
            } else if (result.getResult().getState() == PaymentData.State.EXECUTED) {
                paymentResult = PaymentData.State.EXECUTED;
                setStatusSuccess(isSendMoney, isP2B, isPreauthorization);
            } else if (result.getResult().getState() == PaymentData.State.FAILED) {
                paymentResult = PaymentData.State.FAILED;
                setStatusFail(isP2B);
            } else {
                paymentResult = PaymentData.State.AUTHORIZED;
                setStatusAuthorized();
            }
        } else if (result.isSessionExpired()) {
            setCjPaymentCompletedFail();
            BCMAbortCallback.getInstance().getAuthenticationListener()
                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
        } else {
            setCjPaymentCompletedFail();
            setStatusFail(isP2B);
        }
    }

    private void setStatusFail(boolean isP2B) {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.loadingImgBackground.setVisibility(View.GONE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultOkKo, 200);

        if (isP2B) {
            binding.resultTextSwitcher.setText(getString(R.string.p2b_result_ko));
            BcmLocation shopLocation = (BcmLocation) getIntent().getSerializableExtra(SHOP_LOCATION);

            // Try to check location coordinates in PaymentItem data
            if (shopLocation == null) {
                if (paymentData instanceof MerchantQrPaymentData) {
                    PaymentItem paymentItem = ((MerchantQrPaymentData) paymentData).getPaymentItem();

                    if (paymentItem.getLatitude() != null && paymentItem.getLongitude() != null) {
                        shopLocation = new BcmLocation();
                    }
                }
            }

            if (shopLocation != null) {
                binding.fabMerchantLocation.show();
            }
        } else {
            binding.resultTextSwitcher.setText(getString(R.string.p2p_result_ko));
        }

        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, AnimationFadeUtil.DEFAULT_DURATION);

        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);

        if (binding.toolbarSimple.isRightImageVisible()) {
            binding.toolbarSimple.setRightImageVisibility(false);
        }

        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ko));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);

        showWarning = false;
    }

    private void setPaymentRequestStatusFail(StatusCodeInterface statusCode) {
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.loadingImgBackground.setVisibility(View.GONE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultOkKo, 200);
        int idString = ErrorMapper.getStringFromStatusCode(statusCode);
        if (statusCode.equals(P2P_FAILED_INVIA_RICHIESTA_DENARO_BANK_SERVICE_NOT_AVAILABLE)) {
            isNoBcmPayMoneyRequest = true;
            binding.resultTextSwitcher.setText(getString(R.string.request_money_error_not_bmcpay_receiver));
            binding.toolbarSimple.setRightImageVisibility(true);
        } else if (statusCode.equals(P2P_FAILED_INVIA_RICHIESTA_DENARO_NOT_VALID_BENEFICIARY) || statusCode.equals(P2P_FAILED_AMOUNT_NOT_VALID) || statusCode.equals(P2P_FAILED_INVIA_RICHIESTA_DENARO_ONE_BENEFICIARY_NOT_VALID) || statusCode.equals(P2P_FAILED_INVIA_RICHIESTA_BENEFICIARY_NOT_ENABLED_TO_PAYMENT_REQUEST) || statusCode.equals(P2P_FAILED_INVIA_RICHIESTA_CONTACT_NOT_FOUND) || statusCode.equals(P2P_FAILED_INVIA_RICHIESTA_NO_VALID_SERVICE_FOUND)) {
            isNoBcmPayMoneyRequest = false;
            binding.resultTextSwitcher.setText(getString(idString));
            binding.toolbarSimple.setRightImageVisibility(false);
        } else {
            isNoBcmPayMoneyRequest = false;
            binding.resultTextSwitcher.setText(getString(R.string.get_money_result_ko));
            binding.toolbarSimple.setRightImageVisibility(false);
        }

        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, AnimationFadeUtil.DEFAULT_DURATION);

        binding.resultAnimation.setAnimation(getString(R.string.check_ko));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);

        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ko));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);

        showWarning = false;
    }

    private void setStatusSuccess(boolean isSendMoney, boolean isP2B, boolean isPreauthorization) {
        binding.loadingImgBackground.setVisibility(View.GONE);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultOkKo, 200);
        if (isSendMoney) {
            if (isP2B) {
                if (isPreauthorization) {
                    binding.resultTextSwitcher.setText(getString(R.string.p2b_operation_confirmed));
                    AnimationFadeUtil.startFadeInAnimationV1(binding.textPreauthorizationNotificationLabel, AnimationFadeUtil.DEFAULT_DURATION);
                } else {
                    binding.resultTextSwitcher.setText(getString(R.string.p2b_result_ok));
                }

                BcmLocation shopLocation = (BcmLocation) getIntent().getSerializableExtra(SHOP_LOCATION);

                // Try to check location coordinates in PaymentItem data
                if (shopLocation == null) {
                    if (paymentData instanceof MerchantQrPaymentData) {
                        PaymentItem paymentItem = ((MerchantQrPaymentData) paymentData).getPaymentItem();

                        if (paymentItem.getLatitude() != null && paymentItem.getLongitude() != null) {
                            shopLocation = new BcmLocation();
                        }
                    }
                }

                if (shopLocation != null) {
                    binding.fabMerchantLocation.show();
                }
            } else {
                binding.resultTextSwitcher.setText(getString(R.string.p2p_result_ok));
            }
        } else {
            binding.resultTextSwitcher.setText(getString(R.string.get_money_result_ok));
        }
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultTextSwitcher, AnimationFadeUtil.DEFAULT_DURATION);

        binding.resultAnimation.setAnimation(getString(R.string.check_ok));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);

        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ok));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);
        if (!binding.toolbarSimple.isRightImageVisible() && !isPreauthorization) {
            binding.toolbarSimple.setRightImageVisibility(true);
        }

        showWarning = false;
    }

    private void setStatusAuthorized() {
        binding.loadingImgBackground.setVisibility(View.GONE);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultOkKo.setVisibility(View.INVISIBLE);
        binding.resultTextSwitcher.setText(getString(R.string.authorized_info_message));
        binding.resultAnimation.setAnimation(getString(R.string.check_presa_in_carico));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ok));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);

        if (!binding.toolbarSimple.isRightImageVisible()) {
            binding.toolbarSimple.setRightImageVisibility(false);
        }
        showWarning = false;

    }

    private void setStatusPending(boolean isSendMoney) {
        binding.loadingImgBackground.setVisibility(View.GONE);
        AnimationFadeUtil.startFadeOutAnimationV1(binding.resultLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);
        binding.resultTextSwitcher.setText(getString(R.string.invite_new_bcm_user));
        binding.resultOkKo.setVisibility(View.INVISIBLE);

        binding.resultAnimation.setAnimation(getString(R.string.check_attesa));
        AnimationFadeUtil.startFadeInAnimationV1(binding.resultAnimation, AnimationFadeUtil.DEFAULT_DURATION);
        binding.animationResultP2p.setAnimation(getString(R.string.animation_p2p_result_ok));
        binding.animationResultP2p.playAnimation();
        AnimationFadeUtil.startFadeInAnimationV1(binding.animationResultP2p, 200);

        if (!binding.toolbarSimple.isRightImageVisible() && isSendMoney && !isPreauthorization) {
            binding.toolbarSimple.setRightImageVisibility(true);
        }

        showWarning = false;
    }

    private void shareAction() {
        String message = "";
        String price = StringUtils.getFormattedValue(paymentData.getAmount());

        if (isSendMoney) {
            if (paymentResult != null) {
                if (PaymentData.State.EXECUTED == paymentResult) {
                    if (TextUtils.isEmpty(paymentData.getCausal())) {
                        if (isP2B) {
                            message = getString(R.string.movements_outcome_fragment_share_success_p2b, price);
                        } else {
                            message = getString(R.string.movements_outcome_fragment_share_success_p2p_empty, price);
                        }
                    } else {
                        if (isP2B) {
                            message = getString(R.string.movements_outcome_fragment_share_success_p2b, price);
                        } else {
                            message = getString(R.string.movements_outcome_fragment_share_success_p2p, price, paymentData.getCausal());
                        }
                    }
                } else if (PaymentData.State.PENDING == paymentResult) {
                    if (TextUtils.isEmpty(paymentData.getCausal())) {
                        message = getString(R.string.movements_outcome_fragment_share_pending_empty, price);
                    } else {
                        message = getString(R.string.movements_outcome_fragment_share_pending, price, paymentData.getCausal());
                    }
                }
            }

        } else {
            if (isNoBcmPayMoneyRequest) {
                message = getString(R.string.request_money_error_not_bmcpay_receiver_share_msg, price);
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_REQUEST_FAILED_SHARE, null, true);
            } else {
                if (TextUtils.isEmpty(paymentData.getCausal())) {
                    message = getString(R.string.get_money_share_success_p2p_empty, price);
                } else {
                    message = getString(R.string.get_money_share_success_p2p, price, paymentData.getCausal());
                }
            }


        }

        if (isSendMoney) {
            if (isP2B) {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_SHARE, null, true);
            } else {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_SHARE, null, true);
            }
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getResources().getString(R.string.share));
        startActivity(shareIntent);
    }

    private void clickFabMerchantLocation() {

        if (merchantItem == null) {

            LoaderHelper.showLoader(this);

            String merchantTag = "";
            if (paymentData instanceof MerchantPaymentData) {
                merchantTag = ((MerchantPaymentData) paymentData).getTag();
            }

            BancomatSdkInterface.Factory.getInstance().doGetMerchantDetail(this, result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                merchantItem = new ShopsDataMerchant(result.getResult());
                                showBottomDialog(merchantItem);
                            } else if (result.isSessionExpired()) {
                                BCMAbortCallback.getInstance().getAuthenticationListener()
                                        .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                            } else {
                                showError(result.getStatusCode());
                            }
                        }
                    },
                    merchantTag,
                    String.valueOf(paymentData.getDisplayData().getItemInterface().getId()),
                    SessionManager.getInstance().getSessionToken());

        } else {
            showBottomDialog(merchantItem);
        }

    }


    private void showBottomDialog(ShopsDataMerchant merchantItem) {
        if (BancomatFullStackSdk.getInstance().hasHuaweiServices()) {
            BottomDialogHmsMerchantLocation hmsBottomDialog = new BottomDialogHmsMerchantLocation(this, merchantItem);
            if (!hmsBottomDialog.isVisible()) {
                hmsBottomDialog.showDialog();
            }
        } else if (BancomatFullStackSdk.getInstance().hasGooglePlayServices()) {
            BottomDialogMerchantLocation bottomDialog = new BottomDialogMerchantLocation(this, merchantItem);
            if (!bottomDialog.isVisible()) {
                bottomDialog.showDialog();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (showWarning) {
            showWarningDialog();
        } else {
            BCMReturnHomeCallback.getInstance().getReturnHomeListener().goToHome(this, false, false, false);
        }
    }

    private void setCjPaymentCompletedFail() {
        HashMap<String, String> mapEventParams = new HashMap<>();
        mapEventParams.put(PARAM_STATUS,
                PaymentStateType.FAILED.toString());
        mapEventParams.put(PARAM_ELAPSED, isP2B ? CjUtils.getInstance().getP2BPaymentTimeElapsed() : CjUtils.getInstance().getP2PPaymentTimeElapsed());
        CjUtils.getInstance().sendCustomerJourneyTagEvent(ResultPaymentActivity.this,
                isP2B ? KEY_P2B_PAYMENT_COMPLETED : KEY_P2P_PAYMENT_COMPLETED,
                mapEventParams, true);
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title)
                .setMessage(R.string.error_back_on_send_money)
                .setPositiveButton(R.string.ok, (dialog, id) -> finish())
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false);
        if (!isSendMoney) {
            builder.setMessage(R.string.error_back_on_request_money);
        }
        builder.show();
    }

    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
    }

    @Override
    public void goToHome(Activity activity, boolean finishSdkFlow, boolean pinBlocked, boolean returnToIntro) {
        PaymentFlowManager.goToHome(activity, finishSdkFlow);
    }

}

