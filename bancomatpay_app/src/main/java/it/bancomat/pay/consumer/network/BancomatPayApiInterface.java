package it.bancomat.pay.consumer.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.List;
import java.util.concurrent.Callable;

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
import it.bancomat.pay.consumer.utilities.BCMAuthenticationResultListener;
import it.bancomat.pay.consumer.utilities.apptoapp.AppToAppData;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.Task;
import it.bancomatpay.sdk.manager.model.AuthenticationOperationType;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.task.model.BcmLocation;
import it.bancomatpay.sdk.manager.task.model.CustomerJourneyTag;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdk.manager.task.model.ShopCategory;
import it.bancomatpay.sdk.manager.task.model.ShopList;
import it.bancomatpay.sdkui.listener.BCMFullStackCJEventListener;

public interface BancomatPayApiInterface {

    Task<?> doInit(OnCompleteResultListener<Void> listener);

    Task<?> doInitUser(OnCompleteResultListener<Void> listener, String msisdn);

    Task<?> doGetBanksConfigurationFile(OnCompleteResultListener<DtoGetBanksConfigurationFileResponse> listener, String version);

    Task<?> doVerifyOtp(OnCompleteResultListener<OtpData> listener, String otp);

    Task<?> doVerifyActivationCode(OnCompleteResultListener<VerifyActionCodeData> listener, String activationCode, String bankUuid);

    Task<?> doSetPin(OnCompleteResultListener<DtoSetPinResponse> listener, String pin, String token, String outgoingIban);

    boolean isUserRegistered();

    boolean isBiometricMigrationNeed();

    boolean isShowBPlayPopup();

    boolean isValidPin(String pin);

    Result<String> getActivationCode(String rawCode, String token);

    void deleteUserData();

    Task<?> doLogin(OnCompleteResultListener<VerifyPinData> listener, AppAuthenticationInterface authenticationInterface);

    Task<?> doVerifyPin(OnCompleteResultListener<VerifyPinData> listener, AppAuthenticationInterface authenticationInterface);

    Task<?> doGetAuthorizationToken(OnCompleteResultListener<DtoGetAuthorizationTokenResponse> mListener, AppAuthenticationInterface authenticationInterface, AuthenticationOperationType pinOperationType, String operationId, String amount, String msisdnSender, String receiver);

    boolean isFingerprintEnrolled();

    boolean isFingerprintEnrollable();

    boolean isLoginRequired();

    boolean showCjConsentsinHome();

    boolean isProfilingConsentAllowed();

    boolean isMarketingConsentAllowed();

    boolean isDataToThirdPartiesConsentAllowed();

    boolean isBancomatSdkInitialized();

    Result<AppToAppData> checkActivationFromDeepLink(Intent intent);

    void storeBankUuidChoosed(String bankUuid);

    String getBankUuidChoosed();

    void removeTouchId();

    void setLoginRequired(boolean isRequired);

    void setShowCjConsentsinHome(boolean isRequired);

    void setProfilingConsent(boolean allowConsent);

    void setMarketingConsent(boolean allowConsent);

    void setDataToThirdPartiesConsent(boolean allowConsent);

    Task<?> doModifyPinTask(OnCompleteResultListener<VerifyPinData> mListener, String oldPin, String newPin);

    Task<?> doMigratePinTask(OnCompleteResultListener<VerifyPinData> mListener, String oldPin);


    boolean isDeviceOnline(Context context);

    void doSubscribePush(OnCompleteResultListener<Void> mListener, String token, String sessionToken);

    void setAuthenticationResultListener(@NonNull BCMAuthenticationResultListener listener);

    Task<?> doUserMonitoring(OnCompleteResultListener<Void> listener, String bankUUID, String tag, String event, String note);

    void doSetCustomerJourneyTag(OnCompleteResultListener<Void> listener, CustomerJourneyTag tag);

    void doSetCustomerJourneyConsents(@NonNull Activity activity, @NonNull OnCompleteResultListener<Void> listener, boolean allowProfiling, boolean allowMarketing, boolean allowDataToThirdParties, String sessionToken);

    void setCustomerJourneyEventListener(@NonNull BCMFullStackCJEventListener listener);

    void doGetStoreLocator(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopList> listener, BcmLocation location, int page, String sessionToken);

    void doGetStoreLocatorSearchOnlineRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopList> listener, String shopName, String categoryUuid, int page, String sessionToken);

    void doGetStoreLocatorSearchPhysicalRequest(@NonNull Activity activity, @NonNull OnCompleteResultListener<ShopList> listener, String shopName,BcmLocation location, int page, String sessionToken);

    void doGetStoreLocatorCategories(@NonNull Activity activity, @NonNull OnCompleteResultListener<List<ShopCategory>> listener, String sessionToken);

    //new API with calleble

    CallableVoid updateBanksConfigurationFile();

    CallableVoid userMonitoring(String bankUUID, String tag, String event, String note);

    Callable<VerifyActionCodeData> verifyActivationCode(String activationCode);

    CallableVoid resendOtpCode(VerifyActionCodeData verifyActionCodeData);

    Callable<VerifyOtpCodeData> verifyOtpCode(VerifyActionCodeData verifyActionCodeData, String otp);

    Callable<BiometricEnrollData> biometricEnroll(String token, InstrumentData instrumentData);

    CallableVoid storeBiometricEnrollData(FragmentActivity activity, BiometricPrompt.PromptInfo biometric, BiometricEnrollData biometricEnrollData, int requestCode, ActivityResultLauncher<Intent> activityResultLauncher);

    CallableVoid initSdk(Activity activity, String abiCode, String groupCode, String phoneNumber, String phonePrefix, String iban);

    Callable<VerifyPinData> verifyPin(AppAuthenticationInterface authenticationInterface);

    boolean isDeviceSecured();

    boolean isBiometricConfigured();


    class Factory {
        public static BancomatPayApiInterface getInstance() {
            return BancomatPayApi.getInstance();
        }
    }

}
