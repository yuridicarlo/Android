package it.bancomatpay.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.db.LoyaltyCardFrequent;
import it.bancomatpay.sdk.manager.db.UserDbHelper;
import it.bancomatpay.sdk.manager.db.UserFrequent;
import it.bancomatpay.sdk.manager.events.TaskEventError;
import it.bancomatpay.sdk.manager.events.request.ContactRequest;
import it.bancomatpay.sdk.manager.model.BCMOperation;
import it.bancomatpay.sdk.manager.model.BCMPaymentRequest;
import it.bancomatpay.sdk.manager.network.dto.DtoBrand;
import it.bancomatpay.sdk.manager.network.dto.DtoDocument;
import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;
import it.bancomatpay.sdk.manager.network.dto.response.DtoGetShopListResponse;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.FlagModel;
import it.bancomatpay.sdk.manager.task.AcceptCashbackBpayTermsAndConditionsTask;
import it.bancomatpay.sdk.manager.task.AtmConfirmWithdrawalTask;
import it.bancomatpay.sdk.manager.task.BackgroundSingleton;
import it.bancomatpay.sdk.manager.task.ConfirmBankIdRequestTask;
import it.bancomatpay.sdk.manager.task.ConfirmDirectDebitRequestTask;
import it.bancomatpay.sdk.manager.task.ConfirmPetrolPaymentTask;
import it.bancomatpay.sdk.manager.task.ConfirmPosWithdrawalTask;
import it.bancomatpay.sdk.manager.task.DeleteBankIdBlacklistTask;
import it.bancomatpay.sdk.manager.task.DenyBankIdRequestTask;
import it.bancomatpay.sdk.manager.task.DenyDirectDebitsRequestTask;
import it.bancomatpay.sdk.manager.task.DisableCashbackTask;
import it.bancomatpay.sdk.manager.task.GetBankIdBlacklistTask;
import it.bancomatpay.sdk.manager.task.GetBankIdContactsTask;
import it.bancomatpay.sdk.manager.task.GetBankIdRequestsTask;
import it.bancomatpay.sdk.manager.task.GetBankIdStatusTask;
import it.bancomatpay.sdk.manager.task.GetCashbackDataTask;
import it.bancomatpay.sdk.manager.task.GetCashbackStatusTask;
import it.bancomatpay.sdk.manager.task.GetDirectDebitsHistoryTask;
import it.bancomatpay.sdk.manager.task.GetLoyaltyJwtTask;
import it.bancomatpay.sdk.manager.task.GetRectImageTask;
import it.bancomatpay.sdk.manager.task.GetSquareImageTask;
import it.bancomatpay.sdk.manager.task.InitSdkTask;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.OnProgressResultListener;
import it.bancomatpay.sdk.manager.task.SetBankIdContactsTask;
import it.bancomatpay.sdk.manager.task.SetBankIdStatusTask;
import it.bancomatpay.sdk.manager.task.SubscribeCashbackTask;
import it.bancomatpay.sdk.manager.task.UnsubscribeCashbackTask;
import it.bancomatpay.sdk.manager.task.model.AllowPaymentRequest;
import it.bancomatpay.sdk.manager.task.model.AtmConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.task.model.BankIdAddress;
import it.bancomatpay.sdk.manager.task.model.BankIdContactsData;
import it.bancomatpay.sdk.manager.task.model.BankIdMerchantData;
import it.bancomatpay.sdk.manager.task.model.BankIdRequestsData;
import it.bancomatpay.sdk.manager.task.model.BcmDocument;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.CameraImageProcessingResult;
import it.bancomatpay.sdk.manager.task.model.CashbackData;
import it.bancomatpay.sdk.manager.task.model.CashbackStatusData;
import it.bancomatpay.sdk.manager.task.model.ConfirmPetrolPaymentData;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.DenyPaymentData;
import it.bancomatpay.sdk.manager.task.model.DirectDebitsHistoryData;
import it.bancomatpay.sdk.manager.task.model.DocumentImages;
import it.bancomatpay.sdk.manager.task.model.DocumentsData;
import it.bancomatpay.sdk.manager.task.model.EBankIdStatus;
import it.bancomatpay.sdk.manager.task.model.FrequentItem;
import it.bancomatpay.sdk.manager.task.model.ItemInterface;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCard;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardBrandsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyCardsData;
import it.bancomatpay.sdk.manager.task.model.LoyaltyJwtData;
import it.bancomatpay.sdk.manager.task.model.NotificationData;
import it.bancomatpay.sdk.manager.task.model.OutgoingPaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentHistoryData;
import it.bancomatpay.sdk.manager.task.model.PaymentRequestData;
import it.bancomatpay.sdk.manager.task.model.PollingData;
import it.bancomatpay.sdk.manager.task.model.PosConfirmWithdrawalData;
import it.bancomatpay.sdk.manager.task.model.ProfileData;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdk.manager.task.model.SplitBeneficiary;
import it.bancomatpay.sdk.manager.task.model.SplitBillHistory;
import it.bancomatpay.sdk.manager.task.model.SyncPhoneBookData;
import it.bancomatpay.sdk.manager.task.model.TransactionDetails;
import it.bancomatpay.sdk.manager.task.model.UserData;
import it.bancomatpay.sdk.manager.task.model.UserThresholds;
import it.bancomatpay.sdk.manager.task.model.VerifyPaymentData;
import it.bancomatpay.sdk.manager.utilities.ApplicationModel;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.MockUtils;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCodeInterface;

