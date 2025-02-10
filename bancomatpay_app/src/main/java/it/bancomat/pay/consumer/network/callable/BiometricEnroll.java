package it.bancomat.pay.consumer.network.callable;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.UUID;
import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.AuthenticationData;
import it.bancomat.pay.consumer.network.dto.BiometricEnrollData;
import it.bancomat.pay.consumer.network.dto.request.DtoSetPinRequest;
import it.bancomat.pay.consumer.network.dto.response.DtoSetPinResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.totp.PSKCManager;
import it.bancomat.pay.consumer.network.totp.PSKCMapper;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.FingerprintData;
import it.bancomat.pay.consumer.storage.model.Pskc;
import it.bancomatpay.sdk.LoyaltyTokenManager;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.task.model.InstrumentData;

public class BiometricEnroll implements Callable<BiometricEnrollData> {

    private final static String TAG = MigratePin.class.getSimpleName();


    private String token;
    private InstrumentData instrumentData;



    public BiometricEnroll(String token, InstrumentData instrumentData) {
        this.token = token;
        this.instrumentData = instrumentData;
    }

    @Override
    public BiometricEnrollData call() throws Exception {

        String randomPin = UUID.randomUUID().toString();


        DtoSetPinRequest req = new DtoSetPinRequest();
        req.setPin(randomPin);
        req.setToken(token);
        if (instrumentData != null) {
            req.setOutgoingIban(instrumentData.getCipheredIban());
        }

        AppHandleRequestInteractor<DtoSetPinRequest, DtoSetPinResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoSetPinResponse.class, req, AppCmd.SET_PIN_V2, ExtendedTask.getJsessionClient());

        BiometricEnrollData biometricEnrollData = new BiometricEnrollData();

        Result<DtoSetPinResponse> result = NetworkUtil.getResult(requestInteractor);
        AppBancomatDataManager.getInstance().putTokens(result.getResult().getTokens().getAuthorizationToken().getToken(), result.getResult().getTokens().getRefreshToken().getToken());
        Pskc pskc = PSKCMapper.buildPskcV2(result.getResult().getPskc());

        PSKCManager.getInstance().setPskc(pskc);
        AuthenticationData authenticationData = new AuthenticationData(randomPin);
        FingerprintData fingerprintData = new FingerprintData();
        fingerprintData.setSeed(authenticationData.getSeed());
        fingerprintData.setHmacKey(authenticationData.getHmacKey());

        Gson gson = new Gson();
        String jsonFingerprint = gson.toJson(fingerprintData, FingerprintData.class);
        biometricEnrollData.setBiometricData(jsonFingerprint.getBytes());

        biometricEnrollData.setAbiCode(result.getResult().getAbiCode());
        biometricEnrollData.setGroupCode(result.getResult().getGroupCode());
        biometricEnrollData.setPskc(pskc);

        if(!TextUtils.isEmpty(result.getResult().getLoyaltyToken())) {
            LoyaltyTokenManager.getInstance().setLoyaltyToken(result.getResult().getLoyaltyToken());
        }

        return biometricEnrollData;
    }


}
