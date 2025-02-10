package it.bancomat.pay.consumer.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.nio.charset.StandardCharsets;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import it.bancomat.pay.consumer.activation.databank.DataBank;
import it.bancomat.pay.consumer.init.BaseInitActivity;
import it.bancomat.pay.consumer.init.model.CodeObservable;
import it.bancomat.pay.consumer.init.model.ErrorState;
import it.bancomat.pay.consumer.network.BancomatPayApiInterface;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.network.dto.VerifyActionCodeData;
import it.bancomat.pay.consumer.network.dto.VerifyOtpCodeData;
import it.bancomat.pay.consumer.widget.KeyboardCodeObservable;
import it.bancomatpay.sdk.manager.lifecycle.LiveCompletable;
import it.bancomatpay.sdk.manager.lifecycle.LiveSingle;
import it.bancomatpay.sdk.manager.lifecycle.LiveUtil;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveCompletable;
import it.bancomatpay.sdk.manager.lifecycle.MutableLiveSingle;
import it.bancomatpay.sdk.manager.model.VoidResponse;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdkui.activities.documents.DocumentDetailActivity;
import it.bancomatpay.sdkui.flowmanager.DocumentsFlowManager;
import it.bancomatpay.sdkui.utilities.CjUtils;

import static it.bancomatpay.sdkui.utilities.CjConstants.KEY_ACTIVATION_AUTHENTICATION_SETTED;

public class InitViewModel extends ViewModel {

    private final static String TAG = InitViewModel.class.getSimpleName();
    private MutableLiveData<Integer> introPageSelected;

    private MutableLiveCompletable updateBanksConfigurationFileResponse;
    private MutableLiveSingle<VerifyActionCodeData> verifyActivationCodeResponse;
    private MutableLiveCompletable resendOtpCodeResponse;
    private MutableLiveSingle<VerifyOtpCodeData> verifyOtpCodeResponse;
    private MutableLiveSingle<BiometricEnrollData> biometricEnrollResponse;
    private MutableLiveCompletable storeBiometricEnrollDataResponse;

    private MutableLiveCompletable initSdkResponse;
    private ErrorState errorState;


    private KeyboardCodeObservable keyboardCodeObservable;

    private DataBank dataBank;
    private InstrumentData instrumentDataSelected;
    private String token;
    private String maskedPhoneNumber;
    private String phoneNumber;
    private String prefix;
    private String activationCode;
    private String iban;
    private String bankUUID;
    private boolean isFromDeepLink;
    private VerifyActionCodeData verifyActionCodeData;
    private BiometricEnrollData biometricEnrollDataValue;

    private CodeObservable observableActivationCode;
    private boolean isSMSReceiverActive;

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    private static final String DATA_BANK = TAG + "_DATA_BANK";
    private static final String TOKEN =  TAG + "_TOKEN";
    private static final String ACTIVATION_CODE =  TAG + "_ACTIVATION_CODE";
    private static final String IS_FROM_DEEP_LINK = TAG +  "_IS_FROM_DEEP_LINK";
    private static final String MASKED_PHONE_NUMBER =  TAG + "_MASKED_PHONE_NUMBER";
    private static final String ERROR_STATE =  TAG + "_ERROR_STATE";
    private static final String PREFIX =  TAG + "_PREFIX";
    private static final String PHONE_NUMBER =  TAG + "_PHONE_NUMBER";
    private static final String IS_SMS_RECEIVER_ACTIVE =  TAG + "_IS_SMS_RECEIVER_ACTIVE";

    public InitViewModel() {
        reset();
    }

    public void reset(){
        introPageSelected = new MutableLiveData<>(0);
        updateBanksConfigurationFileResponse = new MutableLiveCompletable();
        verifyActivationCodeResponse = new MutableLiveSingle<>();
        observableActivationCode = new CodeObservable(16);
        resendOtpCodeResponse = new MutableLiveCompletable();
        verifyOtpCodeResponse = new MutableLiveSingle<>();
        biometricEnrollResponse = new MutableLiveSingle<>();
        initSdkResponse = new MutableLiveCompletable();
        storeBiometricEnrollDataResponse = new MutableLiveCompletable();
        keyboardCodeObservable = new KeyboardCodeObservable(6);
        dataBank = null;
        token = null;
        activationCode = null;
        isFromDeepLink = false;
        maskedPhoneNumber = null;
        errorState = null;
        prefix = null;
        phoneNumber = null;
        isSMSReceiverActive = false;
    }