import static it.bancomatpay.sdk.manager.utilities.Constants.DISPLACEMENT;

public class BancomatSdk implements BancomatSdkInterface, OnCompleteListener {

	private static final String TAG = BancomatSdk.class.getSimpleName();

	private static BancomatSdk instance;

	public static synchronized BancomatSdk getInstance() {
		if (instance == null) {
			instance = new BancomatSdk();
		}
		return instance;
	}

    @Override
    public void doInitSdk(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, Boolean needsCheckRoot, Boolean blockIfRooted, String abiCode, String groupCode) {
        if (TextUtils.isEmpty(abiCode) || TextUtils.isEmpty(groupCode)) {
            listener.onComplete((Result<Void>) getResultInvalidParameter());
        } else {
            Task<?> t = TaskManager.getInstance().doInitSdk(listener, needsCheckRoot, blockIfRooted, abiCode, groupCode);
            addTask(activity, t);
        }
    }

    @Override
    public void doEnableUser(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String phonePrefix, String phoneNumber, String iban, String sessionToken) {
        if (TextUtils.isEmpty(phonePrefix) || TextUtils.isEmpty(phoneNumber)) {
            listener.onComplete((Result<Void>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doEnableUser(listener, phonePrefix, phoneNumber, iban);
            addTask(activity, t);
        }
    }

    public void deleteUserData() {
        BancomatDataManager.getInstance().deleteUserData();
        UserDbHelper.getInstance().deleteContactsFrequent();
        UserDbHelper.getInstance().deleteLoyaltyCardFrequent();
        ApplicationModel.getInstance().resetApplicationModel();
    }

    @Override
    public void getUserData(@NonNull Activity activity, @NonNull OnCompleteResultListener<UserData> listener, String sessionToken) {
        Task<?> t = BackgroundSingleton.getInstance().userDataRequest(activity, listener, sessionToken);
        addTask(activity, t);
    }

    public ProfileData getProfileData() {
        ProfileData profileData = new ProfileData();
        UserData userData = ApplicationModel.getInstance().getUserData();
        if (userData != null) {
            HashMap<String, ContactItem> map = ApplicationModel.getInstance().getContactItemHashMap();
            if (map != null && map.containsKey(userData.getMsisdn())) {
                ContactItem contactUser = map.get(userData.getMsisdn());
                if (contactUser != null) {
                    profileData.setImage(contactUser.getImage());
                } else {
                    profileData.setImage(null);
                }
            }
            String letter = "";
            if (!TextUtils.isEmpty(userData.getName())) {
                letter = userData.getName().substring(0, 1).toUpperCase();
            }
            if (!TextUtils.isEmpty(userData.getSurname())) {
                letter += userData.getSurname().substring(0, 1).toUpperCase();
            }
            profileData.setLetter(letter);
            profileData.setIban(userData.getIban());
            profileData.setP2BThresholds(userData.getP2BThresholds());
            profileData.setP2PThresholds(userData.getP2PThresholds());
            profileData.setMsisdn(userData.getMsisdn());
            profileData.setName(userData.getName() + " " + userData.getSurname());
            profileData.setDefaultReceiver(userData.isDefaultReceiver());
        }
        return profileData;
    }

    @Override
    public void getSyncPhoneBook(@NonNull Activity activity, @NonNull OnCompleteResultListener<SyncPhoneBookData> listener, boolean forced, String sessionToken) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Task<?> t = BackgroundSingleton.getInstance().contactRequest(activity, new ContactRequest(forced), sessionToken,
                    result -> {
                        if (result != null && result.isSuccess()) {
                            ApplicationModel.getInstance().setContactItems(result.getResult().getContactItems());
                            if (!result.getResult().isContactsSynced()) {
                                Task<?> tSyncPhoneBook = BackgroundSingleton.getInstance().syncPhoneBook(
                                        activity, listener, false, sessionToken);
                                addTask(activity, tSyncPhoneBook);
                            } else {
                                listener.onComplete(result);
                            }
                        } else {
                            listener.onComplete(result);
                        }
            });
            addTask(activity, t);
            //listener.onComplete(MockUtils.fakeSyncPhoneBookDataResponse());
        } else {
            Result<SyncPhoneBookData> resultError = new Result<>();
            resultError.setStatusCode(StatusCode.Mobile.PERMISSION_REFUSED);
            resultError.setStatusCodeDetail("Error in getSyncPhoneBook");
            resultError.setStatusCodeMessage("Permission READ_CONTACTS is not granted");
            listener.onComplete(resultError);
        }
    }

