package it.bancomatpay.sdkui.activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.BCMOperation;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ConfirmPaymentActivityBinding;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.StringUtils;

import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.SCROLLBARS_INSIDE_INSET;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.SHOP_LOCATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CONFIRM;

public class ConfirmPaymentActivity extends GenericErrorActivity {

    private ConfirmPaymentActivityBinding binding;

    AbstractPaymentData paymentData;
    private BcmLocation shopLocation;
    String hiddenNameResult;
    boolean isSendMoney;

    private boolean isFromDeepLink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(ConfirmPaymentActivity.class.getSimpleName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources()
                    .getColor(R.color.confirm_payment_statusbar_color));
        }


        binding = ConfirmPaymentActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        shopLocation = (BcmLocation) getIntent().getSerializableExtra(SHOP_LOCATION);
        isFromDeepLink = getIntent().getBooleanExtra(PaymentFlowManager.PAYMENT_FROM_DEEP_LINK, false);

      //  applyInsets();

        binding.toolbarSimple.setRightImageVisibility(false);
        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());

        binding.sendMoneyMessage.setMultiLineMode(
                3,
                EditorInfo.IME_FLAG_NO_ENTER_ACTION,
                false,
                OVER_SCROLL_ALWAYS,
                SCROLLBARS_INSIDE_INSET,
                true);

        binding.sendMoneyMessage.requestFocus();

        paymentData = (AbstractPaymentData) getIntent().getSerializableExtra(PaymentFlowManager.PAYMENT_DATA);
        isSendMoney = getIntent().getBooleanExtra(PaymentFlowManager.OPERATION_TYPE, true);
        initLayout(paymentData);
        if (isSendMoney) {
            goToSendMoney();
        }

    }


   /*  private void applyInsets() {
        int insetTop = BancomatDataManager.getInstance().getScreenInsetTop();


        binding.toolbarSimple.post(() -> {
            ConstraintLayout.LayoutParams spaceParams = (ConstraintLayout.LayoutParams) binding.toolbarSimple.getLayoutParams();
            if (insetTop != 0) {
                spaceParams.topMargin = insetTop;
            } else spaceParams.topMargin = 48;
            binding.toolbarSimple.setLayoutParams(spaceParams);
            binding.toolbarSimple.requestLayout();
        });


        int insetBottom = BancomatDataManager.getInstance().getScreenInsetBottom();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.mainLayout.getLayoutParams();

        binding.mainLayout.post(() ->
        {
            if (insetBottom != 0) {
                layoutParams.bottomMargin = insetBottom;
            } else layoutParams.bottomMargin = 80;
            binding.mainLayout.setLayoutParams(layoutParams);
        });

        if (!

               hasSoftKeys()) {
            layoutParams.setMargins(0, 0, 0, 0);
            binding.mainLayout.setLayoutParams(layoutParams);
        }
    }*/

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        binding.sendMoneyMessage.removeCustomOnFocusChangeListener();
        super.onDestroy();
    }

    private void goToSendMoney() {
        BancomatSdkInterface sdk = BancomatSdkInterface.Factory.getInstance();
        OnCompleteResultListener<VerifyPaymentData> listener = result -> {

            AnimationFadeUtil.startFadeOutAnimationV1(binding.resultCenterLoader, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE);

            if (result != null) {
                if (result.isSuccess()) {
                    if (!TextUtils.isEmpty(result.getResult().getContactName())) {
                        binding.contactHiddenName.setText(result.getResult().getContactName());
                        binding.expandableLayoutHiddenName.expand();
                    }

                    hiddenNameResult = result.getResult().getContactName();
                    paymentData.setFee(result.getResult().getFee());
                    paymentData.setPaymentId(result.getResult().getPaymentId());
                    if (paymentData.getFee() != null && paymentData.getFee().doubleValue() > 0) {
                        binding.textFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(paymentData.getFee())));
                        AnimationFadeUtil.startFadeInAnimationV1(binding.textFee, AnimationFadeUtil.DEFAULT_DURATION);
                    } else {
                        binding.textFee.setVisibility(View.INVISIBLE);
                    }

                    binding.confirmButton.setEnabled(true);
                    binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> {
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_CONFIRM, null, true);
                        paymentData.setCausal(binding.sendMoneyMessage.getText());
                        PaymentFlowManager.goToResultPayment(this, paymentData, shopLocation, hiddenNameResult, isSendMoney, isFromDeepLink);
                    }));
                } else if (result.isSessionExpired()) {
                    BCMAbortCallback.getInstance().getAuthenticationListener()
                            .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                } else {
                    showErrorAndDoAction(result.getStatusCode(), (dialog, which) -> finish());
                }
            }
        };

        BCMOperation bcmPayment = new BCMOperation();

        if (paymentData.getDisplayData().getItemInterface() instanceof ShopItem) {

            bcmPayment.setTag(((MerchantPaymentData) paymentData).getTag());
            bcmPayment.setShopId(((MerchantPaymentData) paymentData).getShopId());
            bcmPayment.setTillId(((MerchantPaymentData) paymentData).getTillId());
            bcmPayment.setAmount(paymentData.getCentsAmount());

            sdk.doVerifyPaymentP2B(this, listener, bcmPayment, SessionManager.getInstance().getSessionToken());

        } else if (paymentData.getDisplayData().getItemInterface() instanceof ShopsDataMerchant) {

            bcmPayment.setTag(((MerchantPaymentData) paymentData).getTag());
            bcmPayment.setShopId(((MerchantPaymentData) paymentData).getShopId());
            bcmPayment.setTillId(((MerchantPaymentData) paymentData).getTillId());
            bcmPayment.setAmount(paymentData.getCentsAmount());
            bcmPayment.setQrCodeId(((MerchantPaymentData) paymentData).getQrCodeId());
            bcmPayment.setCausal(paymentData.getCausal());

            sdk.doVerifyPaymentP2B(this, listener, bcmPayment, SessionManager.getInstance().getSessionToken());

        } else {

            bcmPayment.setMsisdn(paymentData.getDisplayData().getPhoneNumber());
            bcmPayment.setPaymentId(paymentData.getPaymentId());
            bcmPayment.setAmount(paymentData.getCentsAmount());
            bcmPayment.setCausal(paymentData.getCausal());

            sdk.doVerifyPaymentP2P(this, listener, bcmPayment, SessionManager.getInstance().getSessionToken());

        }
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

    private void initLayout(AbstractPaymentData paymentData) {
        if (paymentData.getDisplayData().getItemInterface() instanceof ShopItem || paymentData.getDisplayData().getItemInterface() instanceof ShopsDataMerchant) {

            binding.contactConsumerImageProfileCircle.setVisibility(View.GONE);
            binding.contactConsumerLetter.setVisibility(View.GONE);
            binding.contactConsumerIsActive.setVisibility(View.GONE);
            binding.contactConsumerText.setGravity(Gravity.CENTER);

            if (TextUtils.isEmpty(paymentData.getDisplayData().getTitle())) {
                binding.contactConsumerName.setVisibility(View.GONE);
            } else {
                binding.contactConsumerName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimensionPixelSize(R.dimen.size_25));
                binding.contactConsumerName.setText(paymentData.getDisplayData().getTitle());
            }
            if (TextUtils.isEmpty(paymentData.getDisplayData().getDescription())) {
                binding.contactConsumerNumber.setVisibility(View.GONE);
            } else {
                binding.contactConsumerNumber.setText(paymentData.getDisplayData().getDescription());
                binding.contactConsumerNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimensionPixelSize(R.dimen.size_20));
            }
        } else {
            if (isSendMoney) {
                binding.paymentTypeLabel.setVisibility(View.VISIBLE);
                binding.paymentTypeLabel.setText(R.string.sending_money_text);
            } else {
                binding.paymentTypeLabel.setVisibility(View.VISIBLE);
                binding.paymentTypeLabel.setText(R.string.request_money_text);
                String userMsisdn = BancomatDataManager.getInstance().getPrefixCountryCode() + BancomatDataManager.getInstance().getUserPhoneNumber();
                if (paymentData.getDisplayData().getPhoneNumber().contentEquals(userMsisdn)) {
                    binding.confirmButton.setEnabled(false);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> showErrorFromString(getString(R.string.common_p2p_error_receiver_not_valid), (dialog, which) -> finish()), 500);
                } else {
                    binding.confirmButton.setEnabled(true);
                    binding.confirmButton.setOnClickListener(new CustomOnClickListener(v -> {
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_CONFIRM, null, true);
                        paymentData.setCausal(binding.sendMoneyMessage.getText());
                        PaymentFlowManager.goToResultPayment(this, paymentData, hiddenNameResult, isSendMoney, isFromDeepLink, false);
                    }));
                    binding.resultCenterLoader.setVisibility(View.INVISIBLE);
                }
            }
            if (paymentData.getDisplayData().getBitmap() != null) {
                if (TextUtils.isEmpty(paymentData.getDisplayData().getDescription()) && paymentData.getDisplayData().getItemInterface().getType() == ItemInterface.Type.NONE) {
                    binding.contactConsumerImageProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_contact));
                    binding.contactConsumerImageProfile.setVisibility(View.VISIBLE);
                    binding.contactConsumerImageProfileCircle.setVisibility(View.INVISIBLE);
                } else {
                    binding.contactConsumerImageProfileCircle.setImageBitmap(paymentData.getDisplayData().getBitmap());
                    binding.contactConsumerImageProfile.setVisibility(View.INVISIBLE);
                    binding.contactConsumerImageProfileCircle.setVisibility(View.VISIBLE);
                }
                binding.contactConsumerLetter.setVisibility(View.GONE);

            } else {
                binding.contactConsumerImageProfile.setVisibility(View.VISIBLE);
                binding.contactConsumerImageProfileCircle.setVisibility(View.INVISIBLE);
                binding.contactConsumerLetter.setVisibility(View.VISIBLE);
                binding.contactConsumerLetter.setText(paymentData.getDisplayData().getInitials());
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
                binding.contactConsumerNumber.setVisibility(View.GONE);
            }
        }
        if (paymentData.getAmount() != null) {
            binding.totalAmount.setText(StringUtils.getFormattedValue(paymentData.getAmount()));
        }

        KeyboardVisibilityEvent.setEventListener(
                this, this, isOpen -> {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.confirmButton.getLayoutParams();
                    if (isOpen) {
                        binding.confirmButton.setBackgroundResource(R.drawable.button_square_background_state_list);
                        params.leftMargin = 0;
                        params.rightMargin = 0;
                        params.bottomMargin = 0;
                        binding.scrollViewConfirmPayment.postDelayed(() -> binding.scrollViewConfirmPayment.fullScroll(ScrollView.FOCUS_DOWN), 150);
                    } else {
                        binding.confirmButton.setBackgroundResource(R.drawable.button_round_background_state_list);
                        params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                        params.bottomMargin = (int) getResources().getDimension(R.dimen.size_25);
                    }
                });

    }

    public synchronized void showErrorFromString(String error, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title);
        builder.setCancelable(false);
        builder.setMessage(error)
                .setPositiveButton(R.string.ok, clickListener);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        showWarningDialog();
    }

}