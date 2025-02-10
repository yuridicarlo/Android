package it.bancomatpay.sdk.manager.task.model;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.Serializable;

import it.bancomatpay.sdk.manager.db.UserContact;

public class ContactItem implements ItemInterface, Serializable {

    private long contactId;
    private String name;
    private String msisdn;
    private String photoUri;
    private Type type = Type.NONE;
    private String letter;
    private boolean isBlocked;

    private UserContact.Model dbModel;

    public UserContact.Model getDbModel() {
        return dbModel;
    }

    public void setDbModel(UserContact.Model dbModel) {
        this.dbModel = dbModel;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getLetter() {
        if (!TextUtils.isEmpty(letter)) {
            return letter.toUpperCase();
        }
        return "";
    }

    public String getLetterSurname() {
        String letterSurname = letter.toUpperCase();
        if (getTitle().contains(" ")) {
            int spaceIndex = getTitle().lastIndexOf(" ");
            letterSurname = getTitle().substring(spaceIndex).substring(1, 2).toUpperCase();
        }
        return letterSurname;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getInitials() {
        if (getTitle().contains(" ") && getTitle().length() > 1) {
            String first = getTitle().substring(0, 1);
            String[] nameList = getTitle().split(" ");
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
        return getLetter();
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getTitle() {
        if (TextUtils.isEmpty(name)) {
            return msisdn;
        } else {
            return name;
        }
    }

    @Override
    public String getDescription() {
        if (!TextUtils.isEmpty(name)) {
            return msisdn;
        } else {
            return "";
        }
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public long getPinningTime() {
        if (dbModel != null) {
            return dbModel.getPinningTime();
        } else {
            return 0;
        }
    }

    public void setPinningTime(long pinningTime) {
        if (dbModel != null) {
            this.dbModel.setPinningTime(pinningTime);
        } else {
            this.dbModel = new UserContact.Model();
            this.dbModel.setPinningTime(pinningTime);
        }
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public String getImage() {
        return photoUri;
    }

    @Override
    public String getPhoneNumber() {
        return msisdn;
    }

    @Override
    public long getId() {
        return contactId;
    }

    @Override
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @NonNull
    @Override
    public String toString() {
        return "ContactItem{" +
                "contactId=" + contactId +
                ", name='" + name + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", type=" + type +
                ", letter='" + letter + '\'' +
                '}';
    }

}
