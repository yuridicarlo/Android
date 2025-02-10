package it.bancomat.pay.consumer.network;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import com.appsflyer.AppsFlyerLib;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.AppAuthenticationResultCallback;
import it.bancomat.pay.consumer.BancomatApplication;
import it.bancomat.pay.consumer.network.callable.BiometricEnroll;
import it.bancomat.pay.consumer.network.callable.GetBanksConfigurationBank;
import it.bancomat.pay.consumer.network.callable.InitSdk;
import it.bancomat.pay.consumer.network.callable.ResendOtpCode;
import it.bancomat.pay.consumer.network.callable.StoreBiometricEnrollData;
import it.bancomat.pay.consumer.network.callable.UserMonitoring;
import it.bancomat.pay.consumer.network.callable.VerifyActivationCode;
import it.bancomat.pay.consumer.network.callable.VerifyOtpCode;
import it.bancomat.pay.consumer.network.callable.VerifyPin;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.network.dto.OtpData;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.network.dto.VerifyOtpCodeData;
import it.bancomat.pay.consumer.network.dto.VerifyPinData;
import it.bancomat.pay.consumer.network.dto.response.CallableVoid;
import it.bancomat.pay.consumer.network.dto.response.DtoGetAuthorizationTokenResponse;
import it.bancomat.pay.consumer.network.dto.response.DtoGetBanksConfigurationFileResponse;
import it.bancomat.pay.consumer.network.dto.response.DtoSetPinResponse;
import it.bancomat.pay.consumer.network.task.GetAuthorizationTokenTask;
import it.bancomat.pay.consumer.network.task.GetConfigurationBankFileTask;
import it.bancomat.pay.consumer.network.task.InitTask;
import it.bancomat.pay.consumer.network.task.InitUserTask;
import it.bancomat.pay.consumer.network.task.LoginTask;
import it.bancomat.pay.consumer.network.task.MigratePinTask;
import it.bancomat.pay.consumer.network.task.ModifyPinTask;
import it.bancomat.pay.consumer.network.task.PushRegistrationTask;
import it.bancomat.pay.consumer.network.task.SetCustomerJourneyConsentsTask;
import it.bancomat.pay.consumer.network.task.SetCustomerJourneyTagTask;
import it.bancomat.pay.consumer.network.task.SetPinTask;
import it.bancomat.pay.consumer.network.task.StoreLocatorGetCategoriesTask;
import it.bancomat.pay.consumer.network.task.StoreLocatorNearbyTask;
import it.bancomat.pay.consumer.network.task.StoreLocatorSearchOnlineTask;
import it.bancomat.pay.consumer.network.task.StoreLocatorSearchPhysicalTask;
import it.bancomat.pay.consumer.network.task.UserMonitoringTask;
import it.bancomat.pay.consumer.network.task.VerifyActivationCodeTask;
import it.bancomat.pay.consumer.network.task.VerifyOTPTask;
import it.bancomat.pay.consumer.network.task.VerifyPinTask;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.AppFlagModel;
import it.bancomat.pay.consumer.touchid.FingerprintDataManager;
import it.bancomat.pay.consumer.touchid.FingerprintState;
import it.bancomat.pay.consumer.utilities.AppCjUtils;
import it.bancomat.pay.consumer.utilities.BCMAuthenticationResultListener;
import it.bancomat.pay.consumer.utilities.PinUtilities;
import it.bancomat.pay.consumer.utilities.apptoapp.AppToAppData;
import it.bancomat.pay.consumer.utilities.apptoapp.AppToAppHelper;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.SessionManager;
import it.bancomatpay.sdk.core.HttpError;
import it.bancomatpay.sdk.core.OnCompleteListener;
import it.bancomatpay.sdk.core.PayCore;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.events.TaskEventError;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.storage.BancomatDataManager;
import it.bancomatpay.sdk.manager.task.CancelableTask;
import it.bancomatpay.sdk.manager.task.DelayTask;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.SingleTask;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.LoaderHelper;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;
import it.bancomatpay.sdkui.BCMCJTagEventCallback;
import it.bancomatpay.sdkui.BancomatFullStackSdk;
import it.bancomatpay.sdkui.listener.BCMFullStackCJEventListener;

public class BancomatPayApi implements BancomatPayApiInterface, OnCompleteListener {

    private static final String TAG = BancomatPayApi.class.getSimpleName();
    private static BancomatPayApi instance;

    private HashMap<String, Task<?>> taskMap;

    private BancomatPayApi() {
        taskMap = new HashMap<>();
    }

    public static synchronized BancomatPayApi getInstance() {
        if (instance == null) {
            instance = new BancomatPayApi();
        }
        return instance;
    }

