package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import java.math.BigDecimal;
import java.util.Date;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.TransactionData;
import it.bancomatpay.sdk.manager.task.model.TransactionType;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.BitmapCache;

public class Transaction implements DisplayData {

    private TransactionData transactionData;

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

    public String getIdSct() {
        return transactionData.getIdSct();
    }

    public String getTransactionIban() {
        return transactionData.getIban();
    }

    public void setTransactionIban(String transactionIban) {
        transactionData.setIban(transactionIban);
    }

    public BigDecimal getAmount() {
        return transactionData.getAmount();
    }

    public BigDecimal getTotalAmount(){return transactionData.getTotalAmount();}

    public BigDecimal getCashbackAmount(){return transactionData.getCashbackAmount();}

    public Transaction(TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    public TransactionData.Status getTransactionStatus() {
        return transactionData.getTransactionStatus();
    }

    public void setTransactionStatus(TransactionData.Status transactionStatus) {
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
        switch (transactionData.getTransactionType()) {
            case P2P:
                try {
                    Uri uri = Uri.parse(transactionData.getImageResource());
                    bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
                } catch (Exception e) {
                    if (TextUtils.isEmpty(transactionData.getDisplayName())) {
                        bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                                R.drawable.placeholder_contact_list);
                    }
                }
                break;
            case B2P:
            case P2B:
                try {
                    byte[] decodedString = Base64.decode(transactionData.getImageResource(), Base64.NO_WRAP);
                    bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                } catch (Exception e) {
                    bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                            R.drawable.placeholder_merchant);
                }
                break;
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

    public String getTag() {
        return transactionData.getTag();
    }

    @Override
    public boolean showBancomatLogo() {
        switch (transactionData.getTransactionStatus()) {
            case PND:
            case PAG:
            case STR:
            case ANN_P2B:
                return false;
            default:
                return true;
        }
    }

    public  String getFee(){
        return transactionData.getFee();
    }


    @Override
    public ItemInterface getItemInterface() {
        return transactionData.getItemInterface();
    }

}
