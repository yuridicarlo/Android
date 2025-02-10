package it.bancomatpay.sdkui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bancomat.pay.eventbus.EventBusIndex;
import it.bancomatpay.sdk.BancomatSdk;
import it.bancomatpay.sdk.BancomatSdkInterface;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.RetrySessionTaskManager;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.network.dto.PaymentRequestType;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.storage.model.BankInfo;
import it.bancomatpay.sdk.manager.storage.model.EPlayServicesType;
import it.bancomatpay.sdk.manager.task.model.ContactItem;
import it.bancomatpay.sdk.manager.task.model.NotificationPaymentData;
import it.bancomatpay.sdk.manager.task.model.PaymentItem;
import it.bancomatpay.sdk.manager.task.model.QrCodeDetailsData;
import it.bancomatpay.sdk.manager.task.model.ShopItem;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.activities.HomeActivity;
import it.bancomatpay.sdkui.activities.TransactionListActivity;
import it.bancomatpay.sdkui.config.BancomatSdkConfig;
import it.bancomatpay.sdkui.flowmanager.AtmCardlessFlowManager;
import it.bancomatpay.sdkui.flowmanager.HomeFlowManager;
import it.bancomatpay.sdkui.flowmanager.PaymentFlowManager;
import it.bancomatpay.sdkui.flowmanager.PosWithdrawalFLowManager;
import it.bancomatpay.sdkui.listener.BCMFullStackAbortListener;
import it.bancomatpay.sdkui.listener.BCMFullStackAuthenticationListener;
import it.bancomatpay.sdkui.listener.BCMFullStackCompleteListener;
import it.bancomatpay.sdkui.listener.BCMFullStackKeepAliveListener;
import it.bancomatpay.sdkui.model.ConsumerPaymentData;
import it.bancomatpay.sdkui.model.ContactsItemConsumer;
import it.bancomatpay.sdkui.model.MerchantPaymentData;
import it.bancomatpay.sdkui.model.MerchantQrPaymentData;
import it.bancomatpay.sdkui.model.PaymentContactFlowType;
import it.bancomatpay.sdkui.model.ShopsDataMerchant;
import it.bancomatpay.sdkui.utilities.FullStackSdkDataManager;
import it.bancomatpay.sdkui.utilities.GoToHomeInterface;
import it.bancomatpay.sdkui.utilities.JsonFileUtil;
import it.bancomatpay.sdkui.utilities.LogoBankSingleton;

import static it.bancomatpay.sdk.manager.utilities.Constants.QR_CODE_BASE_URL;
import static it.bancomatpay.sdkui.config.BancomatSdkConfig.SDK_CONFIG_JSON_FILE_NAME;

public class BancomatFullStackSdk implements BancomatFullStackSdkInterface {

    private static final String TAG = BancomatFullStackSdk.class.getSimpleName();

    private static BancomatFullStackSdk instance;
    private static Set<Task<?>> taskSet;

    public static synchronized BancomatFullStackSdk getInstance() {
        if (instance == null) {
            instance = new BancomatFullStackSdk();
            taskSet = new HashSet<>();
        }
        return instance;
    }

