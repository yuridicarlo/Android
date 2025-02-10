package it.bancomatpay.sdkui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Objects;

import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.manager.db.UserContact;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.BitmapCache;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class SplitItemConsumer extends SplitBeneficiary implements ItemInterfaceConsumer, Serializable, Comparable, DisplayData {

    private transient Bitmap bitmap;
    private boolean isImageAvailable;

    public long getContactId() {
        return getBeneficiary().getContactId();
    }

    public SplitItemConsumer(ContactItem contactItem) {
        setBeneficiary(contactItem);
    }

    public boolean isOnlyConsumer() {
        return getBeneficiary().getType() == ContactItem.Type.CONSUMER || getBeneficiary().getType() == ContactItem.Type.CONSUMER_PR;
    }

    public boolean isUnregistered() {
        return getBeneficiary().getType() == ContactItem.Type.NONE;
    }

    public boolean isOnlyMerchant() {
        return getBeneficiary().getType() == ContactItem.Type.MERCHANT;
    }

    public boolean isMerchantAndConsumer() {
        return getBeneficiary().getType() == ContactItem.Type.BOTH || getBeneficiary().getType() == ContactItem.Type.BOTH_PR;
    }

    public boolean isBlocked() {
        return getBeneficiary().isBlocked();
    }

    public void setBlocked(boolean block) {
        getBeneficiary().setBlocked(block);
    }

    @Override
    public String getLetter() {
        if (TextUtils.isEmpty(getBeneficiary().getLetter())) {
            return "#";
        }
        return getBeneficiary().getLetter();
    }

    @Override
    public String getLetterSurname() {
        if (TextUtils.isEmpty(getBeneficiary().getLetterSurname())) {
            return "#";
        }
        return getBeneficiary().getLetterSurname();
    }

    @Override
    public boolean showBancomatLogo() {
        return getBeneficiary().getType() != ContactItem.Type.NONE;
    }

    @Override
    public ItemInterface getItemInterface() {
        return getBeneficiary();
    }

    @Override
    public boolean performFilter(String filter) {
        return StringUtils.contains(new String[]{getBeneficiary().getMsisdn(), getBeneficiary().getDescription(), getBeneficiary().getTitle()}, filter);
    }

    @Override
    public String getInitials() {
        return getBeneficiary().getInitials();
    }

    @Override
    public String getTitle() {
        return getBeneficiary().getTitle();
    }

    @Override
    public String getDescription() {
        return getBeneficiary().getDescription();
    }

    @Override
    public String getPhoneNumber() {
        return getBeneficiary().getMsisdn();
    }

    @Override
    public Bitmap getBitmap() {
        try {
            if (this.bitmap == null) {
                Uri uri = Uri.parse(getBeneficiary().getPhotoUri());
                this.bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
            }
            setImageAvailable(true);
        } catch (Exception e) {
            if (TextUtils.isEmpty(getBeneficiary().getDescription())) {
                this.bitmap = BitmapFactory.decodeResource(PayCore.getAppContext().getResources(),
                        R.drawable.placeholder_contact_list);
            }
            setImageAvailable(false);
        }
        return this.bitmap;
    }

    public boolean isImageAvailable() {
        return isImageAvailable;
    }

    public void setImageAvailable(boolean imageAvailable) {
        isImageAvailable = imageAvailable;
    }

    @Override
    public int compareTo(@NonNull Object x) {

        if (!(x instanceof SplitItemConsumer)) throw new ClassCastException();

        SplitItemConsumer e = (SplitItemConsumer) x;
        if (e.getBeneficiary().getPinningTime() != this.getBeneficiary().getPinningTime()) {
            return Long.compare(e.getBeneficiary().getPinningTime(), this.getBeneficiary().getPinningTime());
        } else {
            //return Long.compare(e.getBeneficiary().getTransactionTime(), this.getBeneficiary().getTransactionTime());
            return 0;
        }
    }

    public boolean isFavorite() {
        return getBeneficiary().getPinningTime() > 0;
    }

    public void setFavorite(boolean favorite) {
        if (favorite) {
            getBeneficiary().setPinningTime(System.currentTimeMillis());
        } else {
            getBeneficiary().setPinningTime(0);
        }
    }

    public UserContact.Model getDbModel() {
        return getBeneficiary().getDbModel();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SplitItemConsumer) || ((SplitItemConsumer) obj).getBeneficiary() == null)
            return false;

        return Objects.equals(getBeneficiary().getMsisdn(), ((SplitItemConsumer) obj).getBeneficiary().getMsisdn());
    }

    public static SplitItemConsumer fromFrequentItemConsumer(FrequentItemConsumer frequentItemConsumer){
        return new SplitItemConsumer((ContactItem) frequentItemConsumer.getFrequentItem().getItemInterface());
    }


}