    @Override
    public void getShopList(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<ShopItem>> listener, BcmLocation location, String sessionToken) {
        BcmLocation lastLocation = ApplicationModel.getInstance().getLastLocation();
        if (!isDeviceOnline(activity)) {
            Result<ArrayList<ShopItem>> resultOffline = new Result<>();
            resultOffline.setResult(null);
            resultOffline.setStatusCode(StatusCode.Http.NO_RETE);
            listener.onComplete(resultOffline);
        } else if (location != null && (lastLocation == null || location.distanceTo(lastLocation) >= DISPLACEMENT)
                || location != null && ApplicationModel.getInstance().getShopItems() == null) {
            ApplicationModel.getInstance().setLastLocation(location);
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = BackgroundSingleton.getInstance().shopListRequest(activity, location, listener);
            addTask(activity, t);
        } else if (ApplicationModel.getInstance().getShopItems() != null) {
            Result<ArrayList<ShopItem>> shopItemResult = new Result<>();
            shopItemResult.setResult(ApplicationModel.getInstance().getShopItems());
            shopItemResult.setStatusCode(StatusCode.Mobile.OK);
            listener.onComplete(shopItemResult);
        }
    }

    @Override
    public void doGetShopByMerchantNameList(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<ShopItem>> listener, String name, BcmLocation location, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetShopByMerchantNameList(listener, name, location);
        addTask(activity, t);
    }

