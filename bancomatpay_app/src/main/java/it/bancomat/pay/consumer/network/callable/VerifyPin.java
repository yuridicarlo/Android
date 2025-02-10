package it.bancomat.pay.consumer.network.callable;

import com.google.gson.Gson;

import java.util.concurrent.Callable;

import it.bancomat.pay.consumer.exception.ServerException;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkUtil;
import it.bancomat.pay.consumer.network.dto.AppAuthenticationInterface;
import it.bancomat.pay.consumer.network.dto.PinOperationType;
import it.bancomat.pay.consumer.network.dto.VerifyPinData;
import it.bancomat.pay.consumer.network.dto.request.DtoPinCrypted;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyPinRequest;
import it.bancomat.pay.consumer.network.dto.request.DtoVerifyPinRequestUnencrypted;
import it.bancomat.pay.consumer.network.dto.response.DtoModifyPinResponse;
import it.bancomat.pay.consumer.network.dto.response.DtoVerifyPinResponse;
import it.bancomat.pay.consumer.network.interactor.AppHandleRequestInteractor;
import it.bancomat.pay.consumer.network.interactor.DtoPinCryptedInteractor;
import it.bancomat.pay.consumer.storage.model.FingerprintData;
import it.bancomatpay.sdk.Result;

import static it.bancomatpay.sdk.manager.task.ExtendedTask.getJsessionClient;

public class VerifyPin implements Callable<VerifyPinData> {

    private final static String TAG = VerifyPin.class.getSimpleName();
    AppAuthenticationInterface authenticationInterface;

    public VerifyPin(AppAuthenticationInterface authenticationInterface) {
        this.authenticationInterface = authenticationInterface;
    }

    @Override
    public VerifyPinData call() throws Exception {
        DtoVerifyPinRequestUnencrypted dtoVerifyPinRequestUnencrypted = new DtoVerifyPinRequestUnencrypted();
        dtoVerifyPinRequestUnencrypted.setOperation(PinOperationType.VERIFY_PIN);
        dtoVerifyPinRequestUnencrypted.setTimestamp(Long.toString(System.currentTimeMillis()));

        DtoPinCrypted dtoPinCrypted = new DtoPinCryptedInteractor<>(authenticationInterface, dtoVerifyPinRequestUnencrypted).call();

        DtoVerifyPinRequest req = new DtoVerifyPinRequest();
        req.setDtoPinCrypted(dtoPinCrypted);

        AppHandleRequestInteractor<DtoVerifyPinRequest, DtoVerifyPinResponse> requestInteractor = new AppHandleRequestInteractor<>(DtoVerifyPinResponse.class, req, AppCmd.VERIFY_PIN, getJsessionClient());

        VerifyPinData verifyPinData = new VerifyPinData();

        try{

            NetworkUtil.getResult(requestInteractor);
            FingerprintData fingerprintData = new FingerprintData();
            fingerprintData.setSeed(authenticationInterface.getSeed());
            fingerprintData.setHmacKey(authenticationInterface.getHmacKey());
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