    @Override
    public void initBancomatSDKWithCheckRoot(@NonNull Activity activity, String abiCode, String groupCode,
                                             String phoneNumber, String phonePrefix, String iban, String sessionToken, BCMFullStackCompleteListener listener) {

        //saveInsets(activity);

        if (TextUtils.isEmpty(abiCode) || TextUtils.isEmpty(groupCode)
                || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(phonePrefix)) {
            listener.onComplete(getResultInvalidParameter());
            LoaderHelper.dismissLoader();
        } else {

            Gson gson = new Gson();
            BancomatSdkConfig sdkConfig;
            try {
                sdkConfig = gson.fromJson(JsonFileUtil.loadJSONFromAsset(SDK_CONFIG_JSON_FILE_NAME), BancomatSdkConfig.class);
                sdkConfig.getGenericFlags().setNeedsCheckRoot(!BuildConfig.DEBUG);
            } catch (Exception e) {
                sdkConfig = new BancomatSdkConfig();
                sdkConfig.getGenericFlags().setNeedsCheckRoot(!BuildConfig.DEBUG);
                sdkConfig.getGenericFlags().setBlockIfRooted(!BuildConfig.DEBUG);
            }

            SessionManager.getInstance().setSessionToken(sessionToken);

            BancomatSdkInterface.Factory.getInstance().doInitSdk(activity, result -> {
                if (result != null && result.isSuccess()) {

                    BancomatSdkInterface.Factory.getInstance().doEnableUser(activity, resultEnableUser -> {
                        if (resultEnableUser != null && resultEnableUser.isSuccess()) {

                            Result<Void> resultSuccess = new Result<>();
                            resultSuccess.setStatusCode(StatusCode.Mobile.OK);
                            resultSuccess.setStatusCodeDetail(resultEnableUser.getStatusCodeDetail());
                            resultSuccess.setStatusCodeMessage("initBancomatSDKWithCheckRoot success");
                            listener.onComplete(resultSuccess);

                        } else {

                            Result<Void> resultError = new Result<>();
                            resultError.setStatusCode(StatusCode.Mobile.NOT_INITIALIZED);
                            resultError.setStatusCodeDetail(resultEnableUser != null ? resultEnableUser.getStatusCodeDetail() : "Error in doEnableUser");
                            resultError.setStatusCodeMessage("Error in doEnableUser");
                            listener.onComplete(resultError);

                        }
                    }, phonePrefix, phoneNumber, iban, sessionToken);

                } else {

                    Result<Void> resultError = new Result<>();
                    resultError.setStatusCode(StatusCode.Mobile.NOT_INITIALIZED);
                    resultError.setStatusCodeDetail(result != null ? result.getStatusCodeDetail() : "Error in doInitSdk");
                    resultError.setStatusCodeMessage("Error in doInitSdk");
                    listener.onComplete(resultError);

                }
            }, sdkConfig.getGenericFlags().isNeedsCheckRoot(), sdkConfig.getGenericFlags().isBlockIfRooted(), abiCode, groupCode);

            updatePrefixAndCountryCode(phonePrefix, phoneNumber);
        }
    }

    @Override
    public void startBancomatPayFlow(@NonNull Activity activity, Drawable logoBank, String sessionToken) {

        try {
            EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();
        } catch (EventBusException e) {
            CustomLogger.e(TAG, e.getMessage());
        }

        Gson gson = new Gson();
        BancomatSdkConfig sdkConfig;
        try {
            sdkConfig = gson.fromJson(JsonFileUtil.loadJSONFromAsset(SDK_CONFIG_JSON_FILE_NAME), BancomatSdkConfig.class);
        } catch (Exception e) {
            sdkConfig = new BancomatSdkConfig();
            sdkConfig.getGenericFlags().setHideSpendingLimits(false);
            sdkConfig.getAndroidFlags().setFileProviderAuthority("");
        }

        BankInfo bankInfo = BancomatDataManager.getInstance().getBankInfo();

        BancomatSdkInterface.Factory.getInstance().doInitSdk(activity, result -> {
            //Ignoriamo risultato initSdk, chiamata atta solo a recuperare la lista servizi aggiornata
        }, null, null, bankInfo.getAbiCode(), bankInfo.getGroupCode());

        SessionManager.getInstance().setSessionToken(sessionToken);
        LogoBankSingleton.getInstance().setLogoBank(logoBank);
        BancomatDataManager.getInstance().putFileProviderAuthority(sdkConfig.getAndroidFlags().getFileProviderAuthority());
        BancomatDataManager.getInstance().putHideSpendingLimits(sdkConfig.getGenericFlags().isHideSpendingLimits());

        BCMAbortCallback.getInstance().setSessionRefreshListener(refreshedToken -> {
            SessionManager.getInstance().setSessionToken(refreshedToken);
            retrySessionTask();
        });

        Intent intentHome = new Intent(activity, HomeActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intentHome);
    }

