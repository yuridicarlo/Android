package it.bancomatpay.sdkui.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.utilities.Constants;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.BCMCJTagEventCallback;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_INSERT_CODE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_READ_QR;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_AUTHENTICATION_SETTED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_BACK_FROM_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_AUTHORIZATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_BIOMETRIC_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_CHANGE_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_MERCHANT_SELECTED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_OPEN_FROM_NOTIFICATION_LIST;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_OPEN_FROM_PUSH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_OPEN_MAP;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_PAYMENT_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_PAYMENT_REQUEST_DENY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_PIN_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2B_SHARE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_ADD_NUMBER;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_BACK_FROM_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_AUTHORIZATION;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_BIOMETRIC_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CHANGE_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CONFIRM;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CONFIRM_AMOUNT;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_CONTACT_SELECTED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_OPEN_FROM_NOTIFICATION_LIST;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_OPEN_FROM_PUSH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_PAYMENT_COMPLETED;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_PAYMENT_REQUEST_DENY;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_PIN_AUTH;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_REQUEST_FAILED_SHARE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_SAVE_NUMBER;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_SHARE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_P2P_TYPE;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_PRE_LOGIN_SELECT_BANK;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_QR_CODE_SCAN;
import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_STORE_LOCATOR;

public class CjUtils {

	private static final String TAG = CjUtils.class.getSimpleName();

	private static CjUtils mInstance;

	public static CjUtils getInstance() {
		if (mInstance == null) {
			mInstance = new CjUtils();
		}
		return mInstance;
	}

	public void sendCustomerJourneyTagEvent(@NonNull Context context, @NonNull String eventKey,
	                                        @Nullable HashMap<String, String> mapEventParams, boolean hasExecutionId) {
		if (Constants.CUSTOMER_JOURNEY_ENABLED) {
			CustomerJourneyTag tag = new CustomerJourneyTag();
			tag.setTagTimestamp(getCurrentTimestampString());
			tag.setTagKey(eventKey);
			if (mapEventParams != null && !mapEventParams.isEmpty()) {
				tag.setTagJsonData(getJsonStringFromParams(mapEventParams));
			}
			if (hasExecutionId) {
				tag.setTagExecutionId(getExecutionIdForFlowKey(eventKey));
			}
			tag.setCuid(BancomatDataManager.getInstance().getAppsFlyerCustomerUserId());

			BCMCJTagEventCallback.getInstance().getCJEventListener()
					.onSendCJTagEvent(context, tag);

			CustomLogger.d(TAG, "sendCustomerJourneyTagEvent: " + tag.toString());
		} else {
			CustomLogger.d(TAG, "sendCustomerJourneyTagEvent: Customer Journey not enabled");
		}
	}

	private String getJsonStringFromParams(HashMap<String, String> jsonMap) {
		StringBuilder sRet = new StringBuilder("{");
		for (Map.Entry<String, String> entryPair : jsonMap.entrySet()) {
			sRet.append("\"").append(entryPair.getKey()).append("\":\"").append(entryPair.getValue()).append("\",");
		}
		sRet.deleteCharAt(sRet.length() - 1).append("}");
		return sRet.toString();
	}

