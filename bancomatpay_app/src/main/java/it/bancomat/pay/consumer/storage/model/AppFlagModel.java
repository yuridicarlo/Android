package it.bancomat.pay.consumer.storage.model;

public class AppFlagModel {

    private boolean isLoginRequired = false;
    private boolean allowP2PPaymentRequest = true;
    private boolean allowP2BPaymentRequest = true;
    private boolean showBalanceInHome = false;
    private boolean hasRecent = false;
    private boolean orderContactsPerName = true;
    private boolean isShowCJConsents = true;
    private boolean allowProfiling = true;
    private boolean allowMarketing = false;
    private boolean allowDataToThirdParties = false;
    private boolean showBPlayPopup = false;

    public boolean isProfilingAllowed() {
        return allowProfiling;
    }

    public void setAllowProfiling(boolean allowProfiling) {
        this.allowProfiling = allowProfiling;
    }

    public boolean isMarketingAllowed() {
        return allowMarketing;
    }

    public void setAllowMarketing(boolean allowMarketing) {
        this.allowMarketing = allowMarketing;
    }

    public boolean isDataToThirdPartiesAllowed() {
        return allowDataToThirdParties;
    }

    public void setAllowDataToThirdParties(boolean allowDataToThirdParties) {
        this.allowDataToThirdParties = allowDataToThirdParties;
    }

    public boolean isShowCJConsents() {
        return isShowCJConsents;
    }

    public void setShowCJConsents(boolean showCJConsents) {
        isShowCJConsents = showCJConsents;
    }

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

    public boolean isShowBPlayPopup() {
        return showBPlayPopup;
    }

    public void setShowBPlayPopup(boolean showBPlayPopup) {
        this.showBPlayPopup = showBPlayPopup;
    }
}
