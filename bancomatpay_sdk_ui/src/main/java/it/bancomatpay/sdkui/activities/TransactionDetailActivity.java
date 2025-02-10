package it.bancomatpay.sdkui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.task.model.TransactionDataOutgoing;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdk.manager.utilities.BigDecimalUtils;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdkui.BCMAbortCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.databinding.ActivityBcmTransactionDetailBinding;
import it.bancomatpay.sdkui.flowmanager.TransactionsFlowManager;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.model.Transaction;
import it.bancomatpay.sdkui.model.TransactionOutgoing;
import it.bancomatpay.sdkui.utilities.AlertDialogBuilderExtended;
import it.bancomatpay.sdkui.utilities.AnimationFadeUtil;
import it.bancomatpay.sdkui.utilities.CjUtils;
import it.bancomatpay.sdkui.utilities.CustomOnClickListener;
import it.bancomatpay.sdkui.utilities.PlacesClientUtil;
import it.bancomatpay.sdkui.utilities.StringUtils;
import it.bancomatpay.sdkui.widgets.BottomDialogHmsMerchantLocation;
import it.bancomatpay.sdkui.widgets.BottomDialogMerchantLocation;

import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.ANN_P2B;
import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.ANN_POS;
import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.PND;
import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.POS;
import static it.bancomatpay.sdk.manager.task.model.TransactionData.Status.RIC;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_SAVE_NUMBER;

public class TransactionDetailActivity extends GenericErrorActivity implements PlacesClientUtil.MerchantImageLoadingListener {

    private ActivityBcmTransactionDetailBinding binding;

    private Transaction transaction;
    private TransactionOutgoing transactionOutgoing;

    private ShopsDataMerchant merchantItem;
    private boolean isP2B = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityName(TransactionDetailActivity.class.getSimpleName());
        binding = ActivityBcmTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarSimpleMerchant.setOnClickLeftImageListener(v -> finish());
        binding.toolbarSimpleConsumer.setOnClickLeftImageListener(v -> finish());
        binding.toolbarSimpleConsumer.setOnClickRightImageListener(v -> shareAction());
        binding.toolbarSimpleConsumer.setCenterImageVisibility(true);
        binding.toolbarSimpleConsumer.setCenterImageVisibility(true);

        if (getIntent().getSerializableExtra(TransactionsFlowManager.TRANSACTION_DATA) != null) {

            transaction = (Transaction) getIntent().getSerializableExtra(TransactionsFlowManager.TRANSACTION_DATA);

            if (transaction.getTransactionType() == TransactionType.P2B) {
                isP2B = true;
                initLayoutMerchant();
            } else {
                initLayoutConsumer();
            }

        } else if (getIntent().getSerializableExtra(TransactionsFlowManager.TRANSACTION_DATA_OUTGOING) != null) {
            transactionOutgoing = (TransactionOutgoing) getIntent().getSerializableExtra(TransactionsFlowManager.TRANSACTION_DATA_OUTGOING);
            initOutgoingTransaction();
        }

