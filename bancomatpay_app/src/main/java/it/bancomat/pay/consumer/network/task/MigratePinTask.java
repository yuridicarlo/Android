package it.bancomat.pay.consumer.network.task;

import com.google.gson.Gson;

import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.bancomat.pay.consumer.network.AppCmd;
import it.bancomat.pay.consumer.network.NetworkRefreshListener;
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
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.ObserverSingleCustom;
import it.bancomatpay.sdk.manager.task.OnCompleteResultListener;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;

public class MigratePinTask extends PinCryptedTask<VerifyPinData> {

    private String oldPin, newPin;

    public MigratePinTask(OnCompleteResultListener mListener, String oldPin) {
        super(mListener);
        this.authenticationInterface = new AuthenticationData(oldPin);
        this.oldPin = oldPin;
        this.newPin = UUID.randomUUID().toString();
        CustomLogger.d(TAG, "new Random pin " + newPin);;

    }

    @Override
    protected void start() {
        DtoModifyPinRequestUnencrypted dtoRequest = new DtoModifyPinRequestUnencrypted();
        dtoRequest.setNewPin(newPin);

        Single.fromCallable(new DtoPinCryptedInteractor<>(authenticationInterface, dtoRequest))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<DtoPinCrypted>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull DtoPinCrypted dtoPinCrypted) {
                        DtoModifyPinRequest req = new DtoModifyPinRequest();
                        req.setDtoPinCrypted(dtoPinCrypted);

                        Single.fromCallable(new AppHandleRequestInteractor<>(DtoModifyPinResponse.class, req, AppCmd.MODIFY_PIN_V2, getJsessionClient()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new ObserverSingleCustom<>(l));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<Void> resultError = new Result<>();
                        resultError.setStatusCodeMessage(e.getMessage());
                        sendCompletition(resultError);
                        CustomLogger.e(TAG, "Error in DtoPinCryptedInteractor: " + e.getMessage());
                    }
                });
    }

    OnNetworkCompleteListener<DtoAppResponse<DtoModifyPinResponse>> l = new NetworkRefreshListener<DtoModifyPinResponse>(this) {

        @Override
        protected void manageComplete(DtoAppResponse<DtoModifyPinResponse> response) {
            CustomLogger.d(TAG, response.toString());
            Result<VerifyPinData> r = new Result<>();
            prepareResult(r, response);
            VerifyPinData verifyPinData = new VerifyPinData();
            r.setResult(verifyPinData);
            if (r.isSuccess()) {
                AppBancomatDataManager.getInstance().putTokens(response.getRes().getTokens().getAuthorizationToken().getToken(), response.getRes().getTokens().getRefreshToken().getToken());
                Pskc pskc = PSKCMapper.buildPskcV2(response.getRes().getPskc());
                AppBancomatDataManager.getInstance().putPskc(pskc);
                PSKCManager.getInstance().setPskc(pskc);
                AuthenticationData authenticationData = new AuthenticationData(newPin);
                FingerprintData fingerprintData = new FingerprintData();
                fingerprintData.setSeed(authenticationData.getSeed());
                fingerprintData.setHmacKey(authenticationData.getHmacKey());
                Gson gson = new Gson();
                String jsonFingerprint = gson.toJson(fingerprintData, FingerprintData.class);
                verifyPinData.setLastAttempts(-1);
                verifyPinData.setFingerprintData(jsonFingerprint.getBytes());


            } else {
                try {
                    verifyPinData.setLastAttempts(Integer.parseInt(response.getRes().getLastAttempts()));
                } catch (Exception e) {
                    verifyPinData.setLastAttempts(-1);
                }
            }
            sendCompletition(r);
        }
    };

}
