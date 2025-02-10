package it.bancomatpay.sdk;

import android.text.TextUtils;

public class SessionManager {

    private static SessionManager instance;

    private String mSessionToken;

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String sessionToken) {
        if (!TextUtils.isEmpty(sessionToken)) {
            this.mSessionToken = sessionToken;
        }
    }

}
