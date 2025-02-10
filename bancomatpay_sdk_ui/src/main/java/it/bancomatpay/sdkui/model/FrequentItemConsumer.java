package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.BitmapCache;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class FrequentItemConsumer implements ItemInterfaceConsumer, Serializable, Comparable, DisplayData {

    protected FrequentItem frequentItem;

    public FrequentItemConsumer(FrequentItem recentItem) {
        this.frequentItem = recentItem;
    }

    public FrequentItem getFrequentItem() {
        return frequentItem;
    }

    @Override
    public String getTitle() {
        return frequentItem.getTitle();
    }

    public String getDescription() {
        return frequentItem.getDescription();
    }

    @Override
    public String getPhoneNumber() {
        return frequentItem.getPhoneNumber();
    }

    @Override
    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        try {
            Uri uri = Uri.parse(frequentItem.getImage());
            bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
        } catch (Exception e) {
            if (TextUtils.isEmpty(frequentItem.getDescription())) {
                bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                        R.drawable.placeholder_contact_list);
            }
        }
        return bitmap;
    }

    public Bitmap getNullableBitmap() {
        Bitmap bitmap = null;
        try {
            Uri uri = Uri.parse(frequentItem.getImage());
            bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
        } catch (Exception e) {
        }
        return bitmap;
    }

    public boolean isFrequent() {
        return frequentItem.getOperationCounter() > 0;
    }

    /*public void setFrequent(boolean frequent) {
        if(favorite) {
            frequentItem.setPinningTime(System.currentTimeMillis());
        }
        else {
            frequentItem.setPinningTime(0);
        }
    }*/

    @Override
    public String getLetter() {
        if (TextUtils.isEmpty(frequentItem.getLetter())) {
            return "#";
        }
        return frequentItem.getLetter();
    }

    @Override
    public String getLetterSurname() {
        return getLetter();
    }

    @Override
    public String getInitials() {
        if (frequentItem.getItemInterface() instanceof ContactItem) {
            return ((ContactItem) frequentItem.getItemInterface()).getInitials();
        } else {
            return "";
        }
    }

    @Override
    public boolean showBancomatLogo() {
        return frequentItem.getType() != ItemInterface.Type.NONE;
    }

    @Override
    public ItemInterface getItemInterface() {
        return frequentItem.getItemInterface();
    }

    @Override
    public boolean performFilter(String filter) {
        return StringUtils.contains(new String[]{frequentItem.getPhoneNumber(), frequentItem.getDescription(), frequentItem.getTitle()}, filter);
    }

    @Override
    public int compareTo(@NonNull Object x) {

        if (!(x instanceof FrequentItemConsumer)) throw new ClassCastException();

        FrequentItemConsumer e = (FrequentItemConsumer) x;
        return Integer.compare(e.frequentItem.getOperationCounter(), this.frequentItem.getOperationCounter());
    }

    public int getOperationCounter() {
        return frequentItem.getOperationCounter();
    }

}