    public void saveInsets(Activity activity) {
        ViewCompat.setOnApplyWindowInsetsListener(activity.getWindow().getDecorView(), (v, insets) -> {
            int insetTop = insets.getSystemWindowInsetTop();
            int insetBottom = insets.getMandatorySystemGestureInsets().bottom;

            if (insetTop != 0) {
                BancomatDataManager.getInstance().putScreenInsetTop(insetTop);
            }

            BancomatDataManager.getInstance().putScreenInsetBottom(insetBottom);

            CustomLogger.d(TAG, "Inset top = " + insetTop);
            CustomLogger.d(TAG, "Inset bottom = " + insetBottom);

            return insets;
        });
    }

    @Override
    public void showBancomatPayHistory(@NonNull Activity activity, String sessionToken) {
        try {
            EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();
        } catch (EventBusException e) {
            CustomLogger.e(TAG, e.getMessage());
        }
        SessionManager.getInstance().setSessionToken(sessionToken);

        BCMAbortCallback.getInstance().setSessionRefreshListener(refreshedToken -> {
            SessionManager.getInstance().setSessionToken(refreshedToken);
            retrySessionTask();
        });

        Intent intentPaymentHistory = new Intent(activity, TransactionListActivity.class);
        intentPaymentHistory.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intentPaymentHistory);
    }

    @Override
    public void startPaymentFromNotification(@NonNull Activity activity, String paymentRequestId, String paymentRequestType, String sessionToken, @NonNull BCMFullStackCompleteListener listener) {
        if (TextUtils.isEmpty(paymentRequestId) || TextUtils.isEmpty(paymentRequestType)) {
            listener.onComplete(getResultInvalidParameter());
            LoaderHelper.dismissLoader();
        } else {

            try {
                EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();
            } catch (EventBusException e) {
                CustomLogger.e(TAG, e.getMessage());
            }
            SessionManager.getInstance().setSessionToken(sessionToken);

            BCMAbortCallback.getInstance().setSessionRefreshListener(refreshedToken -> {
                SessionManager.getInstance().setSessionToken(refreshedToken);
                retrySessionTask();
            });

            PaymentRequestType requestType;
            try {
                requestType = PaymentRequestType.fromValue(paymentRequestType);
            } catch (IllegalArgumentException e) {

                Result<Void> resultError = new Result<>();
                resultError.setStatusCode(EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                resultError.setStatusCodeDetail("Error in startPaymentFromNotification");
                resultError.setStatusCodeMessage("Error in startPaymentFromNotification");
                listener.onComplete(resultError);

                return;
            }

            BancomatSdkInterface.Factory.getInstance().doGetPaymentRequest(activity, result -> {
                        if (result != null) {
                            if (result.isSuccess()) {

                                Result<Void> resultSuccess = new Result<>();
                                resultSuccess.setStatusCode(StatusCode.Mobile.OK);
                                resultSuccess.setStatusCodeDetail(result.getStatusCodeDetail());
                                resultSuccess.setStatusCodeMessage(result.getStatusCodeMessage());
                                listener.onComplete(resultSuccess);

                                if (result.getResult().getNotificationPaymentData() != null
                                        && result.getResult().getNotificationPaymentData().size() > 0) {

                                    NotificationPaymentData item = result.getResult().getNotificationPaymentData().get(0);

                                    if (item.getItem() instanceof ShopItem) {
                                        ShopsDataMerchant shopsDataMerchant = new ShopsDataMerchant((ShopItem) item.getItem());
                                        MerchantPaymentData abstractPaymentData = new MerchantPaymentData();
                                        abstractPaymentData.setDisplayData(shopsDataMerchant);
                                        abstractPaymentData.setAmount(item.getPaymentItem().getAmount());
                                        abstractPaymentData.setCausal(item.getPaymentItem().getCausal());
                                        abstractPaymentData.setCentsAmount(item.getPaymentItem().getCentsAmount());
                                        abstractPaymentData.setPaymentId(item.getPaymentItem().getPaymentId());
                                        abstractPaymentData.setTillId(item.getPaymentItem().getTillId());

                                        abstractPaymentData.setTag(shopsDataMerchant.getShopItem().getTag());
                                        abstractPaymentData.setShopId(shopsDataMerchant.getShopItem().getShopId());

                                        abstractPaymentData.setRequestPayment(true);

                                        PaymentFlowManager.goToAcceptance(activity, abstractPaymentData, false);

                                    } else if (item.getItem() instanceof ContactItem) {

                                        ContactItem contact = new ContactItem();
                                        contact.setName(item.getPaymentItem().getInsignia());
                                        contact.setMsisdn(((ContactItem) item.getItem()).getMsisdn());
                                        contact.setType(item.getItem().getType());
                                        contact.setPhotoUri(((ContactItem) item.getItem()).getPhotoUri());
                                        contact.setContactId(((ContactItem) item.getItem()).getContactId());
                                        contact.setLetter(contact.getInitials());

                                        ContactsItemConsumer paymentDataConsumer = new ContactsItemConsumer(contact);
                                        ConsumerPaymentData abstractPaymentData = new ConsumerPaymentData();
                                        abstractPaymentData.setDisplayData(paymentDataConsumer);
                                        abstractPaymentData.setAmount(item.getPaymentItem().getAmount());
                                        abstractPaymentData.setCausal(item.getPaymentItem().getCausal());
                                        abstractPaymentData.setCentsAmount(item.getPaymentItem().getCentsAmount());
                                        abstractPaymentData.setPaymentId(item.getPaymentItem().getPaymentId());

                                        abstractPaymentData.setRequestPayment(true);

                                        PaymentFlowManager.goToAcceptance(activity, abstractPaymentData, false);
                                    }

                                } else {
                                    HomeFlowManager.goToNotifications(activity);
                                }

                            } else {
                                Result<Void> resultError = new Result<>();
                                resultError.setStatusCode(result.getStatusCode());
                                resultError.setStatusCodeDetail("Error in startPaymentFromNotification");
                                resultError.setStatusCodeMessage("Error in startPaymentFromNotification");
                                listener.onComplete(resultError);
                            }

                        }
                    },
                    paymentRequestId,
                    requestType,
                    SessionManager.getInstance().getSessionToken());

        }
    }

    @Override
    public void startPaymentFromQrCode(@NonNull Activity activity, String qrCodeString, String sessionToken, @NonNull BCMFullStackCompleteListener listener) {

        if (TextUtils.isEmpty(qrCodeString)) {
            listener.onComplete(getResultInvalidParameter());
            LoaderHelper.dismissLoader();
        } else {

            try {
                EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();
            } catch (EventBusException e) {
                CustomLogger.e(TAG, e.getMessage());
            }
            SessionManager.getInstance().setSessionToken(sessionToken);

            BCMAbortCallback.getInstance().setSessionRefreshListener(refreshedToken -> {
                SessionManager.getInstance().setSessionToken(refreshedToken);
                retrySessionTask();
            });

            BancomatSdkInterface.Factory.getInstance().doGetQrCodeDetails(activity, result -> {
                if (result != null) {
                    if (result.isSuccess()) {
                        QrCodeDetailsData detailsData = result.getResult();
                        if (isQrCodeStatic(qrCodeString)) {
                            if (detailsData.getShopItem() != null) {

                                Result<Void> resultSuccess = new Result<>();
                                resultSuccess.setStatusCode(StatusCode.Mobile.OK);
                                resultSuccess.setStatusCodeDetail(result.getStatusCodeDetail());
                                resultSuccess.setStatusCodeMessage(result.getStatusCodeMessage());
                                listener.onComplete(resultSuccess);

                                PaymentFlowManager.goToInsertAmount(activity,
                                        new ShopsDataMerchant(detailsData.getShopItem(), detailsData.getPaymentItem()), true, PaymentContactFlowType.SEND, false);

                            } else if (detailsData.getPaymentItem() != null) {

                                Result<Void> resultSuccess = new Result<>();
                                resultSuccess.setStatusCode(StatusCode.Mobile.OK);
                                resultSuccess.setStatusCodeDetail(result.getStatusCodeDetail());
                                resultSuccess.setStatusCodeMessage(result.getStatusCodeMessage());
                                listener.onComplete(resultSuccess);

                                if (result.getResult().getPaymentItem().getCategory() != null && result.getResult().getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.atmWithdrawal)) {
                                    AtmCardlessFlowManager.goToChooseAmount(activity,
                                            new MerchantQrPaymentData(detailsData.getPaymentItem(), false), false);
                                } else {
                                    PaymentFlowManager.goToConfirm(activity,
                                            new MerchantQrPaymentData(detailsData.getPaymentItem(), false), false, true);
                                }

                            } else {
                                Result<Void> resultError = new Result<>();
                                resultError.setStatusCode(EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                                resultError.setStatusCodeDetail(result.getStatusCodeDetail());
                                resultError.setStatusCodeMessage("Error in doGetQrCodeDetails");
                                listener.onComplete(resultError);
                            }
                        } else if (detailsData.getPaymentItem() != null && detailsData.getPaymentItem().getCategory() != null) {
                            if (detailsData.getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.atmWithdrawal)) {
                                Result<Void> resultSuccess = new Result<>();
                                resultSuccess.setStatusCode(StatusCode.Mobile.OK);
                                resultSuccess.setStatusCodeDetail(result.getStatusCodeDetail());
                                resultSuccess.setStatusCodeMessage(result.getStatusCodeMessage());
                                listener.onComplete(resultSuccess);

                                AtmCardlessFlowManager.goToChooseAmount(activity,
                                        new MerchantQrPaymentData(detailsData.getPaymentItem(), false), false);
                            } else if (result.getResult().getPaymentItem().getCategory().equals(PaymentItem.EPaymentCategory.posWithdrawal)) {
								Result<Void> resultSuccess = new Result<>();
								resultSuccess.setStatusCode(StatusCode.Mobile.OK);
								resultSuccess.setStatusCodeDetail(result.getStatusCodeDetail());
								resultSuccess.setStatusCodeMessage(result.getStatusCodeMessage());
								listener.onComplete(resultSuccess);

								PosWithdrawalFLowManager.goToPosWithdrawalPaymentData(activity,
										detailsData, false);
                            }
                        } else {

                            Result<Void> resultSuccess = new Result<>();
                            resultSuccess.setStatusCode(StatusCode.Mobile.OK);
                            resultSuccess.setStatusCodeDetail(result.getStatusCodeDetail());
                            resultSuccess.setStatusCodeMessage(result.getStatusCodeMessage());
                            listener.onComplete(resultSuccess);

                            PaymentFlowManager.goToQrPaymentData(activity, detailsData, true);

                        }
                    } else {
                        Result<Void> resultError = new Result<>();
                        resultError.setStatusCode(result.getStatusCode());
                        resultError.setStatusCodeDetail(result.getStatusCodeDetail());
                        resultError.setStatusCodeMessage("Error in doGetQrCodeDetails");
                        listener.onComplete(resultError);
                    }
                } else {

                    Result<Void> resultError = new Result<>();
                    resultError.setStatusCode(EBCMFullStackStatusCodes.SDKAbortType_GENERIC);
                    resultError.setStatusCodeDetail("Error in doGetQrCodeDetails");
                    resultError.setStatusCodeMessage("Error in doGetQrCodeDetails");
                    listener.onComplete(resultError);

                }
            }, qrCodeString, sessionToken);
        }
    }

    @Override
    public void setAuthenticationListener(@NonNull BCMFullStackAuthenticationListener listener) {
        BCMAuthenticationCallback.getInstance().setAuthenticationListener(listener);
    }

    @Override
    public void setAbortListener(@NonNull BCMFullStackAbortListener listener) {
        BCMAbortCallback.getInstance().setAbortListener(listener);
    }

    @Override
    public void setKeepAliveListener(@NonNull BCMFullStackKeepAliveListener listener) {
        BCMKeepAliveCallback.getInstance().setAuthenticationListener(listener);
    }

    @Override
    public void resetBancomatSDK() {
        FullStackSdkDataManager.getInstance().deleteUserData();
        BancomatSdk.getInstance().deleteUserData();
    }

    public void setReturnHomeListener(@NonNull GoToHomeInterface listener) {
        BCMReturnHomeCallback.getInstance().setReturnHomeListener(listener);
    }

    public boolean isQrCodeLinkPayment(String qrCode) {
        boolean bRet = true;
        if (!qrCode.contains(QR_CODE_BASE_URL)) {
            bRet = false;
        }
        return bRet;
    }

    public boolean isQrCodeStatic(String qrCodeString) {
        boolean isQrCodeStatic = false;
        if (qrCodeString.contains("#")) {
            String[] splitted = qrCodeString.split("#");
            isQrCodeStatic = !(splitted.length >= 2 && !TextUtils.isEmpty(splitted[1]));
        }
        return isQrCodeStatic;
    }

    private void updatePrefixAndCountryCode(String phonePrefix, String phoneNumber) {

        BancomatDataManager.getInstance().putPrefixCountryCode(phonePrefix);
        BancomatDataManager.getInstance().putUserPhoneNumber(phoneNumber);

        String completeNumber = phonePrefix + phoneNumber;
        try {
            Phonenumber.PhoneNumber phoneObj = PhoneNumberUtil.getInstance().parse(completeNumber, "ZZ");
            String countryCode = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(phoneObj.getCountryCode());
            BancomatDataManager.getInstance().putDefaultCountryCode(countryCode);
        } catch (NumberParseException e) {
            CustomLogger.e(TAG, "Get country code for number failed: " + e.getMessage());
        }
    }

    public boolean isQrCodeAtmCardless(QrCodeDetailsData detailsData) {
        boolean isQrCodeAtmCardless = false;
        if (detailsData != null && detailsData.getPaymentItem() != null
                && detailsData.getPaymentItem().getAmount() != null
                && detailsData.getPaymentItem().getAmount().equals(BigDecimal.ZERO)) {
            isQrCodeAtmCardless = true;
        }
        return isQrCodeAtmCardless;
    }

    public boolean hasGooglePlayServices() {
        return BancomatDataManager.getInstance().getPlayServicesType() == EPlayServicesType.GOOGLE_PLAY_SERVICES;
    }

    public boolean hasHuaweiServices() {
        return BancomatDataManager.getInstance().getPlayServicesType() == EPlayServicesType.HUAWEI_SERVICES;
    }

    protected void retrySessionTask() {
        List<Task<?>> taskRetryList = RetrySessionTaskManager.getInstance().getRetrySessionTaskList();
        if (taskRetryList != null) {
            for (Task<?> taskRetry : taskRetryList) {
                if (taskRetry != null && !taskSet.contains(taskRetry)) {
                    taskRetry.setMasterListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Task<?> task) {
                            taskSet.remove(taskRetry);
                            taskRetry.removeListener();
                            RetrySessionTaskManager.getInstance().removeTaskFromList(task);
                            CustomLogger.d(TAG, "Retry task master listener completed for task " + task.getClass().getSimpleName());
                            LoaderHelper.dismissLoader();
                        }

                        @Override
                        public void onCompleteWithError(Task<?> task, Error e) {
                            taskSet.remove(taskRetry);
                            taskRetry.removeListener();
                            RetrySessionTaskManager.getInstance().removeTaskFromList(task);
                            CustomLogger.e(TAG, "Retry task master listener error: " + e.toString());
                            LoaderHelper.dismissLoader();
                        }
                    });
                    taskSet.add(taskRetry);
                    taskRetry.execute();
                    CustomLogger.d(TAG, "Retrying task " + taskRetry.getClass().getSimpleName());
                }
            }
        }
    }

    @NonNull
    private Result<Void> getResultInvalidParameter() {
        Result<Void> result = new Result<>();
        result.setStatusCode(StatusCode.Mobile.INVALID_PARAMETER);
        result.setStatusCodeDetail(StatusCode.Mobile.INVALID_PARAMETER.toString());
        result.setStatusCodeMessage("Invalid parameter, check mandatory parameters");
        return result;
    }

}
