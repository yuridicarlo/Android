package it.bancomat.pay.consumer.network.callable;

import com.google.gson.Gson;

import java.util.UUID;
import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.AuthenticationData;
import it.bancomat.pay.consumer.network.dto.VerifyPinData;
import it.bancomat.pay.consumer.network.dto.request.DtoModifyPinRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoModifyPinRequestUnencrypted;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.dto.response.DtoModifyPinResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.interactor.DtoPinCryptedInteractor;
import it.bancomat.pay.consumer.network.totp.PSKCManager;
import it.bancomat.pay.consumer.network.totp.PSKCMapper;
import it.bancomat.pay.consumer.storage.AppBancomatDataManager;
import it.bancomat.pay.consumer.storage.model.FingerprintData;
import it.bancomat.pay.consumer.storage.model.Pskc;
import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.ExtendedTask;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class MigratePin implements Callable<VerifyPinData> {

    private final AppAuthenticationInterface authenticationInterface;
    private String newPin;
    private final static String TAG = MigratePin.class.getSimpleName();

    public MigratePin(String oldPin) {

        this.authenticationInterface = new AuthenticationData(oldPin);

        this.newPin = UUID.randomUUID().toString();

        CustomLogger.d(TAG, "new Random pin " + newPin);;

    }

    @Override
    public VerifyPinData call() throws Exception {

        DtoModifyPinRequestUnencrypted dtoRequest = new DtoModifyPinRequestUnencrypted();
        dtoRequest.setNewPin(newPin);
        DtoPinCrypted dtoPinCrypted = new DtoPinCryptedInteractor<>(authenticationInterface, dtoRequest).call();

        DtoModifyPinRequest req = new DtoModifyPinRequest();
        req.setDtoPinCrypted(dtoPinCrypted);

        AppHandleRequestInteractor<DtoModifyPinRequest, DtoModifyPinResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoModifyPinResponse.class, req, AppCmd.MODIFY_PIN_V2, ExtendedTask.getJsessionClient());

        VerifyPinData verifyPinData = new VerifyPinData();

        try{

            Result<DtoModifyPinResponse> result = NetworkUtil.getResult(requestInteractor);
            AppBancomatDataManager.getInstance().putTokens(result.getResult().getTokens().getAuthorizationToken().getToken(), result.getResult().getTokens().getRefreshToken().getToken());
            Pskc pskc = PSKCMapper.buildPskcV2(result.getResult().getPskc());
            PSKCManager.getInstance().setPskc(pskc);
            AppBancomatDataManager.getInstance().putPskc(pskc);
            AuthenticationData authenticationData = new AuthenticationData(newPin);
            FingerprintData fingerprintData = new FingerprintData();
            fingerprintData.setSeed(authenticationData.getSeed());
            fingerprintData.setHmacKey(authenticationData.getHmacKey());
            Gson gson = new Gson();
            String jsonFingerprint = gson.toJson(fingerprintData, FingerprintData.class);
            verifyPinData.setLastAttempts(-1);
            verifyPinData.setFingerprintData(jsonFingerprint.getBytes());


        } catch (ServerException serverException){
            try {
                Result<DtoModifyPinResponse> result = serverException.getResult();
                verifyPinData.setLastAttempts(Integer.parseInt(result.getResult().getLastAttempts()));
            } catch (Exception e) {
                verifyPinData.setLastAttempts(-1);
            }


        }
        return verifyPinData;
    }

}
