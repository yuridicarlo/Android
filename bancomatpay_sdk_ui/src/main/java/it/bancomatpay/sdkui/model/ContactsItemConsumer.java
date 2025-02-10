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
import it.bancomatpay.sdkui.R;
import it.bancomatpay.sdkui.utilities.BitmapCache;
import it.bancomatpay.sdkui.utilities.StringUtils;

public class ContactsItemConsumer implements ItemInterfaceConsumer, Serializable, Comparable, DisplayData {

    private ContactItem contactItem;
    private transient Bitmap bitmap;
    private boolean isImageAvailable;

    public long getContactId() {
        return contactItem.getContactId();
    }

    public ContactsItemConsumer(ContactItem contactItem) {
        this.contactItem = contactItem;
    }

    public boolean isOnlyConsumer() {
        return contactItem.getType() == ContactItem.Type.CONSUMER || contactItem.getType() == ContactItem.Type.CONSUMER_PR;
    }

    public boolean isUnregistered() {
        return contactItem.getType() == ContactItem.Type.NONE;
    }

    public boolean isOnlyMerchant() {
        return contactItem.getType() == ContactItem.Type.MERCHANT;
    }

    public boolean isMerchantAndConsumer() {
        return contactItem.getType() == ContactItem.Type.BOTH || contactItem.getType() == ContactItem.Type.BOTH_PR;
    }

    public boolean isBlocked() {
        return contactItem.isBlocked();
    }

    public void setBlocked(boolean block) {
        contactItem.setBlocked(block);
    }

    @Override
    public String getLetter() {
        if (TextUtils.isEmpty(contactItem.getLetter())) {
            return "#";
        }
        return contactItem.getLetter();
    }

    @Override
    public String getLetterSurname() {
        if (TextUtils.isEmpty(contactItem.getLetterSurname())) {
            return "#";
        }
        return contactItem.getLetterSurname();
    }

    @Override
    public boolean showBancomatLogo() {
        return contactItem.getType() != ContactItem.Type.NONE;
    }

    @Override
    public ItemInterface getItemInterface() {
        return contactItem;
    }

    @Override
    public boolean performFilter(String filter) {
        return StringUtils.contains(new String[]{contactItem.getMsisdn(), contactItem.getDescription(), contactItem.getTitle()}, filter);
    }

    @Override
    public String getInitials() {
        return contactItem.getInitials();
    }

    @Override
    public String getTitle() {
        return contactItem.getTitle();
    }

    @Override
    public String getDescription() {
        return contactItem.getDescription();
    }

    @Override
    public String getPhoneNumber() {
        return contactItem.getMsisdn();
    }

    @Override
    public Bitmap getBitmap() {
        try {
            if (this.bitmap == null) {
                Uri uri = Uri.parse(contactItem.getPhotoUri());
                this.bitmap = BitmapCache.getInstance().getThumbnail(uri, PayCore.getAppContext());
            }
            setImageAvailable(true);
        } catch (Exception e) {
            if (TextUtils.isEmpty(contactItem.getDescription())) {
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

        if (!(x instanceof ContactsItemConsumer)) throw new ClassCastException();

        ContactsItemConsumer e = (ContactsItemConsumer) x;
        if (e.contactItem.getPinningTime() != this.contactItem.getPinningTime()) {
            return Long.compare(e.contactItem.getPinningTime(), this.contactItem.getPinningTime());
        } else {
            //return Long.compare(e.contactItem.getTransactionTime(), this.contactItem.getTransactionTime());
            return 0;
        }
    }

    public boolean isFavorite() {
        return contactItem.getPinningTime() > 0;
    }

    public void setFavorite(boolean favorite) {
        if (favorite) {
            contactItem.setPinningTime(System.currentTimeMillis());
        } else {
            contactItem.setPinningTime(0);
        }
    }

    public UserContact.Model getDbModel() {
        return contactItem.getDbModel();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ContactsItemConsumer) || ((ContactsItemConsumer) obj).contactItem == null)
            return false;

        return Objects.equals(contactItem.getMsisdn(), ((ContactsItemConsumer) obj).contactItem.getMsisdn());
    }
}