    @Override
    public Task<?> doInit(OnCompleteResultListener<Void> listener) {
        Task<?> task = new InitTask(listener);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doInitUser(OnCompleteResultListener<Void> listener, String msisdn) {
        Task<?> task = new InitUserTask(listener, msisdn);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doGetBanksConfigurationFile(OnCompleteResultListener<DtoGetBanksConfigurationFileResponse> listener, String version) {
        Task<?> task = new GetConfigurationBankFileTask(listener, version);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public CallableVoid updateBanksConfigurationFile() {
        return new GetBanksConfigurationBank();
    }

    @Override
    public CallableVoid userMonitoring(String bankUUID, String tag, String event, String note) {
        return new UserMonitoring(bankUUID, tag, event, note);
    }

    @Override
    public Callable<VerifyActionCodeData> verifyActivationCode(String activationCode) {
        return new VerifyActivationCode(activationCode);
    }

    @Override
    public CallableVoid resendOtpCode(VerifyActionCodeData verifyActionCodeData) {
        return new ResendOtpCode(verifyActionCodeData);
    }

    @Override
    public Callable<VerifyOtpCodeData> verifyOtpCode(VerifyActionCodeData verifyActionCodeData, String otp) {
        return new VerifyOtpCode(verifyActionCodeData, otp);
    }

    @Override
    public Callable<BiometricEnrollData> biometricEnroll(String token, InstrumentData instrumentData) {
        return new BiometricEnroll(token, instrumentData);
    }

    @Override
    public CallableVoid storeBiometricEnrollData(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, BiometricEnrollData biometricEnrollData, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher) {
        return new StoreBiometricEnrollData(activity, biometric, biometricEnrollData, requestCode, activityResultLauncher);
    }

    @Override
    public CallableVoid initSdk(Activity activity, String abiCode, String groupCode, String phoneNumber, String phonePrefix, String iban) {
        return new InitSdk(activity, abiCode, groupCode, phoneNumber, phonePrefix, iban);
    }

    @Override
    public Callable<VerifyPinData> verifyPin(AppAuthenticationInterface authenticationInterface) {
        return new VerifyPin(authenticationInterface);
    }

    @Override
    public boolean isDeviceSecured() {
        KeyguardManager manager = (KeyguardManager) PayCore.getAppContext().getSystemService(Context.KEYGUARD_SERVICE);
        return manager.isDeviceSecure();
    }

    @Override
    public boolean isBiometricConfigured() {
        return isDeviceSecured() && !isBiometricMigrationNeed();
    }

    @Override
    public Task<?> doVerifyOtp(OnCompleteResultListener<OtpData> listener, String otp) {
        Task<?> task = new VerifyOTPTask(listener, otp);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doVerifyActivationCode(OnCompleteResultListener<VerifyActionCodeData> listener, String activationCode, String bankUuid) {
        Task<?> task = new VerifyActivationCodeTask(listener, activationCode, bankUuid);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doSetPin(OnCompleteResultListener<DtoSetPinResponse> listener, String pin, String token, String outgoingIban) {
        Task<?> task = new SetPinTask(listener, pin, token, outgoingIban);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public boolean isUserRegistered() {
        return AppBancomatDataManager.getInstance().getPskc() != null;
    }

    @Override
    public boolean isBiometricMigrationNeed() {
        return AppBancomatDataManager.getInstance().getPskc() != null && AppBancomatDataManager.getInstance().getDataAppAuthentication() == null;
    }

    @Override
    public boolean isShowBPlayPopup() {
        AppFlagModel appFlagModel = AppBancomatDataManager.getInstance().getFlagModel();
        if(appFlagModel.isShowBPlayPopup()){
            appFlagModel.setShowBPlayPopup(false);
            AppBancomatDataManager.getInstance().putFlagmodel(appFlagModel);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidPin(String pin) {
        return PinUtilities.checkPinFromClient(pin);
    }

    @Override
    public Result<String> getActivationCode(String rawCode64, String token) {
        String rawCode;
        try {
            byte[] data = Base64.decode(rawCode64, Base64.DEFAULT);
            rawCode = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            rawCode = "";
        }
        Result<String> result = new Result<>();
        if (!TextUtils.isEmpty(rawCode)) {
            CustomLogger.d(TAG, "raw Code: " + rawCode + "  token: " + token);
            String[] list = rawCode.split("\\?");
            if (list.length == 2 && list[0].equals(token)) {
                result.setStatusCode(StatusCode.Mobile.OK);
                result.setResult(list[1]);
                return result;
            }
        }
        result.setStatusCode(StatusCode.Server.QRCODE_WRONG_ACTIVATION_CODE);
        return result;
    }

    @Override
    public void deleteUserData() {
        AppBancomatDataManager.getInstance().putPskc(null);
        BancomatDataManager.getInstance().putAppsFlyerCustomerUserId("");
        AppBancomatDataManager.getInstance().deleteUserData();
        BancomatFullStackSdk.getInstance().resetBancomatSDK();
        resetAppsFlyerSdk();
    }

    @Override
    public Task<?> doLogin(OnCompleteResultListener<VerifyPinData> listener, AppAuthenticationInterface authenticationInterface) {
        Task<?> task = new LoginTask(listener, authenticationInterface);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doVerifyPin(OnCompleteResultListener<VerifyPinData> listener, AppAuthenticationInterface authenticationInterface) {
        Task<?> task = new VerifyPinTask(listener, authenticationInterface);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doGetAuthorizationToken(OnCompleteResultListener<DtoGetAuthorizationTokenResponse> mListener, AppAuthenticationInterface authenticationInterface, AuthenticationOperationType pinOperationType, String operationId, String amount, String msisdnSender, String receiver) {
        Task<?> task = new GetAuthorizationTokenTask(mListener, authenticationInterface, pinOperationType, operationId, amount, msisdnSender, receiver);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public boolean isFingerprintEnrolled() {
        return FingerprintDataManager.getInstance().getFingerprintState() == FingerprintState.ENABLED;
    }

    @Override
    public boolean isFingerprintEnrollable() {
        return FingerprintDataManager.getInstance().getFingerprintState() == FingerprintState.DISABLED
                || FingerprintDataManager.getInstance().getFingerprintState() == FingerprintState.ENABLED;
    }

    @Override
    public boolean isLoginRequired() {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        return flagModel.isLoginRequired() && !isBiometricMigrationNeed();
    }

    @Override
    public boolean showCjConsentsinHome() {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        return flagModel.isShowCJConsents();
    }


    @Override
    public boolean isProfilingConsentAllowed() {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        return flagModel.isProfilingAllowed();
    }

    @Override
    public boolean isMarketingConsentAllowed() {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        return flagModel.isMarketingAllowed();
    }

    @Override
    public boolean isDataToThirdPartiesConsentAllowed() {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        return flagModel.isDataToThirdPartiesAllowed();
    }

    @Override
    public boolean isBancomatSdkInitialized() {
        return !(BancomatDataManager.getInstance().getMobileDevice() == null
                || TextUtils.isEmpty(BancomatDataManager.getInstance().getMobileDevice().getUuid())
                || TextUtils.isEmpty(BancomatDataManager.getInstance().getUserAccountId()));
    }

    @Override
    public Result<AppToAppData> checkActivationFromDeepLink(Intent intent) {
        return AppToAppHelper.getActivationCodeIfExist(intent);
    }

    @Override
    public void storeBankUuidChoosed(String bankUuid) {
        AppBancomatDataManager.getInstance().putBankUuid(bankUuid);
    }

    @Override
    public String getBankUuidChoosed() {
        return AppBancomatDataManager.getInstance().getBankUuid();
    }

    @Override
    public void removeTouchId() {
        FingerprintDataManager.getInstance().delete();
    }

    @Override
    public void setLoginRequired(boolean isRequired) {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        flagModel.setLoginRequired(isRequired);
        dm.putFlagmodel(flagModel);
    }

    @Override
    public void setShowCjConsentsinHome(boolean isRequired) {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        flagModel.setShowCJConsents(isRequired);
        dm.putFlagmodel(flagModel);
    }

    @Override
    public void setProfilingConsent(boolean allowProfiling) {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        flagModel.setAllowProfiling(allowProfiling);
        dm.putFlagmodel(flagModel);
    }

    @Override
    public void setMarketingConsent(boolean allowMarketing) {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        flagModel.setAllowMarketing(allowMarketing);
        dm.putFlagmodel(flagModel);
    }

    @Override
    public void setDataToThirdPartiesConsent(boolean allowDataToThirdParties) {
        AppBancomatDataManager dm = AppBancomatDataManager.getInstance();
        AppFlagModel flagModel = dm.getFlagModel();
        flagModel.setAllowDataToThirdParties(allowDataToThirdParties);
        dm.putFlagmodel(flagModel);
    }

    @Override
    public Task<?> doModifyPinTask(OnCompleteResultListener<VerifyPinData> mListener, String oldPin, String newPin) {
        Task<?> task = new ModifyPinTask(mListener, oldPin, newPin);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public Task<?> doMigratePinTask(OnCompleteResultListener<VerifyPinData> mListener, String oldPin) {
        Task<?> task = new MigratePinTask(mListener, oldPin);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public boolean isDeviceOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void doSubscribePush(OnCompleteResultListener<Void> mListener, String token, String sessionToken) {
        AppBancomatDataManager.getInstance().putPushToken(token);
        if (isUserRegistered()) {
            PushRegistrationTask pushRegistrationTask = new PushRegistrationTask(mListener, token, sessionToken);
            pushRegistrationTask.start();
        }//else(deregister push Token?)
    }

    @Override
    public void setAuthenticationResultListener(@NonNull BCMAuthenticationResultListener listener) {
        AppAuthenticationResultCallback.getInstance().setAuthenticationResultListener(listener);
    }

    @Override
    public Task<?> doUserMonitoring(OnCompleteResultListener<Void> listener, String bankUuid, String tag, String event,String note) {
        Task<?> task = new UserMonitoringTask(listener, bankUuid, tag, event, note);
        task.setMasterListener(this);
        return task;
    }

    @Override
    public void doSetCustomerJourneyTag(OnCompleteResultListener<Void> listener, CustomerJourneyTag tag) {
        Task<?> t = SetCustomerJourneyTagTask.newInstance(listener, tag);
        t.setMasterListener(this);
        t.execute();
    }

    @Override
    public void doSetCustomerJourneyConsents(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, boolean allowProfiling, boolean allowMarketing, boolean allowDataToThirdParties, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new SetCustomerJourneyConsentsTask(listener, allowProfiling, allowMarketing, allowDataToThirdParties);
        t.setMasterListener(this);
        t.execute();
    }

    @Override
    public void doGetStoreLocator(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopList> listener, BcmLocation location, int page, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new StoreLocatorNearbyTask(listener, location, page);
        t.setMasterListener(this);
        t.execute();
    }

    @Override
    public void doGetStoreLocatorSearchOnlineRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopList> listener, String shopName, String categoryUuid, int page, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> tProgress = taskMap.get(activity.toString());
        if(tProgress != null) {
            tProgress.removeListener();
            taskMap.values().remove(tProgress);
        }

        CancelableTask<?> t = new StoreLocatorSearchOnlineTask(listener, shopName, categoryUuid, page);
        DelayTask<ShopList> delayTask = new DelayTask<>(listener, t, 1500);
        Task<?> singleTask = new SingleTask<>(listener, delayTask);
        addTask(activity, singleTask);
    }

    @Override
    public void doGetStoreLocatorSearchPhysicalRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopList> listener, String shopName, BcmLocation location, int page, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> tProgress = taskMap.get(activity.toString());
        if(tProgress != null) {
            tProgress.removeListener();
            taskMap.values().remove(tProgress);
        }

        CancelableTask<?> t = new StoreLocatorSearchPhysicalTask(listener, shopName, location, page);
        DelayTask<ShopList> delayTask = new DelayTask<>(listener, t, 1500);
        Task<?> singleTask = new SingleTask<>(listener, delayTask);
        addTask(activity, singleTask);
    }

    @Override
    public void doGetStoreLocatorCategories(@NonNull Activity activity, @NonNull OnCompleteResultListener<List<ShopCategory>> listener, String sessionToken) {
        SessionManager.getInstance().setSessionToken(sessionToken);
        Task<?> t = new StoreLocatorGetCategoriesTask(listener);
        t.setMasterListener(this);
        t.execute();
    }

    @Override
    public void setCustomerJourneyEventListener(@NonNull BCMFullStackCJEventListener listener) {
        BCMCJTagEventCallback.getInstance().setCJEventListener(listener);
    }

    @Override
    public void onComplete(Task<?> task) {
        if (!(task instanceof SetPinTask)) {
            LoaderHelper.dismissLoader();
        }
    }

    @Override
    public void onCompleteWithError(Task<?> task, Error e) {
        if (!(e instanceof HttpError) && !(task instanceof SetCustomerJourneyTagTask)) {
            EventBus.getDefault().post(new TaskEventError(task, e));
        }
        LoaderHelper.dismissLoader();
    }

    private void resetAppsFlyerSdk() {
        AppsFlyerLib.getInstance().stop(true, BancomatApplication.getAppContext());
        CustomLogger.d(TAG, "AppsFlyerLib stopped");
        AppCjUtils.updateAppsFlyerCuidIfNeeded();
    }

    private synchronized void addTask(Activity activity, Task<?> task) {
        if (taskMap == null) {
            taskMap = new HashMap<>();
        }
        if (task != null && !taskMap.containsValue(task)) {
            task.setMasterListener(this);
            taskMap.put(activity.toString(), task);
            task.execute();
        }
    }

}
