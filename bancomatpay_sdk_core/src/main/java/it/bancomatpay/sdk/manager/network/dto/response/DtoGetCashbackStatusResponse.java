package it.bancomatpay.sdk.manager.network.dto.response;

import java.io.Serializable;
import java.util.Date;

public class DtoGetCashbackStatusResponse implements Serializable {

	private boolean bpaySubscribed;
	private boolean bpayTermsAndConditionsAccepted;
	private boolean pagoPaCashbackEnabled;
	private Date bpaySubscribedTimestamp;
	private Date bpayUnsubscribedTimestamp;
	private String bpayTermsAndConditionsUrl;
	private String pagoPaTermsAndConditionsUrl;

	public boolean isBpaySubscribed() {
		return bpaySubscribed;
	}

	public void setBpaySubscribed(boolean bpaySubscribed) {
		this.bpaySubscribed = bpaySubscribed;
	}

	public boolean isBpayTermsAndConditionsAccepted() {
		return bpayTermsAndConditionsAccepted;
	}

	public void setBpayTermsAndConditionsAccepted(boolean bpayTermsAndConditionsAccepted) {
		this.bpayTermsAndConditionsAccepted = bpayTermsAndConditionsAccepted;
	}

	public boolean isPagoPaCashbackEnabled() {
		return pagoPaCashbackEnabled;
	}

	public void setPagoPaCashbackEnabled(boolean pagoPaCashbackEnabled) {
		this.pagoPaCashbackEnabled = pagoPaCashbackEnabled;
	}

	public Date getBpaySubscribedTimestamp() {
		return bpaySubscribedTimestamp;
	}

	public void setBpaySubscribedTimestamp(Date bpaySubscribedTimestamp) {
		this.bpaySubscribedTimestamp = bpaySubscribedTimestamp;
	}

	public Date getBpayUnsubscribedTimestamp() {
		return bpayUnsubscribedTimestamp;
	}

	public void setBpayUnsubscribedTimestamp(Date bpayUnsubscribedTimestamp) {
		this.bpayUnsubscribedTimestamp = bpayUnsubscribedTimestamp;
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