    @Override
    public void doGetMerchantDetail(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopItem> listener, String msisdn, String sessionToken) {
        if (TextUtils.isEmpty(msisdn)) {
            listener.onComplete((Result<ShopItem>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doGetMerchantDetail(listener, msisdn);
            addTask(activity, t);
        }
    }

    @Override
    public void doGetMerchantDetail(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopItem> listener, String tag, String shopId, String sessionToken) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(shopId)) {
            listener.onComplete((Result<ShopItem>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doGetMerchantDetail(listener, tag, shopId);
            addTask(activity, t);
        }
    }

    @Override
    public void doVerifyPaymentP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<VerifyPaymentData> listener, @NonNull BCMOperation payment, String sessionToken) {
        if (TextUtils.isEmpty(payment.getMsisdn()) || payment.getAmount() == 0) {
            listener.onComplete((Result<VerifyPaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doVerifyPaymentP2P(
                    listener, payment.getMsisdn(), payment.getAmount(),
                    payment.getPaymentRequestId(), payment.getCausal());
            addTask(activity, t);
        }
    }

    @Override
    public void doVerifyPaymentP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<VerifyPaymentData> listener, @NonNull BCMOperation payment, String sessionToken) {
        if (TextUtils.isEmpty(payment.getTag()) || payment.getShopId() == 0 || payment.getAmount() == 0) {
            listener.onComplete((Result<VerifyPaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doVerifyPaymentP2B(
                    listener, payment.getTag(), payment.getShopId(), payment.getTillId(), payment.getAmount());
            addTask(activity, t);
        }
    }

    @Override
    public void doConfirmPaymentP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, @NonNull BCMOperation payment, String sessionToken, String authorizationToken) {
        if (payment.getItemInterface() == null || TextUtils.isEmpty(payment.getPaymentId()) || TextUtils.isEmpty(payment.getMsisdn()) || payment.getAmount() == 0) {
            listener.onComplete((Result<PaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doConfirmPaymentP2P(
                    listener, payment.getItemInterface(), payment.getPaymentId(), payment.getPaymentRequestId(),
                    payment.getMsisdn(), payment.getAmount(), payment.getCausal(),
                    authorizationToken);
            addTask(activity, t);
        }
    }

    @Override
    public void doConfirmPaymentP2B(@NonNull Activity activity, @NonNull OnProgressResultListener<PaymentData> listener, @NonNull BCMOperation payment, String sessionToken, String authorizationToken) {
        if (TextUtils.isEmpty(payment.getTag()) || payment.getShopId() == 0 || payment.getAmount() == 0) {
            listener.onComplete((Result<PaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doConfirmPaymentP2B(
                    listener, payment.getItemInterface(), payment.getPaymentId(), payment.getTag(),
                    payment.getShopId(), payment.getTillId(), payment.getAmount(), payment.getCausal(),
                    payment.getQrCodeId(), payment.isQrCodeEmpsa(), authorizationToken);
            addTask(activity, t);
        }
    }

    @Override
    public void doCancelPreauthorization(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String paymentId, String paymentRequestId, String amount, String msisdnSender, String sessionToken) {
        if (TextUtils.isEmpty(paymentId) || TextUtils.isEmpty(paymentRequestId) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(msisdnSender)) {
            listener.onComplete((Result<Void>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doCancelPreauthorization(listener, paymentId, paymentRequestId, amount, msisdnSender);
            addTask(activity, t);
        }
    }

    @Override
    public void doGetQrCodeDetails(@NonNull Activity activity, @NonNull OnCompleteResultListener<QrCodeDetailsData> listener, String qrCodeId, String sessionToken) {
        if (TextUtils.isEmpty(qrCodeId)) {
            listener.onComplete((Result<QrCodeDetailsData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doGetQrCodeDetails(listener, qrCodeId);
            addTask(activity, t);
        }
    }

    @Override
    public void doGetPaymentHistory(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentHistoryData> listener, String msisdn, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetPaymentHistory(listener, msisdn);
        addTask(activity, t);
    }

    @Override
    public void doAllowPaymentRequestP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<AllowPaymentRequest> listener, boolean allow, String msisdn, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doAllowPaymentRequestP2P(listener, allow, msisdn);
        addTask(activity, t);
    }

    @Override
    public void doAllowPaymentRequestP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<AllowPaymentRequest> listener, boolean allow, String msisdn, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doAllowPaymentRequestP2B(listener, allow, msisdn);
        addTask(activity, t);
    }

    @Override
    public void doGetBlacklistContacts(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<ContactItem>> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetBlacklistContacts(listener);
        addTask(activity, t);
    }

    @Override
    public void doCancelPayment(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String paymentId, String description, String sessionToken) {
        if (TextUtils.isEmpty(paymentId)) {
            listener.onComplete((Result<Void>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doCancelPayment(listener, paymentId, description);
            addTask(activity, t);
        }
    }

    @Override
    public void doSetIncomingDefaultInstrument(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String iban, String sessionToken) {
        if (TextUtils.isEmpty(iban)) {
            listener.onComplete((Result<Void>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doSetIncomingDefaultInstrument(listener, iban);
            addTask(activity, t);
        }
    }

    @Override
    public void doSetOutgoingDefaultInstrument(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String iban, String sessionToken) {
        if (TextUtils.isEmpty(iban)) {
            listener.onComplete((Result<Void>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doSetOutgoingDefaultInstrument(listener, iban);
            addTask(activity, t);
        }
    }

    @Override
    public void doConfirmPaymentRequestP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, String sessionToken, String authorizationToken) {
        if (TextUtils.isEmpty(paymentRequest.getPaymentId()) || TextUtils.isEmpty(paymentRequest.getPaymentRequestId()) || TextUtils.isEmpty(paymentRequest.getMsisdn()) || paymentRequest.getAmount() == 0) {
            listener.onComplete((Result<PaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doConfirmPaymentRequestP2P(
                    listener, paymentRequest.getItemInterface(), paymentRequest.getPaymentId(), paymentRequest.getPaymentRequestId(),
                    paymentRequest.getMsisdn(), paymentRequest.getAmount(), paymentRequest.getCausal(),
                    authorizationToken);
            addTask(activity, t);
        }
    }

    @Override
    public void doConfirmPaymentRequestP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, String sessionToken, String authorizationToken) {
        if (TextUtils.isEmpty(paymentRequest.getPaymentId()) || TextUtils.isEmpty(paymentRequest.getTag()) || paymentRequest.getShopId() == 0 || paymentRequest.getAmount() == 0) {
            listener.onComplete((Result<PaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doConfirmPaymentRequestP2B(
                    listener, paymentRequest.getItemInterface(), paymentRequest.getPaymentId(), paymentRequest.getTag(),
                    paymentRequest.getShopId(), paymentRequest.getTillId(), paymentRequest.getAmount(), paymentRequest.getCausal(),
                    authorizationToken);
            addTask(activity, t);
        }
    }

    @Override
    public void doDenyPaymentRequestP2P(@NonNull Activity activity, @NonNull OnCompleteResultListener<DenyPaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, boolean addToBlackList, String sessionToken) {
        if (TextUtils.isEmpty(paymentRequest.getPaymentId()) || TextUtils.isEmpty(paymentRequest.getMsisdn())) {
            listener.onComplete((Result<DenyPaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doDenyPaymentRequestP2P(
                    listener, paymentRequest.getPaymentId(), paymentRequest.getMsisdn(),
                    addToBlackList);
            addTask(activity, t);
        }
    }

    @Override
    public void doDenyPaymentRequestP2B(@NonNull Activity activity, @NonNull OnCompleteResultListener<DenyPaymentData> listener, @NonNull BCMPaymentRequest paymentRequest, String denyReason, String sessionToken) {
        if (TextUtils.isEmpty(paymentRequest.getPaymentId())) {
            listener.onComplete((Result<DenyPaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doDenyPaymentRequestP2B(
                    listener, paymentRequest.getPaymentId(),
                    denyReason);
            addTask(activity, t);
        }
    }

    @Override
    public void doVerifyPaymentState(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentData> listener, String paymentId, PollingData pollingData, String sessionToken) {
        if (TextUtils.isEmpty(paymentId)) {
            listener.onComplete((Result<PaymentData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doVerifyPaymentState(listener, paymentId, pollingData);
            addTask(activity, t);
        }
    }

    @Override
    public void doSendPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentRequestData> listener, List<String> msisdnBeneficiary, String amount, String causal, ItemInterface itemInterface, String sessionToken) {
        if (msisdnBeneficiary == null || msisdnBeneficiary.isEmpty() || TextUtils.isEmpty(amount) || itemInterface == null) {
            listener.onComplete((Result<PaymentRequestData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doSendPaymentRequest(listener, msisdnBeneficiary, amount, causal, itemInterface);
            addTask(activity, t);
        }
    }

    @Override
    public void doGetOutgoingPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<OutgoingPaymentRequestData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetOutgoingPaymentRequest(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetTransactionDetails(@NonNull Activity activity, @NonNull OnCompleteResultListener<TransactionDetails> listener, String paymentId, boolean isP2p, String sessionToken) {
        if (TextUtils.isEmpty(paymentId)) {
            listener.onComplete((Result<TransactionDetails>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doGetTransactionDetails(listener, paymentId, isP2p);
            addTask(activity, t);
        }
    }

    public ArrayList<FrequentItem> getUserFrequent() {
        BackgroundSingleton.getInstance().requestFrequentItem();
        return ApplicationModel.getInstance().getFrequentItems();
    }

    public void updateUserFrequent(ArrayList<FrequentItem> frequentItems) {
        List<UserFrequent.Model> modelList = new ArrayList<>();
        for (FrequentItem frequentItem : frequentItems) {
            modelList.add(frequentItem.getDbModel());
        }
        UserDbHelper.getInstance().saveUserFrequent(modelList);
    }

    @Override
    public void doDisableUser(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doDisableUser(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<NotificationData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetPaymentRequest(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetThresholds(@NonNull Activity activity, @NonNull OnCompleteResultListener<UserThresholds> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task t = TaskManager.getInstance().doGetThresholds(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetPaymentRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<NotificationData> listener, String paymentRequestId, PaymentRequestType paymentRequestType, String sessionToken) {
        if (TextUtils.isEmpty(paymentRequestId) || paymentRequestType == null) {
            listener.onComplete((Result<NotificationData>) getResultInvalidParameter());
        } else {
            SessionManager.getInstance().setSessionToken(sessionToken);
            Task<?> t = TaskManager.getInstance().doGetPaymentRequest(listener, paymentRequestId, paymentRequestType);
            addTask(activity, t);
        }
    }

    @Override
    public void doGetLoyaltyCards(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyCardsData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetLoyaltyCards(listener);
        addTask(activity, t);
    }

    @Override
    public void doSetLoyaltyCard(@NonNull Activity activity, @NonNull OnCompleteResultListener<String> listener, String brandUuid, String barCodeNumber, String barCodeType, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doSetLoyaltyCard(listener, brandUuid, barCodeNumber, barCodeType, cardType, hexColor, brandName, brandLogoImage);
        addTask(activity, t);
    }

    @Override
    public void doDeleteLoyaltyCard(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String barCodeNumber, String loyaltyCardId, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doDeleteLoyaltyCard(listener, barCodeNumber, loyaltyCardId);
        addTask(activity, t);
    }

    @Override
    public void doModifyLoyaltyCard(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyCard> listener, String barCodeNumber, String barCodeType, String loyaltyCardId, String brandUuid, DtoBrand.LoyaltyCardTypeEnum cardType, String hexColor, String brandName, String brandLogoImage, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doModifyLoyaltyCard(listener, barCodeNumber, barCodeType, loyaltyCardId, brandUuid, cardType, hexColor, brandName, brandLogoImage);
        addTask(activity, t);
    }

    @Override
    public void doGetLoyaltyCardBrands(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyCardBrandsData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetLoyaltyCardBrands(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetDocuments(@NonNull Activity activity, @NonNull OnCompleteResultListener<DocumentsData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetDocumentList(listener);
        addTask(activity, t);
    }


    @Override
    public void doGetDocumentImages(@NonNull Activity activity, @NonNull OnCompleteResultListener<DocumentImages> listener, String documentUuid, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doGetDocumentImages(listener, documentUuid);
        addTask(activity, t);
    }

    @Override
    public void doSetDocument(@NonNull Activity activity, @NonNull OnCompleteResultListener<String> listener, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstituion, String issuingDate, String expirationDate, String note, String documentName, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doSetDocument(listener, documentType, documentNumber, surname, name, fiscalCode, issuingInstituion, issuingDate, expirationDate, note, documentName);
        addTask(activity, t);
    }

    @Override
    public void doSetDocumentImages(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String documentUuid, String frontImage, String backImage, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doSetDocumentImages(listener, documentUuid, frontImage, backImage);
        addTask(activity, t);
    }

    @Override
    public void doModifyDocument(@NonNull Activity activity, @NonNull OnCompleteResultListener<BcmDocument> listener,  String documentUuid, DtoDocument.DocumentTypeEnum documentType, String documentNumber, String surname, String name, String fiscalCode, String issuingInstitution, String issuingDate, String expirationDate, String note, String documentName, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doModifyDocument(listener, documentUuid, documentType, documentNumber, surname, name, fiscalCode, issuingInstitution, issuingDate, expirationDate, note, documentName);
        addTask(activity, t);
    }

    @Override
    public void doDeleteDocument(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String documentUuid, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = TaskManager.getInstance().doDeleteDocument(listener, documentUuid);
        addTask(activity, t);
    }

    @Override
    public void doGetBankIdBlacklist(@NonNull Activity activity, @NonNull OnCompleteResultListener<ArrayList<BankIdMerchantData>> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new GetBankIdBlacklistTask(listener);
        addTask(activity, t);
    }

    @Override
    public void doDeleteBankIdBlacklist(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String merchantTag, String merchantName, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new DeleteBankIdBlacklistTask(listener, merchantTag, merchantName);
        addTask(activity, t);
    }

    @Override
    public void doGetBankIdRequests(@NonNull Activity activity, @NonNull OnCompleteResultListener<BankIdRequestsData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new GetBankIdRequestsTask(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetBankIdContacts(@NonNull Activity activity, @NonNull OnCompleteResultListener<BankIdContactsData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new GetBankIdContactsTask(listener);
        addTask(activity, t);
    }

    @Override
    public void doSetBankIdContacts(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String email, List<BankIdAddress> bankIdAddresses, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new SetBankIdContactsTask(listener, email, bankIdAddresses);
        addTask(activity, t);
    }

    @Override
    public void doConfirmBankIdRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, String tag, String authorizationToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new ConfirmBankIdRequestTask(listener, requestId, tag, authorizationToken);
        addTask(activity, t);
    }

    @Override
    public void doConfirmDirectDebitsRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, String tag, String authorizationToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new ConfirmDirectDebitRequestTask(listener, requestId, tag, authorizationToken);
        addTask(activity, t);
    }

    @Override
    public void doGetBankIdStatus(@NonNull Activity activity, @NonNull OnCompleteResultListener<EBankIdStatus> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new GetBankIdStatusTask(listener);
        addTask(activity, t);
    }

    @Override
    public void doSetBankIdStatus(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, EBankIdStatus status, String sessionToken){
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new SetBankIdStatusTask(listener, status);
        addTask(activity, t);
    }

	@Override
	public void doDenyBankIdRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, boolean blockMerchant, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new DenyBankIdRequestTask(listener, requestId, blockMerchant);
		addTask(activity, t);
	}

    @Override
    public void doDenyDirectDebitsRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String requestId, String merchantTag, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new DenyDirectDebitsRequestTask(listener, requestId, merchantTag);
        addTask(activity, t);
    }

    @Override
	public void doConfirmAtmWithdrawal(@NonNull Activity activity, @NonNull OnCompleteResultListener<AtmConfirmWithdrawalData> listener, String msisdn, String tag, long shopId, String amount, String authorizationToken, String qrCodeId, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new AtmConfirmWithdrawalTask(listener, msisdn, tag, shopId, amount, authorizationToken, qrCodeId);
		addTask(activity, t);
	}

    @Override
    public void doConfirmPosWithdrawal(@NonNull Activity activity, @NonNull OnCompleteResultListener<PosConfirmWithdrawalData> listener, String tag, long shopId, BigInteger tillId, String amount, String totalAmount, String authorizationToken, String qrCodeId, String causal, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new ConfirmPosWithdrawalTask(listener, tag, shopId, tillId, amount, totalAmount, authorizationToken, qrCodeId, causal);
        addTask(activity, t);
    }

    @Override
    public void doConfirmPetrolPayment(@NonNull Activity activity, @NonNull OnCompleteResultListener<ConfirmPetrolPaymentData> listener, String tag, String shopId, BigInteger tillId, String amount, String authorizationToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
	    Task<?> t = new ConfirmPetrolPaymentTask(listener, tag, shopId, tillId, amount, authorizationToken);
        addTask(activity, t);
    }

    @Override
    public void doGetLoyaltyJwt(@NonNull Activity activity, @NonNull OnCompleteResultListener<LoyaltyJwtData> listener, String loyaltyToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new GetLoyaltyJwtTask(listener, loyaltyToken);
        addTask(activity, t);
    }

    @Override
    public void doGetDirectDebitsHistory(@NonNull Activity activity, @NonNull OnCompleteResultListener<DirectDebitsHistoryData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new GetDirectDebitsHistoryTask(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetCashbackStatus(@NonNull Activity activity, @NonNull OnCompleteResultListener<CashbackStatusData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new GetCashbackStatusTask(listener);
        addTask(activity, t);
    }


    @Override
    public void doSubscribeCashback(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String authorizationToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new SubscribeCashbackTask(listener, authorizationToken);
        addTask(activity, t);
    }

    @Override
    public void doUnsubscribeCashback(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String authorizationToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new UnsubscribeCashbackTask(listener, authorizationToken);
        addTask(activity, t);
    }

    @Override
    public void doDisableCashback(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String authorizationToken, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new DisableCashbackTask(listener, authorizationToken);
        addTask(activity, t);
    }

    @Override
    public void doAcceptCashbackBpayTermsAndConditions(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, String timestamp, String sessionToken) {
    SessionManager.getInstance().setSessionToken(sessionToken);
    Task<?> t = new AcceptCashbackBpayTermsAndConditionsTask(listener, timestamp);
    addTask(activity, t);
    }

    @Override
    public void doGetCashbackData(@NonNull Activity activity, @NonNull OnCompleteResultListener<CashbackData> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new GetCashbackDataTask(listener);
        addTask(activity, t);
    }

    @Override
    public void doSplitBill(@NonNull Activity activity, @NonNull OnCompleteResultListener<PaymentRequestData> listener, @NonNull ArrayList<? extends SplitBeneficiary> contactItems, @NonNull String totalAmount, @NonNull String causal, String description, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = TaskManager.getInstance().doSplitBill(listener, contactItems, totalAmount, causal, description);
        addTask(activity, t);
    }

    @Override
    public void doGetSplitBillHistory(@NonNull Activity activity, @NonNull OnCompleteResultListener<List<SplitBillHistory>> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = TaskManager.getInstance().doGetSplitBillHistory(listener);
        addTask(activity, t);
    }

    @Override
    public void doGetSplitBillHistoryDetail(@NonNull Activity activity, @NonNull OnCompleteResultListener<List<SplitBeneficiary>> listener, String splitBillUUID, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = TaskManager.getInstance().doGetSplitBillHistoryDetail(listener, splitBillUUID);
        addTask(activity, t);
    }

    @Override
    public synchronized void removeTasksListener(@NonNull Activity activity) {
        if (taskMap != null) {
            for (int i = 0; i <= taskMap.size(); i++) {
                if (taskMap.containsKey(activity.toString())) {
                    Task<?> currentTask = taskMap.get(activity.toString());
                    if (currentTask != null) {
                        currentTask.removeListener();
                        taskMap.values().remove(currentTask);
                        CustomLogger.d(TAG, "Removed listener for task in " + activity.getClass().getSimpleName());
                    }
                }
            }
        }
    }

    public void getRectImageFromPhoto(@NonNull Activity activity, @NonNull OnCompleteResultListener<CameraImageProcessingResult> listener, @NonNull byte[] imageData, @NonNull String fileName, boolean isCardDocument) {
        Task<?> t = new GetRectImageTask(activity, listener, imageData, fileName, isCardDocument);
        addTask(activity, t);
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void updateLoyaltyCardFrequent(LoyaltyCard loyaltyCard) {
        LoyaltyCardFrequent.Model cardModel = new LoyaltyCardFrequent.Model();
        cardModel.setLoyaltyCardId(loyaltyCard.getLoyaltyCardId());
        cardModel.setOperationCounter(loyaltyCard.getOperationCounter()+1);
        UserDbHelper.getInstance().updateLoyaltyCardFrequent(cardModel);
    }

    public void getSquareImageFromPhoto(@NonNull Activity activity, @NonNull OnCompleteResultListener<CameraImageProcessingResult> listener, @NonNull byte[] imageData, @NonNull String fileName) {
        Task<?> t = new GetSquareImageTask(listener, imageData, fileName);
        addTask(activity, t);
    }

    public boolean checkBarcodeFormatValidity(String barcode, String barcodeType) {
        boolean bRet = true;

        int size = 100;
        MultiFormatWriter barcodeWriter = new MultiFormatWriter();

        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
        if (!TextUtils.isEmpty(barcodeType)) {
            try {
                barcodeFormat = BarcodeFormat.valueOf(barcodeType);
            } catch (IllegalArgumentException e) {
                CustomLogger.e(TAG, "Error in parsing BarcodeType of card, actual barcode type is: " + barcodeType);
            }
        }

        try {
            BitMatrix barcodeBitMatrix = barcodeWriter.encode(barcode, barcodeFormat, size, size);
            Bitmap barcodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    barcodeBitmap.setPixel(x, y, barcodeBitMatrix.get(x, y) ?
                            Color.BLACK : Color.TRANSPARENT);
                }
            }
        } catch (Exception e) {
            CustomLogger.e(TAG, "checkBarcodeFormatValidity error: " + e.getMessage());
            bRet = false;
        }

        return bRet;
    }

    public boolean isDeviceOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

    public AllowPaymentRequest getAllowPaymentRequest() {
        BancomatDataManager dm = BancomatDataManager.getInstance();
        FlagModel flagModel = dm.getFlagModel();
        AllowPaymentRequest allowPaymentRequest = new AllowPaymentRequest();
        allowPaymentRequest.setForP2B(flagModel.isAllowP2BPaymentRequest());
        allowPaymentRequest.setForP2P(flagModel.isAllowP2PPaymentRequest());
        return allowPaymentRequest;
    }

    // ================================================= //

    private HashMap<String, Task<?>> taskMap;

    private synchronized void addTask(Activity activity, Task<?> task) {
        if (taskMap == null) {
            taskMap = new HashMap<>();
        }
        if (task != null && !taskMap.containsValue(task)) {
            //RetrySessionTaskManager.getInstance().addRetrySessionTask(task);
            task.setMasterListener(this);
            taskMap.put(activity.toString(), task);
            task.execute();
        }
    }

    @Override
    public void onComplete(Task<?> task) {
        task.removeListener();
        taskMap.values().remove(task);
        if (!(task instanceof InitSdkTask)) {
            LoaderHelper.dismissLoader();
        }
    }

    @Override
    public void onCompleteWithError(Task<?> task, Error error) {
        if (error instanceof HttpError) {
            if (((HttpError) error).getCode() == 401) {
                CustomLogger.w("onCompleteWithError", "SESSION EXPIRED, received 401");
            } else {
                EventBus.getDefault().post(new TaskEventError(task, error));
            }
        } else {
            EventBus.getDefault().post(new TaskEventError(task, error));
        }
        task.removeListener();
        taskMap.values().remove(task);
        LoaderHelper.dismissLoader();
    }

    @NonNull
    private Result<?> getResultInvalidParameter() {
        CustomLogger.d("INVALID_PARAMETER", "Invalid parameter, check mandatory parameters");
        Result<?> result = new Result<>();
        result.setStatusCode(StatusCode.Mobile.INVALID_PARAMETER);
        result.setStatusCodeDetail(StatusCode.Mobile.INVALID_PARAMETER.toString());
        result.setStatusCodeMessage("Invalid parameter, check mandatory parameters");
        return result;
    }

}
