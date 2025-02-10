package it.bancomatpay.sdk.manager.storage.model;

public class FlagModel {

    private boolean isLoginRequired = false;
    private boolean allowP2PPaymentRequest = true;
    private boolean allowP2BPaymentRequest = true;
    private boolean showBalanceInHome = false;
    private boolean hasRecent = false;
    private boolean orderContactsPerName = true;
    private boolean showBPlayPopUp = false;
    private boolean showStoreLocatorPopUp = true;

    public boolean isLoginRequired() {
        return isLoginRequired;
    }

    public void setLoginRequired(boolean loginRequired) {
        isLoginRequired = loginRequired;
    }

    public boolean isShowBalanceInHome() {
        return showBalanceInHome;
    }

    public void setShowBalanceInHome(boolean showBalanceInHome) {
        this.showBalanceInHome = showBalanceInHome;
    }

    public boolean hasRecent() {
        return hasRecent;
    }

    public void setHasRecent(boolean hasRecent) {
        this.hasRecent = hasRecent;
    }

    public boolean isAllowP2PPaymentRequest() {
        return allowP2PPaymentRequest;
    }

    public void setAllowP2PPaymentRequest(boolean allowP2PPaymentRequest) {
        this.allowP2PPaymentRequest = allowP2PPaymentRequest;
    }

    public boolean isAllowP2BPaymentRequest() {
        return allowP2BPaymentRequest;
    }

    public void setAllowP2BPaymentRequest(boolean allowP2BPaymentRequest) {
        this.allowP2BPaymentRequest = allowP2BPaymentRequest;
    }

    public boolean isOrderContactsPerName() {
        return orderContactsPerName;
    }

    public void setOrderContactsPerName(boolean orderContactsPerName) {
        this.orderContactsPerName = orderContactsPerName;
    }

    public boolean isShowStoreLocatorPopUp() {
        return showStoreLocatorPopUp;
    }

    public void setShowStoreLocatorPopUp(boolean showStoreLocatorPopUp) {
        this.showStoreLocatorPopUp = showStoreLocatorPopUp;
    }
}