	private String getCurrentTimestampString() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
	}

	public String getPaymentDateTimestampString(Date paymentDate) {
		if (paymentDate != null) {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(paymentDate);
		}
		return null;
	}

	private String getExecutionIdForFlowKey(String flowKey) {
		switch (flowKey) {
			case KEY_PRE_LOGIN_SELECT_BANK:
			case KEY_ACTIVATION_READ_QR:
			case KEY_ACTIVATION_INSERT_CODE:
			case KEY_ACTIVATION_COMPLETED:
			case KEY_ACTIVATION_AUTHENTICATION_SETTED:
				return getActivationExecutionId();

			case KEY_P2B_MERCHANT_SELECTED:
			case KEY_P2B_OPEN_MAP:
			case KEY_P2B_CHANGE_AMOUNT:
			case KEY_P2B_BIOMETRIC_AUTH:
			case KEY_P2B_AUTHORIZATION:
			case KEY_P2B_PIN_AUTH:
			case KEY_P2B_BACK_FROM_AMOUNT:
			case KEY_P2B_SHARE:
			case KEY_P2B_PAYMENT_COMPLETED:
			case KEY_P2B_OPEN_FROM_PUSH:
			case KEY_P2B_OPEN_FROM_NOTIFICATION_LIST:
			case KEY_P2B_PAYMENT_REQUEST_DENY:
			case KEY_QR_CODE_SCAN:
				return getP2bPaymentExecutionId();

			case KEY_P2P_CONTACT_SELECTED:
			case KEY_P2P_BACK_FROM_AMOUNT:
			case KEY_P2P_CHANGE_AMOUNT:
			case KEY_P2P_CONFIRM_AMOUNT:
			case KEY_P2P_CONFIRM:
			case KEY_P2P_SHARE:
			case KEY_P2P_PAYMENT_COMPLETED:
			case KEY_P2P_BIOMETRIC_AUTH:
			case KEY_P2P_AUTHORIZATION:
			case KEY_P2P_PIN_AUTH:
			case KEY_P2P_TYPE:
			case KEY_P2P_ADD_NUMBER:
			case KEY_P2P_OPEN_FROM_PUSH:
			case KEY_P2P_OPEN_FROM_NOTIFICATION_LIST:
			case KEY_P2P_PAYMENT_REQUEST_DENY:
			case KEY_P2P_REQUEST_FAILED_SHARE:
			case KEY_P2P_SAVE_NUMBER:
				return getP2pPaymentExecutionId();
			case KEY_STORE_LOCATOR:
				return getStoreLocatorExecutionId();
			default:
				return "";
		}
	}

	private long activationStartTimestamp;
	private long p2bOpenMapStartTimestamp;
	private long p2bPaymentStartTimestamp;
	private long p2pPaymentStartTimestamp;

	private String activationExecutionId;
	private String p2bPaymentExecutionId;
	private String p2pPaymentExecutionId;
	private String storeLocatorExecutionId;

	public void startActivationFlow() {
		activationStartTimestamp = System.currentTimeMillis();
		activationExecutionId = UUID.randomUUID().toString().toUpperCase();
	}

	public String getActivationTimeElapsed() {
		return String.valueOf(System.currentTimeMillis() - activationStartTimestamp);
	}

	public void startP2BOpenMap() {
		p2bOpenMapStartTimestamp = System.currentTimeMillis();
	}

	public String getP2BOpenMapTimeElapsed() {
		return String.valueOf(System.currentTimeMillis() - p2bOpenMapStartTimestamp);
	}

	public void startP2BPaymentFlow() {
		p2bPaymentStartTimestamp = System.currentTimeMillis();
		p2bPaymentExecutionId = UUID.randomUUID().toString().toUpperCase();
	}

	public String getP2BPaymentTimeElapsed() {
		return String.valueOf(System.currentTimeMillis() - p2bPaymentStartTimestamp);
	}

	public void startP2PPaymentFlow() {
		p2pPaymentStartTimestamp = System.currentTimeMillis();
		p2pPaymentExecutionId = UUID.randomUUID().toString().toUpperCase();
	}

	public String getP2PPaymentTimeElapsed() {
		return String.valueOf(System.currentTimeMillis() - p2pPaymentStartTimestamp);
	}

	public String getActivationExecutionId() {
		return activationExecutionId;
	}

	public String getP2bPaymentExecutionId() {
		return p2bPaymentExecutionId;
	}

	public String getP2pPaymentExecutionId() {
		return p2pPaymentExecutionId;
	}

	public void startStoreLocatorFlow() {
		storeLocatorExecutionId = UUID.randomUUID().toString().toUpperCase();
	}

	public String getStoreLocatorExecutionId() { return storeLocatorExecutionId; }

}
