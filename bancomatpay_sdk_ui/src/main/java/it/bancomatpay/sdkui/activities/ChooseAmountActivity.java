package it.bancomatpay.sdkui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.model.BCMOperation;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ActivityChooseAmountBinding;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.model.AbstractPaymentData;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.DisplayData;
import it.bancomatpay.sdkui.model.KeyboardType;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.widgets.LabelPaymentAmount;

import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.CAN_CHANGE_FLOW_TYPE;
import static it.bancomatpay.sdkui.flowmanager.PaymentFlowManager.PAYMENT_CONTACT_FLOW_TYPE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_BACK_FROM_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_CHANGE_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_BACK_FROM_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CHANGE_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CONFIRM_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_TYPE;
import static it.bancomatpay.sdkui.utilities.CjConstants.PARAM_TYPE;

public class ChooseAmountActivity extends GenericErrorActivity implements LabelPaymentAmount.LabelListener, PlacesClientUtil.MerchantImageLoadingListener {

    private final static String TAG = ChooseAmountActivity.class.getSimpleName();
    private ActivityChooseAmountBinding binding;

    int money = 0;
    AbstractPaymentData paymentData;
    DisplayData displayData;
    PaymentContactFlowType flowType;
    Boolean isP2B = false;
    boolean isSendMoney = true;
    boolean canSendChangeAmountCjTag = true;
    boolean canChangeFlowType = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(ChooseAmountActivity.class.getSimpleName());
        binding = ActivityChooseAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        flowType = (PaymentContactFlowType) getIntent().getSerializableExtra(PAYMENT_CONTACT_FLOW_TYPE);
        if(flowType == null) {
            flowType = (PaymentContactFlowType) savedInstanceState.getSerializable(PAYMENT_CONTACT_FLOW_TYPE);
        }
        if(flowType == null) {
            flowType = PaymentContactFlowType.SEND;
        }

        Serializable canChangeFlowTypeSerializable = getIntent().getSerializableExtra(CAN_CHANGE_FLOW_TYPE);
        if(canChangeFlowTypeSerializable != null) {
            canChangeFlowType = (boolean) canChangeFlowTypeSerializable;
        }

        binding.toolbarSimple.setOnClickLeftImageListener(v -> onBackPressed());
        binding.toolbarSimple.setRightImageVisibility(false);

        binding.getMoneyKeyboard.setKeyboardListener(binding.getMoneyLabel);
        binding.getMoneyKeyboard.setKeyboardType(KeyboardType.PAYMENT_KEYBOARD);
        binding.getMoneyLabel.setLabelListener(this);

        displayData = (DisplayData) getIntent().getSerializableExtra(PaymentFlowManager.INSERT_AMOUNT);
        if (displayData != null) {
            initLayout(displayData);
        }

