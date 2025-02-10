package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Date;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.TransactionDataOutgoing;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.BitmapCache;

public class TransactionOutgoing implements DisplayData {

    private TransactionDataOutgoing transactionData;

    public Date getDisplayDate() {
        if (transactionData.getPaymentDate() != null) {
            return transactionData.getPaymentDate();
        }
        if (transactionData.getRequestDate() != null) {
            return transactionData.getRequestDate();
        }
        return new Date();
    }

    public TransactionType getTransactionType() {
        return transactionData.getTransactionType();
    }

    public String getTransactionId() {
        return transactionData.getTransactionId();
    }

    public BigDecimal getAmount() {
        return transactionData.getAmount();
    }

    public TransactionOutgoing(TransactionDataOutgoing transactionData) {
        this.transactionData = transactionData;
    }

    public TransactionDataOutgoing.Status getTransactionStatus() {
        return transactionData.getTransactionStatus();
    }

    public void setTransactionStatus(TransactionDataOutgoing.Status transactionStatus) {
        transactionData.setTransactionStatus(transactionStatus);
    }

    @Override
    public String getTitle() {
        if (TextUtils.isEmpty(transactionData.getDisplayName())) {
            return transactionData.getMsisdn();
        } else {
            return transactionData.getDisplayName();
        }
    }

    @Override
    public String getDescription() {
        return transactionData.getCausal();
    }

    @Override
    public String getPhoneNumber() {
        return transactionData.getMsisdn();
    }

    @Override
    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        try {
            Uri uri = Uri.parse(transactionData.getImageResource());
            bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
        } catch (Exception e) {
            if (TextUtils.isEmpty(transactionData.getItemInterface().getDescription())) {
                bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                        R.drawable.placeholder_contact_list);
            }
        }
        return bitmap;
    }

    @Override
    public String getLetter() {
        if (TextUtils.isEmpty(transactionData.getLetter())) {
            return "#";
        } else {
            return transactionData.getLetter();
        }

    }

    @Override
    public String getLetterSurname() {
        return getLetter();
    }

    @Override
    public String getInitials() {
        if (transactionData.getItemInterface() instanceof ContactItem) {
            return ((ContactItem) transactionData.getItemInterface()).getInitials();
        } else {
            return "";
        }
    }

    @Override
    public boolean showBancomatLogo() {
        //PER
        return true;
    }

    @Override
    public ItemInterface getItemInterface() {
        return transactionData.getItemInterface();
    }

}