    public void onSaveInstanceState(Bundle outstate){
        CustomLogger.d(TAG, "onSaveInstanceState");
        outstate.putSerializable(DATA_BANK, dataBank);
        outstate.putSerializable(TOKEN, token);
        outstate.putSerializable(ACTIVATION_CODE, activationCode);
        outstate.putSerializable(IS_FROM_DEEP_LINK, isFromDeepLink);
        outstate.putSerializable(MASKED_PHONE_NUMBER, maskedPhoneNumber);
        outstate.putSerializable(ERROR_STATE, errorState);
        outstate.putSerializable(PREFIX, prefix);
        outstate.putSerializable(PHONE_NUMBER, phoneNumber);
        outstate.putSerializable(IS_SMS_RECEIVER_ACTIVE, isSMSReceiverActive);
    }

    public void restoreSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            CustomLogger.d(TAG, "restoreSaveInstanceState");
            dataBank = (DataBank) savedInstanceState.getSerializable(DATA_BANK);
            token = (String) savedInstanceState.getSerializable(TOKEN);
            activationCode = (String) savedInstanceState.getSerializable(ACTIVATION_CODE);
            isFromDeepLink = (Boolean) savedInstanceState.getSerializable(IS_FROM_DEEP_LINK);
            maskedPhoneNumber = (String) savedInstanceState.getSerializable(MASKED_PHONE_NUMBER);
            errorState = (ErrorState) savedInstanceState.getSerializable(ERROR_STATE);
            prefix = (String) savedInstanceState.getSerializable(PREFIX);
            phoneNumber = (String) savedInstanceState.getSerializable(PHONE_NUMBER);
            isSMSReceiverActive = (Boolean) savedInstanceState.getSerializable(IS_SMS_RECEIVER_ACTIVE);
        }
    }

    public KeyboardCodeObservable getKeyboardCodeObservable() {
        return keyboardCodeObservable;
    }

    public void setInstrumentDataSelected(InstrumentData instrumentDataSelected) {
        this.instrumentDataSelected = instrumentDataSelected;
        this.iban = instrumentDataSelected.getCipheredIban();
    }

    public DataBank getDataBank() {
        return dataBank;
    }


    public String getBankUUID(){
        return bankUUID;
    }

    public void onIntroPageSelected(int index){
        introPageSelected.setValue(index);
    }

    public LiveData<Integer> getIntroPageSelected(){
        return introPageSelected;
    }

    public void checkBanksFileUpdate() {
        if(updateBanksConfigurationFileResponse.isNotPending()) {
            LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().updateBanksConfigurationFile())
                    .onErrorReturnItem(VoidResponse.VALUE)
                    .subscribe(LiveUtil.getWrapper(updateBanksConfigurationFileResponse));

        }
    }


    public void setActivationCodeFromDeepLink(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean setActivationCodeFromQrCode(String activationCode) {
        String rawCode;
        try {
            byte[] data = Base64.decode(activationCode, Base64.DEFAULT);
            rawCode = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            rawCode = "";
        }
        this.activationCode = activationCode;

        if (!TextUtils.isEmpty(rawCode) && rawCode.startsWith("ZPSmW6Pbp4kM?")) {
            CustomLogger.d(TAG, "raw Code: " + rawCode);
            String[] list = rawCode.split("\\?");
            if (list.length == 2) {
                this.activationCode = list[1];
                return true;
            }
        }
        return false;
    }

    public void setActivationCodeFromManual(String activationCode) {
        this.activationCode = new String(Base64.encode(activationCode.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));
    }

    public String getActivationCode() {
        return activationCode;
    }

    public String getActivationCodeDecoded() {
        return new String(Base64.decode(activationCode.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));
    }

    public boolean isFromDeepLink() {
        return isFromDeepLink;
    }

    public void setFromDeepLink(boolean fromDeepLink) {
        isFromDeepLink = fromDeepLink;
    }

    public CodeObservable getObservableActivationCode() {
        return observableActivationCode;
    }

    public void verifyActivationCode(){
        if(verifyActivationCodeResponse.isNotPending()) {

            LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().verifyActivationCode(activationCode))
                    .doOnSuccess(v -> {
                        verifyActionCodeData = v;
                        maskedPhoneNumber = verifyActionCodeData.getMaskedMsisdn();
                        phoneNumber = verifyActionCodeData.getMsisdn().replace(verifyActionCodeData.getMsisdnAreaCode(), "");
                        bankUUID = verifyActionCodeData.getBankUUID();
                        prefix = verifyActionCodeData.getMsisdnAreaCode();
                    })
                    .subscribe(LiveUtil.getWrapper(verifyActivationCodeResponse));

        }
    }

    public LiveSingle<VerifyActionCodeData> getVerifyActivationCodeResponse() {
        return verifyActivationCodeResponse;
    }

    public String getMaskedPhoneNumber() {
        return maskedPhoneNumber;
    }

    public void resendOtpCode(){
        if(resendOtpCodeResponse.isNotPending()) {
            LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().resendOtpCode(verifyActionCodeData))
                    .subscribe(LiveUtil.getWrapper(resendOtpCodeResponse));

        }
    }

    public void verifyOtpCode(){
        if(verifyOtpCodeResponse.isNotPending()) {
            LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().verifyOtpCode(verifyActionCodeData, keyboardCodeObservable.getCode()))
                    .doOnSuccess(verifyOtpCodeData -> {
                        token = verifyOtpCodeData.getToken();
                        dataBank = verifyOtpCodeData.getDataBank();
                    })
                    .subscribe(LiveUtil.getWrapper(verifyOtpCodeResponse));

        }

    }


    public void biometricEnroll(Context context){
        if(biometricEnrollResponse.isNotPending()) {
            LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().biometricEnroll(token, instrumentDataSelected))
                    .doOnSuccess(biometricEnrollData -> {
                        biometricEnrollDataValue = biometricEnrollData;
                        CjUtils.getInstance().sendCustomerJourneyTagEvent(context, KEY_ACTIVATION_AUTHENTICATION_SETTED, null, true);

                    })
                    .doOnError(throwable -> errorState = ErrorState.RETRY_BIOMETRIC_ENROLL)
                    .subscribe(LiveUtil.getWrapper(biometricEnrollResponse));

        }
    }

    public void storeBiometricEnrollData(FragmentActivity activity, BiometricPrompt.PromptInfo biometric){
        if(storeBiometricEnrollDataResponse.isNotPending()) {
            if (activity instanceof BaseInitActivity) {
                BaseInitActivity activityBase = (BaseInitActivity) activity;
                LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().storeBiometricEnrollData(activity, biometric, biometricEnrollDataValue, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS, activityBase.getActivityResultLauncherDeviceCredentials()))
                        .doOnError(throwable -> errorState = ErrorState.RETRY_STORE_BIOMETRIC_ENROLL_DATA)
                        .subscribe(LiveUtil.getWrapper(storeBiometricEnrollDataResponse));
            }
        }
    }

    public void initSdk(Activity activity){
        if(initSdkResponse.isNotPending()) {
            LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().initSdk(activity, biometricEnrollDataValue.getAbiCode(), biometricEnrollDataValue.getGroupCode(), phoneNumber, prefix, iban))
                    .doOnError(throwable -> errorState = ErrorState.RETRY_INIT_SDK)
                    .subscribe(LiveUtil.getWrapper(initSdkResponse));

        }

    }

    public LiveSingle<BiometricEnrollData> biometricEnrollResponse() {
        return biometricEnrollResponse;
    }

    public LiveCompletable resendOtpCodeResponse(){
        return resendOtpCodeResponse;
    }

    public LiveSingle<VerifyOtpCodeData> verifyOtpCodeResponse(){
        return verifyOtpCodeResponse;
    }

    public LiveCompletable storeBiometricEnrollDataResponse(){
        return storeBiometricEnrollDataResponse;
    }


    public LiveCompletable initSdkResponse(){
        return initSdkResponse;
    }

    public ErrorState getErrorState() {
        return errorState;
    }

    public void userMonitoring(String bankUUID, String tag, String event, String note){
        LiveUtil.fromCallable(BancomatPayApiInterface.Factory.getInstance().userMonitoring(bankUUID, tag, event, note))
                .onErrorReturnItem(VoidResponse.VALUE)
                .subscribe(new SingleObserver<VoidResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull VoidResponse voidResponse) {
                        CustomLogger.d(TAG, "userMonitoring success");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        CustomLogger.d(TAG, "userMonitoring error");

                    }
                });
    }

    public boolean isSMSReceiverActive() {
        return isSMSReceiverActive;
    }

    public void setSMSReceiverActive(boolean SMSReceiverActive) {
        isSMSReceiverActive = SMSReceiverActive;
    }
}
