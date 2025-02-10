package it.bancomatpay.sdk.manager.task.model;

import java.util.ArrayList;

public class NotificationData {

	private ArrayList<NotificationPaymentData> notificationPaymentData;
	private ArrayList<BankIdRequest> notificationBankIdRequest;
	private ArrayList<DirectDebitRequest> notificationDirectDebitRequests;

	public ArrayList<DirectDebitRequest> getNotificationDirectDebitRequests() {
		return notificationDirectDebitRequests;
	}

	public void setNotificationDirectDebitRequests(ArrayList<DirectDebitRequest> notificationDirectDebitRequests) {
		this.notificationDirectDebitRequests = notificationDirectDebitRequests;
	}

	public ArrayList<NotificationPaymentData> getNotificationPaymentData() {
		return notificationPaymentData;
	}

	public void setNotificationPaymentDatas(ArrayList<NotificationPaymentData> transactionData) {
		this.notificationPaymentData = transactionData;
	}

	public ArrayList<BankIdRequest> getNotificationBankIdRequest() {
		return notificationBankIdRequest;
	}

	public void setNotificationBankIdRequest(ArrayList<BankIdRequest> notificationBankIdRequest) {
		this.notificationBankIdRequest = notificationBankIdRequest;
	}

}