        binding.fabMerchantLocation.setOnClickListener(new CustomOnClickListener(v -> clickFabMerchantLocation()));
        binding.buttonShareReceipt.setOnClickListener(new CustomOnClickListener(v -> TransactionsFlowManager.goToTransactionReceipt(this, transaction)));
        binding.buttonCancelPayment.setOnClickListener(new CustomOnClickListener(v -> clickCancelPayment()));
        clearLightStatusBar(binding.layoutMerchantBackground, R.color.blue_statusbar_color);
    }

    @Override
    protected void onDestroy() {
        BancomatSdkInterface.Factory.getInstance().removeTasksListener(this);
        super.onDestroy();
    }


    private void initLayoutMerchant() {
        binding.layoutTransactionDetailConsumer.setVisibility(View.GONE);
        binding.layoutTransactionDetailMerchant.setVisibility(View.VISIBLE);

        if (transaction.getItemInterface() != null && transaction.getItemInterface() instanceof ShopItem) {
            ShopItem shopItem = (ShopItem) transaction.getItemInterface();
            if (shopItem.getAddress() != null) {
                new Handler().postDelayed(() -> binding.fabMerchantLocation.show(), 500);
            }
        }

        binding.layoutMerchantImage.setBackgroundResource(R.drawable.img_merchant);

        binding.textMerchantName.setText(transaction.getTitle());
        binding.textMerchantAddress.setText(transaction.getItemInterface().getDescription());
        if (StringUtils.getFormattedValue(transaction.getAmount()).contains("-")) {
            binding.transactionDetailPriceMerchant.setText(StringUtils.getFormattedValue(transaction.getAmount()).substring(1));
        } else {
            binding.transactionDetailPriceMerchant.setText(StringUtils.getFormattedValue(transaction.getAmount()));
        }
        setFee();

        switch (transaction.getTransactionStatus()) {
            case INV:
                binding.transactionDetailStatusMerchant.setText(R.string.p2b_transaction_detail_payment_success);
                break;
            case PAG:
                binding.textIbanLabelMerchant.setText(R.string.charge_to);
                binding.transactionDetailStatusMerchant.setText(R.string.p2b_transaction_detail_payment_success);
                break;
            case STR:
                binding.textIbanLabelMerchant.setText(R.string.accreditation);
                binding.transactionDetailStatusMerchant.setText(R.string.transaction_detail_money_starling);
                break;
            case RIC:
                binding.textIbanLabelMerchant.setText(R.string.accreditation);
                binding.transactionDetailStatusMerchant.setText(R.string.transaction_detail_money_received);
                break;
            case PND:
                binding.buttonShareReceipt.setVisibility(View.GONE);
                binding.transactionDetailStatusMerchant.setText(R.string.transaction_detail_money_wait);
                break;
            case ANN_P2B:
            case ANN_P2P:
                binding.textIbanView.setVisibility(View.GONE);
                binding.buttonShareReceipt.setVisibility(View.GONE);
                binding.transactionDetailStatusMerchant.setText(R.string.transaction_detail_money_cancel);
                break;
            case ADD:
                binding.textIbanLabelMerchant.setText(R.string.charge_to);
                binding.transactionDetailStatusMerchant.setText(R.string.direct_debit_label);
                binding.layoutMerchantImage.setBackgroundResource(R.drawable.img_recap);
                break;
            case POS:
                binding.transactionDetailStatusMerchant.setText(R.string.withdraw_transaction_detail_success);
                break;
            case ATM:
                binding.transactionDetailStatusMerchant.setText(R.string.withdraw_transaction_detail_success);
                break;
            case ANN_ATM:
                binding.transactionDetailStatusMerchant.setText(R.string.withdraw_transaction_detail_failed);
                binding.textIbanView.setVisibility(View.GONE);
                binding.buttonShareReceipt.setVisibility(View.GONE);
                break;
            case ANN_POS:
                binding.transactionDetailStatusMerchant.setText(R.string.withdraw_transaction_detail_failed);
                binding.textIbanView.setVisibility(View.GONE);
                binding.buttonShareReceipt.setVisibility(View.GONE);
                break;
        }

        String date = StringUtils.getDateStringFormatted(transaction.getDisplayDate(), "dd MMMM - HH:mm");

        binding.transactionDetailDateMerchant.setText(date);

        if (!TextUtils.isEmpty(transaction.getIdSct())) {
            binding.transactionIdTextMerchant.setText(transaction.getIdSct());
            if (isCashbackTransaction()) {
                binding.lineTransactionId.setVisibility(View.VISIBLE);
            }
        } else {
            binding.transactionIdView.setVisibility(View.GONE);
            if (!isCashbackTransaction()) {
                binding.lineIban.setVisibility(View.GONE);
            }
        }
        if (isCashbackTransaction()) {
            BigDecimal cashbackAmount = transaction.getCashbackAmount();
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);
            binding.transactionCashbackView.setVisibility(View.VISIBLE);
            binding.transactionCashbackText.setText(format.format(cashbackAmount));
        }

        double latitude = ((ShopItem) transaction.getItemInterface()).getLatitude();
        double longitude = ((ShopItem) transaction.getItemInterface()).getLongitude();
        String constraint = transaction.getTitle() + " " + transaction.getDescription();
        PlacesClientUtil.getInstance().loadBackgroundMerchant(this, this, latitude, longitude, constraint);

        binding.buttonShareReceipt.setEnabled(false);
        BancomatSdkInterface.Factory.getInstance().doGetTransactionDetails(this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            binding.buttonShareReceipt.setEnabled(true);
                            binding.textIbanMerchant.setText(result.getResult().getIban());
                            binding.progressBarIbanMerchant.setVisibility(View.GONE);
                            AnimationFadeUtil.startFadeInAnimationV1(binding.textIbanMerchant, AnimationFadeUtil.DEFAULT_DURATION);

                            transaction.setTransactionIban(result.getResult().getIban());

                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            binding.buttonShareReceipt.setEnabled(true);
                            showError(result.getStatusCode());
                            binding.textIbanMerchant.setText(getString(R.string.transaction_detail_iban_not_available));
                            transaction.setTransactionIban(getString(R.string.transaction_detail_iban_not_available));
                        }
                    } else {
                        binding.buttonShareReceipt.setEnabled(true);
                        binding.textIbanMerchant.setText(getString(R.string.transaction_detail_iban_not_available));
                        transaction.setTransactionIban(getString(R.string.transaction_detail_iban_not_available));
                    }
                },
                transaction.getTransactionId(),
                !isP2B,
                SessionManager.getInstance().getSessionToken());

    }

    private void setFee(){

        if (!TextUtils.isEmpty(transaction.getFee())) {
            if (transaction.getFee().equals("0")) {
                if (transaction.getTransactionStatus() == POS || transaction.getTransactionStatus() == ANN_POS) {
                    AnimationFadeUtil.startFadeInAnimationV1(binding.merchantTransactionFee, AnimationFadeUtil.DEFAULT_DURATION);
                    binding.merchantTransactionFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(transaction.getFee()))));
                }
            } else {
                AnimationFadeUtil.startFadeInAnimationV1(binding.merchantTransactionFee, AnimationFadeUtil.DEFAULT_DURATION);
                binding.merchantTransactionFee.setText(getString(R.string.fee_label, StringUtils.getFormattedValue(BigDecimalUtils.getBigDecimalFromCents(transaction.getFee()))));
            }
        } else
            AnimationFadeUtil.startFadeOutAnimationV1(binding.merchantTransactionFee, AnimationFadeUtil.DEFAULT_DURATION, View.GONE);


    }

    private void initLayoutConsumer() {
        binding.layoutTransactionDetailConsumer.setVisibility(View.VISIBLE);
        binding.layoutTransactionDetailMerchant.setVisibility(View.GONE);

        binding.layoutMerchantImage.setBackgroundResource(R.drawable.img_recap);

        Bitmap bitmap = transaction.getBitmap();
        if (bitmap != null) {
            if (transaction.getItemInterface().getType() == ItemInterface.Type.NONE && transaction.getTitle().equals(transaction.getPhoneNumber())) {
                binding.contactConsumerImageProfile.setImageResource(R.drawable.placeholder_contact);
                binding.contactConsumerIsActive.setVisibility(View.INVISIBLE);
            } else {
                binding.contactConsumerImageProfile.setImageBitmap(bitmap);
            }
            binding.contactConsumerLetter.setVisibility(View.GONE);
        } else {
            binding.contactConsumerImageProfile.setImageResource(R.drawable.profile_letter_circle_background);
            binding.contactConsumerLetter.setVisibility(View.VISIBLE);
            binding.contactConsumerLetter.setText(transaction.getInitials());
        }
        if (transaction.getItemInterface().getType() != null
                && transaction.getItemInterface().getType() != ContactItem.Type.NONE
                && transaction.showBancomatLogo()) {
            binding.contactConsumerIsActive.setVisibility(View.VISIBLE);
        } else {
            binding.contactConsumerIsActive.setVisibility(View.INVISIBLE);
        }
        binding.contactConsumerName.setText(transaction.getTitle());
        if (!transaction.getTitle().equals(transaction.getPhoneNumber())) {
            binding.contactConsumerNumber.setText(transaction.getPhoneNumber());
        } else {
            binding.contactConsumerNumber.setVisibility(View.GONE);
            AnimationFadeUtil.startFadeInAnimationV1(binding.saveContactButton, AnimationFadeUtil.DEFAULT_DURATION);
            binding.saveContactButton.setOnClickListener(new CustomOnClickListener(v -> {
                CjUtils.getInstance().sendCustomerJourneyTagEvent(this, KEY_P2P_SAVE_NUMBER, null, false);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    showPermissionRequestDialog();
                } else TransactionsFlowManager.goToSaveContact(this, transaction.getPhoneNumber());
            }));
        }

        if (StringUtils.getFormattedValue(transaction.getAmount()).contains("-")) {
            binding.transactionDetailPriceConsumer.setText(StringUtils.getFormattedValue(transaction.getAmount()).substring(1));
        } else {
            binding.transactionDetailPriceConsumer.setText(StringUtils.getFormattedValue(transaction.getAmount()));
        }

        switch (transaction.getTransactionStatus()) {
            case INV:
                binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_send);
                break;
            case PAG:
                binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_send);
                break;
            case STR:
                binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_starling);
                break;
            case RIC:
                binding.toolbarSimpleConsumer.setRightImageVisibility(false);
                binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_received);
                break;
            case PND:
                binding.toolbarSimpleConsumer.setRightImageVisibility(false);
                binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_wait);
                //buttonCancelPayment.setVisibility(View.VISIBLE);
                binding.expandableLayoutButton.expand();
                break;
            case ANN_P2B:
            case ANN_P2P:
                binding.textIbanConsumerView.setVisibility(View.GONE);
                binding.toolbarSimpleConsumer.setRightImageVisibility(false);
                binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_cancel);
                break;
        }

        String date = StringUtils.getDateStringFormatted(transaction.getDisplayDate(), "dd MMMM - HH:mm");

        binding.transactionDetailDateConsumer.setText(date);

        if (transaction.getTransactionStatus() == RIC) {
            binding.textIbanLabelConsumer.setText(getString(R.string.accreditation));
        }
        if (!TextUtils.isEmpty(transaction.getDescription())) {
            binding.transactionMessageTextConsumer.setText(transaction.getDescription());

        } else {
            binding.transactionMessageConsumerView.setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(transaction.getIdSct())) {
            binding.transactionIdTextConsumer.setText(transaction.getIdSct());
        } else {
            binding.transactionIdConsumerView.setVisibility(View.GONE);
            binding.lineMessageConsumer.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(transaction.getIdSct()) && TextUtils.isEmpty(transaction.getDescription())) {
            binding.lineIbanConsumer.setVisibility(View.GONE);
        }

        binding.toolbarSimpleConsumer.setRightImageVisibility(false);
        BancomatSdkInterface.Factory.getInstance().doGetTransactionDetails(this, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {

                            enableShareConsumerIfPossible();
                            binding.textIbanConsumer.setText(result.getResult().getIban());
                            binding.progressBarIbanConsumer.setVisibility(View.GONE);
                            AnimationFadeUtil.startFadeInAnimationV1(binding.textIbanConsumer, AnimationFadeUtil.DEFAULT_DURATION);

                            transaction.setTransactionIban(result.getResult().getIban());

                        } else if (result.isSessionExpired()) {
                            BCMAbortCallback.getInstance().getAuthenticationListener()
                                    .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                        } else {
                            enableShareConsumerIfPossible();
                            showError(result.getStatusCode());
                            binding.textIbanConsumer.setText(getString(R.string.transaction_detail_iban_not_available));
                            transaction.setTransactionIban(getString(R.string.transaction_detail_iban_not_available));
                        }
                    } else {
                        enableShareConsumerIfPossible();
                        binding.textIbanConsumer.setText(getString(R.string.transaction_detail_iban_not_available));
                        transaction.setTransactionIban(getString(R.string.transaction_detail_iban_not_available));
                    }
                },
                transaction.getTransactionId(),
                !isP2B,
                SessionManager.getInstance().getSessionToken());

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

    private void enableShareConsumerIfPossible() {
        switch (transaction.getTransactionStatus()) {
            case RIC:
            case ANN_P2B:
            case ANN_P2P:
                binding.toolbarSimpleConsumer.setRightImageVisibility(false);
                break;
            default:
                binding.toolbarSimpleConsumer.setRightImageVisibility(true);
        }
    }

    private boolean isCashbackTransaction() {
        boolean isCashbackTransaction = false;
        if ((transaction.getTransactionType() == (TransactionType.P2B) || transaction.getTransactionType() == (TransactionType.B2P)) && transaction.getCashbackAmount() != null) {
            isCashbackTransaction = true;
        }
        return isCashbackTransaction;
    }

    private void initOutgoingTransaction() {
        binding.toolbarSimpleConsumer.setRightImageVisibility(false);

        binding.layoutTransactionDetailConsumer.setVisibility(View.VISIBLE);
        binding.layoutTransactionDetailMerchant.setVisibility(View.GONE);

        binding.layoutMerchantImage.setBackgroundResource(R.drawable.img_recap);

        String date = StringUtils.getDateStringFormatted(transactionOutgoing.getDisplayDate(), "dd MMMM - HH:mm");
        binding.transactionDetailDateConsumer.setText(date);

        Bitmap bitmap = transactionOutgoing.getBitmap();
        if (bitmap != null) {
            if (transactionOutgoing.getItemInterface().getType() == ItemInterface.Type.NONE) {
                binding.contactConsumerImageProfile.setImageResource(R.drawable.placeholder_contact);
            } else {
                binding.contactConsumerImageProfile.setImageBitmap(bitmap);
            }
            binding.contactConsumerLetter.setVisibility(View.GONE);
        } else {
            binding.contactConsumerImageProfile.setImageResource(R.drawable.profile_letter_circle_background);
            binding.contactConsumerLetter.setVisibility(View.VISIBLE);
            binding.contactConsumerLetter.setText(transactionOutgoing.getInitials());
        }

        binding.contactConsumerName.setText(transactionOutgoing.getTitle());
        if (!transactionOutgoing.getTitle().equals(transactionOutgoing.getPhoneNumber())) {
            binding.contactConsumerNumber.setText(transactionOutgoing.getPhoneNumber());
        } else {
            binding.contactConsumerNumber.setVisibility(View.GONE);
        }

        binding.transactionDetailPriceConsumer.setText(StringUtils.getFormattedValue(transactionOutgoing.getAmount()));

        if (transactionOutgoing.getTransactionStatus() == TransactionDataOutgoing.Status.SENT) {
            binding.transactionDetailStatusConsumer.setText(R.string.movements_outgoing_payment_request_sent);
        }

        binding.textIbanConsumerView.setVisibility(View.GONE);
        binding.transactionIdConsumerView.setVisibility(View.GONE);


        if (!TextUtils.isEmpty(transactionOutgoing.getDescription())) {
            binding.transactionMessageTextConsumer.setText(transactionOutgoing.getDescription());
            binding.lineMessageConsumer.setVisibility(View.GONE);
        } else {
            binding.transactionMessageConsumerView.setVisibility(View.GONE);
        }
    }

    private void clickFabMerchantLocation() {

        if (merchantItem == null) {

            LoaderHelper.showLoader(this);

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
            }, transaction.getTag(), String.valueOf(transaction.getItemInterface().getId()), SessionManager.getInstance().getSessionToken());

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


    private void shareAction() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String message = "";

        String price;
        if (StringUtils.getFormattedValue(transaction.getAmount()).contains("-")) {
            price = StringUtils.getFormattedValue(transaction.getAmount()).substring(1);

        } else {
            price = StringUtils.getFormattedValue(transaction.getAmount());
        }

        TransactionData.Status paymentStateType = transaction.getTransactionStatus();
        if (paymentStateType != null) {
            if (TransactionData.Status.INV == paymentStateType) {
                if (TextUtils.isEmpty(transaction.getDescription())) {
                    if (isP2B) {
                        message = getString(R.string.movements_outcome_fragment_share_success_p2b, price);
                    } else {
                        message = getString(R.string.movements_outcome_fragment_share_success_p2p_empty, price);
                    }
                } else {
                    if (isP2B) {
                        message = getString(R.string.movements_outcome_fragment_share_success_p2b, price);
                    } else {
                        message = getString(R.string.movements_outcome_fragment_share_success_p2p, price, transaction.getDescription());
                    }
                }
            } else if (PND == paymentStateType) {
                if (TextUtils.isEmpty(transaction.getDescription())) {
                    message = getString(R.string.movements_outcome_fragment_share_pending_empty, price);
                } else {
                    message = getString(R.string.movements_outcome_fragment_share_pending, price, transaction.getDescription());
                }
            }

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));
        }
    }

    private void clickCancelPayment() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.transaction_detail_cancel_send_money_dialog_title))
                .setMessage(getResources().getString(R.string.transaction_detail_cancel_send_money_dialog_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                    LoaderHelper.showLoader(this);

                    BancomatSdkInterface.Factory.getInstance().doCancelPayment(this, result -> {
                                if (result != null) {
                                    if (result.isSuccess()) {

                                        /*buttonCancelPayment.post(() ->
                                                AnimationFadeUtil.startFadeOutAnimationV1(buttonCancelPayment, AnimationFadeUtil.DEFAULT_DURATION, View.INVISIBLE)
                                        );*/
                                        binding.expandableLayoutButton.collapse();
                                        binding.transactionDetailStatusConsumer.setText(R.string.transaction_detail_money_cancel);
                                        binding.textIbanConsumerView.setVisibility(View.GONE);
                                        binding.toolbarSimpleConsumer.setRightImageVisibility(false);
                                        if (transaction.getTransactionType() == TransactionType.P2P) {
                                            transaction.setTransactionStatus(TransactionData.Status.ANN_P2P);
                                        } else {
                                            if (transaction.getItemInterface() != null) {
                                                transaction.setTransactionStatus(ANN_P2B);
                                            }
                                        }
                                    } else if (result.isSessionExpired()) {
                                        BCMAbortCallback.getInstance().getAuthenticationListener()
                                                .onAbortSession(this, BCMAbortCallback.getInstance().getSessionRefreshListener());
                                    } else {
                                        showError(result.getStatusCode());
                                    }
                                }
                            }, transaction.getTransactionId(), transaction.getPhoneNumber(),
                            SessionManager.getInstance().getSessionToken());
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void merchantImageLoaded(Bitmap bitmap, boolean animate) {
        binding.imageBackgroundMerchant.setImageBitmap(bitmap);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMerchant, 200);
        AnimationFadeUtil.startFadeInAnimationV1(binding.imageBackgroundMask, 200);
    }

}
