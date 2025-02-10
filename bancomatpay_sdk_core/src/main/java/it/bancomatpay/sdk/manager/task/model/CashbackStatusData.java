package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;
import java.util.Date;

public class CashbackStatusData implements Serializable {

    private boolean bPaySubscribed;
    private boolean bPayTermsAndConditionsAccepted;
    private boolean pagoPaCashbackEnabled;
    private Date bPaySubscribedTimestamp;
    private Date bPayUnsubscribedTimestamp;
    private String bpayTermsAndConditionsUrl;
    private String pagoPaTermsAndConditionsUrl;

    public boolean isbPaySubscribed() {
        return bPaySubscribed;
    }

    public void setbPaySubscribed(boolean bPaySubscribed) {
        this.bPaySubscribed = bPaySubscribed;
    }

    public Date getbPaySubscribedTimestamp() {
        return bPaySubscribedTimestamp;
    }

    public void setbPaySubscribedTimestamp(Date bPaySubscribedTimestamp) {
        this.bPaySubscribedTimestamp = bPaySubscribedTimestamp;
    }

    public Date getbPayUnsubscribedTimestamp() {
        return bPayUnsubscribedTimestamp;
    }

    public void setbPayUnsubscribedTimestamp(Date bPayUnsubscribedTimestamp) {
        this.bPayUnsubscribedTimestamp = bPayUnsubscribedTimestamp;
    }

    public boolean isbPayTermsAndConditionsAccepted() {
        return bPayTermsAndConditionsAccepted;
    }

    public void setbPayTermsAndConditionsAccepted(boolean bPayTermsAndConditionsAccepted) {
        this.bPayTermsAndConditionsAccepted = bPayTermsAndConditionsAccepted;
    }

    public boolean isPagoPaCashbackEnabled() {
        return pagoPaCashbackEnabled;
    }

    public void setPagoPaCashbackEnabled(boolean pagoPaCashbackEnabled) {
        this.pagoPaCashbackEnabled = pagoPaCashbackEnabled;
    }

    public String getBpayTermsAndConditionsUrl() {
        return bpayTermsAndConditionsUrl;
    }

    public void setBpayTermsAndConditionsUrl(String bpayTermsAndConditionsUrl) {
        this.bpayTermsAndConditionsUrl = bpayTermsAndConditionsUrl;
    }

    public String getPagoPaTermsAndConditionsUrl() {
        return pagoPaTermsAndConditionsUrl;
    }

    public void setPagoPaTermsAndConditionsUrl(String pagoPaTermsAndConditionsUrl) {
        this.pagoPaTermsAndConditionsUrl = pagoPaTermsAndConditionsUrl;
    }
}