        clearLightStatusBar(binding.mainLayout, R.color.blue_statusbar_color);
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isP2B) {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_BACK_FROM_AMOUNT, null, true);
        } else {
            CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_BACK_FROM_AMOUNT, null, true);
        }
        finish();
    }


    private void initLayout(DisplayData displayData) {
        if (displayData.getItemInterface() instanceof ShopItem) {
            ShopItem shopItem = (ShopItem) displayData.getItemInterface();
            MerchantPaymentData merchantPaymentData = new MerchantPaymentData();
            merchantPaymentData.setShopId(shopItem.getShopId());
            merchantPaymentData.setTag(shopItem.getTag());
            paymentData = merchantPaymentData;
            paymentData.setDisplayData(displayData);
            isP2B = true;
        } else if (displayData.getItemInterface() instanceof ShopsDataMerchant) {
            ShopsDataMerchant shopsDataMerchant = (ShopsDataMerchant) displayData.getItemInterface();
            MerchantPaymentData merchantPaymentData = new MerchantPaymentData();
            merchantPaymentData.setShopId(shopsDataMerchant.getShopItem().getShopId());
            merchantPaymentData.setTag(shopsDataMerchant.getShopItem().getTag());
            paymentData = merchantPaymentData;
            paymentData.setDisplayData(displayData);
            isP2B = true;
        } else {
            ConsumerPaymentData consumerPaymentData = new ConsumerPaymentData();
            consumerPaymentData.setMsisdn(displayData.getPhoneNumber());
            paymentData = consumerPaymentData;
        }

        binding.cancelButtonInsertNumber.setOnClickListener(new CustomOnClickListener(v -> onDeleteNumber()));

        binding.cancelButtonInsertNumber.setOnLongClickListener(v -> {
            binding.getMoneyLabel.onDeleteAllText();
            return true;
        });

        binding.contactTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (binding.contactTabLayout.getSelectedTabPosition() == 0) {
                    isSendMoney = true;
                } else if (binding.contactTabLayout.getSelectedTabPosition() == 1) {
                    isSendMoney = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setChooseAmountActivity(isP2B);
    }

    private void setChooseAmountActivity(boolean isP2B) {
        TabLayout.Tab tabSelected;
        if(flowType == PaymentContactFlowType.SEND) {
            tabSelected = binding.contactTabLayout.getTabAt(0);
        } else {
            tabSelected = binding.contactTabLayout.getTabAt(1);
        }
        if(tabSelected != null && !tabSelected.isSelected()) {
            tabSelected.select();
        }

        if (isP2B) {

            binding.contactConsumerBox.setVisibility(View.GONE);
            binding.layoutMerchantBox.setVisibility(View.VISIBLE);

            binding.contactTabLayout.setVisibility(View.GONE);
            binding.tabLayoutLineBackground.setVisibility(View.GONE);

            if (TextUtils.isEmpty(displayData.getTitle())) {
                binding.textMerchantName.setVisibility(View.GONE);
            } else {
                binding.textMerchantName.setText(displayData.getTitle());

            }
            if (TextUtils.isEmpty(displayData.getDescription())) {
                binding.textMerchantAddress.setVisibility(View.GONE);
            } else {
                binding.textMerchantAddress.setText(displayData.getDescription());
            }

            String constraint = displayData.getTitle() + " " + displayData.getDescription();
            PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, paymentData, constraint);

        } else {

            binding.contactConsumerBox.setVisibility(View.VISIBLE);
            binding.layoutMerchantBox.setVisibility(View.GONE);

            binding.layoutMerchantImage.setVisibility(View.GONE);

            if (TextUtils.isEmpty(displayData.getTitle())) {
                binding.contactConsumerName.setVisibility(View.GONE);
            } else {
                binding.contactConsumerName.setText(displayData.getTitle());
            }
            if (TextUtils.isEmpty(displayData.getDescription())) {
                binding.contactConsumerNumber.setVisibility(View.GONE);
            } else {
                binding.contactConsumerNumber.setText(displayData.getPhoneNumber());
            }
            if (displayData.getBitmap() != null) {
                if (TextUtils.isEmpty(displayData.getDescription()) && displayData.getItemInterface().getType() == ItemInterface.Type.NONE) {
                    binding.contactConsumerImageProfile.setImageResource(R.drawable.placeholder_contact);
                    binding.contactConsumerImageProfile.setVisibility(View.VISIBLE);
                    binding.contactConsumerImageProfileCircle.setVisibility(View.INVISIBLE);
                } else {
                    binding.contactConsumerImageProfileCircle.setImageBitmap(displayData.getBitmap());
                    binding.contactConsumerImageProfile.setVisibility(View.INVISIBLE);
                    binding.contactConsumerImageProfileCircle.setVisibility(View.VISIBLE);
                    binding.contactConsumerLetter.setVisibility(View.GONE);
                }
            } else {
                binding.contactConsumerImageProfile.setVisibility(View.VISIBLE);
                binding.contactConsumerImageProfileCircle.setVisibility(View.INVISIBLE);
                binding.contactConsumerLetter.setVisibility(View.VISIBLE);
                binding.contactConsumerLetter.setText(displayData.getInitials());
            }

            boolean isContactUnknown = TextUtils.isEmpty(displayData.getDescription())
                    && displayData.getItemInterface().getType() == ItemInterface.Type.NONE;

            if (!displayData.showBancomatLogo() && !isContactUnknown || !canChangeFlowType) {
                binding.contactConsumerIsActive.setVisibility(View.INVISIBLE);
                binding.contactTabLayout.setVisibility(View.GONE);
                binding.tabLayoutLineBackground.setVisibility(View.GONE);
                binding.paymentTypeLabel.setVisibility(View.VISIBLE);
                if(flowType == PaymentContactFlowType.REQUEST) {
                    binding.paymentTypeLabel.setText(R.string.request_money_text);
                } else {
                    binding.paymentTypeLabel.setText(R.string.sending_money_text);
                }
            } else if (isContactUnknown) {
                binding.contactConsumerIsActive.setVisibility(View.INVISIBLE);
                binding.paymentTypeLabel.setVisibility(View.GONE);
            } else {
                binding.contactConsumerIsActive.setVisibility(View.VISIBLE);
                binding.paymentTypeLabel.setVisibility(View.GONE);
            }
        }
    }

    private void onDeleteNumber() {
        if (canSendChangeAmountCjTag) {
            if (isP2B) {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2B_CHANGE_AMOUNT, null, true);
            } else {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_CHANGE_AMOUNT, null, true);
            }
        }
        canSendChangeAmountCjTag = false;
        binding.getMoneyLabel.onDeleteCharacter();
    }

    @Override
    public void onMoneyInserted(int money, boolean isDeletingCharacter) {
        this.money = money;
        if (money > 0) {
            if (!isDeletingCharacter) {
                canSendChangeAmountCjTag = true;
            }

            binding.getMoneyKeyboard.getButtonContinueOn().setEnabled(true);
            binding.getMoneyKeyboard.getButtonContinueOn().setOnClickListener(new CustomOnClickListener(v -> {

                BigDecimal bigDecimal = BigDecimalUtils.getBigDecimalFromCents(money);

                paymentData.setDisplayData(displayData);
                paymentData.setAmount(bigDecimal);
                paymentData.setCentsAmount(money);

                if (isP2B) {

                    LoaderHelper.showLoader(this);

                    BCMOperation bcmPayment = new BCMOperation();
                    bcmPayment.setTag(((MerchantPaymentData) paymentData).getTag());
                    bcmPayment.setShopId(((MerchantPaymentData) paymentData).getShopId());
                    bcmPayment.setTillId(((MerchantPaymentData) paymentData).getTillId());
                    bcmPayment.setAmount(paymentData.getCentsAmount());

                    BancomatSdkInterface.Factory.getInstance().doVerifyPaymentP2B(
                            this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

                                        paymentData.setFee(result.getResult().getFee());

                                        //hiddenNameResult = result.getResult().getContactName();
                                        paymentData.setFee(result.getResult().getFee());
                                        paymentData.setPaymentId(result.getResult().getPaymentId());
                                        //paymentData.setCausal(editTextMessage.getText().toString());

                                        goToResultSendMoney();
                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                    } else {
                                        showError(result.getStatusCode());
                                    }
                                }
                            },
                            bcmPayment,
                            SessionManager.getInstance().getSessionToken());

                } else {
                    HashMap<String, String> mapEventParams = new HashMap<>();
                    mapEventParams.put(PARAM_TYPE, isSendMoney ? "Send" : "Request");
                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                            this, KEY_P2P_TYPE, mapEventParams, true);

                    CjUtils.getInstance().sendCustomerJourneyTagEvent(
                            this, KEY_P2P_CONFIRM_AMOUNT, null, true);
                    goToConfirmSendMoney();
                }

            }));

        } else {
            binding.getMoneyKeyboard.getButtonContinueOn().setEnabled(false);
            binding.getMoneyKeyboard.getButtonContinueOn().setVisibility(View.INVISIBLE);
            binding.getMoneyKeyboard.getButtonContinueOff().setVisibility(View.VISIBLE);
        }
    }

    private void goToConfirmSendMoney() {
        PaymentFlowManager.goToConfirm(this, paymentData, isSendMoney, false);
    }

    private void goToResultSendMoney() {
        BcmLocation location = null;
        if (getIntent().getSerializableExtra(PaymentFlowManager.INSERT_AMOUNT) instanceof ShopsDataMerchant) {
            ShopsDataMerchant shopData = (ShopsDataMerchant) getIntent().getSerializableExtra(PaymentFlowManager.INSERT_AMOUNT);
            if (shopData != null && shopData.getLatitude() != 0 && shopData.getLongitude() != 0) {
                location = new BcmLocation();
                location.setLatitude(shopData.getLatitude());
                location.setLongitude(shopData.getLongitude());
            }
        }
        PaymentFlowManager.goToResultPayment(this, paymentData, location, "", true, false);
    }

    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        if (animate) {
            AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
            AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
        } else {
            binding.imageBackgroundMerchant.setVisibility(View.VISIBLE);
            binding.imageBackgroundMask.setVisibility(View.VISIBLE);
        }
    }

    public void onSaveInstanceState(Bundle outstate) {
        CustomLogger.d(TAG, "onSaveInstanceState");
        outstate.putSerializable(PAYMENT_CONTACT_FLOW_TYPE, flowType);
        super.onSaveInstanceState(outstate);
    }
}


